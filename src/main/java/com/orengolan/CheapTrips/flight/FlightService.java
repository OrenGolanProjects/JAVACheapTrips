package com.orengolan.CheapTrips.flight;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.orengolan.CheapTrips.Service.Redis;
import com.orengolan.CheapTrips.city.*;
import com.orengolan.CheapTrips.util.API;
import com.orengolan.CheapTrips.util.Dates;
import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDateTime;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class FlightService {
    private final Redis redis;
    private static final Logger logger = Logger.getLogger(CityService.class.getName());

    //TODO Needs to move the variables to Secure storage: API_URL + TOKEN
    private static final String TOKEN = "abd305be5a5291985fedd925eb223ee9";
    private final CityController cityController;
    private final ObjectMapper objectMapper;
    private final API api;

    public FlightService( Redis redis, CityController cityController, ObjectMapper objectMapper, API api){
        this.cityController   = cityController;
        this.objectMapper = objectMapper;
        this.redis = redis;
        this.api = api;
    }

    public String getFlightTickets(String origin_cityIataCode,String destination_cityIataCode,String depart_date,String return_date,String currency) {
        logger.info("FlightService>>getFlightTickets: Start method getFlightTickets.");
        logger.info("FlightService>>getFlightTickets: Send GET request to get flight ticket list.");

        // Build the Request for execute the Cheapest tickets API.
        String newURL = "https://api.travelpayouts.com/v1/prices/cheap?origin="+origin_cityIataCode+"&destination="+destination_cityIataCode+"&page=1&currency="+currency;

        if (!(depart_date ==null)){newURL += "&depart_date="+depart_date;}
        if (!(return_date ==null)){newURL += "&return_date="+return_date;}

        Map<String, String> headers = new HashMap<>();
        headers.put("x-access-token", TOKEN);

        logger.info("getFlightTickets: URL: "+newURL);
        return this.api.buildAndExecuteRequest(newURL,headers);
    }

    public Flight findFlight(String origin_cityName,String destination_cityName,String depart_date,String return_date,String currency) throws ParseException, JsonProcessingException, ChangeSetPersister.NotFoundException {

        logger.info("FlightService>>findFlight>>: Start method generateFlight.");
        Flight flight = this.generateFlight(origin_cityName,destination_cityName);

        logger.info("FlightService>>findFlight>>: End method generateFlight.");
        this.generateFlightTickets(flight,depart_date,return_date,currency);

        logger.info("FlightService>>findFlight: End method findFlight!");
        return flight;
    }


    public void saveFlightTickets(@NotNull FlightTicket flightTicket, @NotNull Flight flight ) {
        String generateTicketKey = flightTicket.generateTicketKey(flight.getOrigin().getCityIataCode(),flight.getDestination().getCityIataCode());

        FlightTicket existFlightTicket = (FlightTicket) this.redis.get(generateTicketKey);

        if (existFlightTicket == null )
        {
            this.redis.set(generateTicketKey,flightTicket, flightTicket.generateExpireTime());
        }
    }

    public Set<String> getTicketByParseKey(String partKey){
        return this.redis.getKeys(partKey);
    }

    @NotNull
    private List<FlightTicket> getTicketsByFlight(@NotNull Flight flight) {
        String partKey = flight.getOrigin().getCountryIataCode()+"_"+flight.getDestination().getCountryIataCode(); // Create the partial value to search tickets.
        Set<String> keys = this.redis.getKeys(partKey);  // Use your custom method to get keys
        List<FlightTicket> tickets = new ArrayList<>(); // initialize list of flight tickets

        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                Object value = redis.get(key);

                FlightTicket flightTicket;
                if (value instanceof FlightTicket) {
                    flightTicket = (FlightTicket) value;
                } else {
                    continue;
                }
                tickets.add(flightTicket);
            }
        }
        return tickets;
    }

    @NotNull
    private Flight generateFlight(String origin_cityName, String destination_cityName) {

        ResponseEntity<?> origin_CityResponse = this.cityController.getSpecificCity(origin_cityName);
        ResponseEntity<?> destination_CityResponse = this.cityController.getSpecificCity(destination_cityName);

        logger.info("FlightService>>generateFlight: Origin City Response Status Code: " + origin_CityResponse.getStatusCodeValue());
        logger.info("FlightService>>generateFlight: Destination City Response Status Code: " + destination_CityResponse.getStatusCodeValue());

        City origin_City = (City) origin_CityResponse.getBody(); // Extract City instance.
        City destination_City = (City) destination_CityResponse.getBody(); // Extract City instance.

        Flight.Location origin = new Flight.Location(
                Objects.requireNonNull(origin_City).getCityName(),  // CityName
                origin_City.getCityIATACode(),      // CityIATACode
                origin_City.getCountryIATACode(),   // CountryIATACode
                origin_City.getLonCoordinates(),    // LonCoordinates
                origin_City.getLatCoordinates()     // LatCoordinates
        );
        // Create a new instance of destination location.
        Flight.Location destination = new Flight.Location(
                Objects.requireNonNull(destination_City).getCityName(), // CityName
                destination_City.getCityIATACode(), // CityIATACode
                destination_City.getCountryIATACode(), // CountryIATACode
                destination_City.getLonCoordinates(), // LonCoordinates
                destination_City.getLatCoordinates() //LatCoordinates
        );
        return new Flight(origin,destination);
    }

    private void generateFlightTickets(Flight flight,String depart_date,String return_date,String currency) throws JsonProcessingException, ParseException {
        logger.info("FlightService>>generateFlightTickets: Start method getTicketsByFlight.");
        List<FlightTicket> flightTicketsList = this.getTicketsByFlight(flight);
        logger.info("FlightService>>generateFlightTickets: End method getTicketsByFlight.");

        if (flightTicketsList.isEmpty()){
            logger.warning("FlightService>>generateFlightTickets: Start method generateFlightTickets.");
            String json = getFlightTickets(flight.getOrigin().getCityIataCode(),flight.getDestination().getCityIataCode(),depart_date,return_date,currency);
            logger.warning("FlightService>>generateFlightTickets: Flight tickets JSON data: "+json);

            JsonNode rootNode = this.objectMapper.readTree(json);
            JsonNode dataNode = rootNode.get("data");
            if(dataNode.isEmpty()){
                throw new IllegalArgumentException("Did not found flight tickets for flight: "+flight);
            }

            // Loop through airport IATA codes
            for (JsonNode airportNode : dataNode) {
                String airportIATACode = airportNode.fieldNames().next();
                JsonNode flightData = airportNode.get(airportIATACode);

                for (JsonNode flightItem : flightData) {
                    int index = Integer.parseInt(flightItem.fieldNames().next());
                    JsonNode flightDetails = flightItem.get(index);

                    Double price = flightDetails.get("price").asDouble();
                    String airlineIataCode = flightItem.get("airline").asText();
                    Integer flightNumber = flightItem.get("flight_number").asInt();

                    LocalDateTime departureAt = Dates.parseStringToLocalDateTime(flightItem.get("departure_at").asText());
                    LocalDateTime returnAt = Dates.parseStringToLocalDateTime(flightItem.get("return_at").asText());
                    LocalDateTime expiresAt = Dates.parseStringToLocalDateTime(flightItem.get("expires_at").asText());

                    // Create an instance of flight ticket.
                    FlightTicket outTicket = new FlightTicket(price,airlineIataCode,flightNumber,departureAt,returnAt,expiresAt,index,currency);
                    flightTicketsList.add(outTicket);
                    this.saveFlightTickets(outTicket, flight);
                }
            }
        }
        else {
            logger.warning("FlightService>>findFlight: Found flight tickets, Count: "+flightTicketsList.size()+" tickets.");
            flight.setTicketKeys(flightTicketsList);
        }
    }


}