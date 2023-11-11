package com.orengolan.cheaptrips.flight;

import com.fasterxml.jackson.databind.JsonNode;
import com.orengolan.cheaptrips.airline.AirlineService;
import com.orengolan.cheaptrips.config.ConfigLoader;
import com.orengolan.cheaptrips.service.Redis;
import com.orengolan.cheaptrips.city.*;
import com.orengolan.cheaptrips.util.API;
import com.orengolan.cheaptrips.util.Dates;
import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDateTime;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.Assert;


@Service
public class FlightService {
    private final Redis redis;
    private static final Logger logger = Logger.getLogger(CityService.class.getName());
    private final CityController cityController;
    private final ObjectMapper objectMapper;
    private final API api;
    JSONObject config = ConfigLoader.loadConfig();
    private final AirlineService airlineService;

    public FlightService( Redis redis, CityController cityController, ObjectMapper objectMapper, API api,AirlineService airlineService){
        this.cityController   = cityController;
        this.objectMapper = objectMapper;
        this.redis = redis;
        this.api = api;
        this.airlineService = airlineService;
    }

    public String getFlightTickets(String origin_cityIataCode, String destination_cityIataCode) throws IOException {
        logger.info("FlightService>>  getFlightTickets: Start method.");
        logger.info("FlightService>>  getFlightTickets: Send GET request to get flight ticket list.");

        // Get secure values.
        String ENDPOINT_URL = (String) config.get("flight_ENDPOINT");
        String TOKEN = (String) config.get("flight_TOKEN");

        // Build the Request for execute the Cheapest tickets API.
        String newURL = ENDPOINT_URL+origin_cityIataCode+"&destination="+destination_cityIataCode+"&currency=USD";

        Map<String, String> headers = new HashMap<>();
        headers.put("x-access-token", TOKEN);

        logger.info("FlightService>>  getFlightTickets: URL: "+newURL);
        logger.info("FlightService>>  getFlightTickets: End method.");
        return this.api.buildAndExecuteRequest(newURL,headers);
    }

    public Flight findFlight(String origin_cityName,String destination_cityName) throws ParseException, IOException {

        logger.info("FlightService>>  findFlight: Start method.");
        Flight flight = this.generateFlight(origin_cityName,destination_cityName);

        this.generateFlightTickets(flight);

        logger.info("FlightService>>  findFlight: End method.");
        return flight;
    }


    public void saveFlightTickets(@NotNull FlightTicket flightTicket, @NotNull Flight flight ) {
        logger.info("FlightService>>  saveFlightTickets: Start method.");
        String generateTicketKey = flightTicket.generateTicketKey(flight.getOrigin().getCityIataCode(),flight.getDestination().getCityIataCode());

        FlightTicket existFlightTicket = (FlightTicket) this.redis.get(generateTicketKey);

        if (existFlightTicket == null )
        {
            this.redis.set(generateTicketKey,flightTicket, flightTicket.generateExpireTime());
            logger.info("FlightService>>  saveFlightTickets: Successfully saved.");
        }
        logger.info("FlightService>>  saveFlightTickets: End method.");
    }

    public Set<String> getTicketByParseKey(String partKey){
        logger.info("FlightService>>  getTicketByParseKey: Start method.");
        Set<String> result = this.redis.getKeys(partKey);
        Assert.notNull(result, "Did not found ticket for parse key:" + partKey);
        logger.info("FlightService>>  getTicketByParseKey: End method.");
        return result;
    }

    @NotNull
    private List<FlightTicket> getTicketsByFlight(@NotNull Flight flight) {
        logger.info("FlightService>>  getTicketsByFlight: Start method.");

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
        logger.info("FlightService>>  getTicketsByFlight: Found: "+tickets.size()+" flight tickets.");
        logger.info("FlightService>>  getTicketsByFlight: End method.");
        return tickets;
    }

    @NotNull
    private Flight generateFlight(String origin_cityName, String destination_cityName) {

        logger.info("FlightService>>  generateFlight: Start method.");
        ResponseEntity<?> origin_CityResponse = this.cityController.getSpecificCity(origin_cityName);
        ResponseEntity<?> destination_CityResponse = this.cityController.getSpecificCity(destination_cityName);

        logger.info("FlightService>>  generateFlight: Origin City Response Status Code: " + origin_CityResponse.getStatusCodeValue());
        logger.info("FlightService>>  generateFlight: Destination City Response Status Code: " + destination_CityResponse.getStatusCodeValue());

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
        logger.info("FlightService>>  generateFlight: End method.");
        return new Flight(origin,destination);
    }

    private void generateFlightTickets(Flight flight) throws IOException, ParseException {
        logger.info("FlightService>>  generateFlightTickets: Start method.");

        List<FlightTicket> flightTicketsList = this.getTicketsByFlight(flight);

        if (flightTicketsList.isEmpty()){

            String json = getFlightTickets(flight.getOrigin().getCityIataCode(),flight.getDestination().getCityIataCode());

            JsonNode rootNode = this.objectMapper.readTree(json);
            JsonNode dataNode = rootNode.get("data");
            if(dataNode.isEmpty()){
                throw new IllegalArgumentException("Did not found flight tickets for "+flight);
            }

            String index = dataNode.fieldNames().next();
            // Loop through airport IATA codes
            for (JsonNode ticketNode : dataNode) {

                Double price = ticketNode.get("price").asDouble();
                String airlineIataCode = ticketNode.get("airline").asText();
                Integer flightNumber = ticketNode.get("flight_number").asInt();

                LocalDateTime departureAt = Dates.parseStringToLocalDateTime(ticketNode.get("departure_at").asText());
                LocalDateTime returnAt = Dates.parseStringToLocalDateTime(ticketNode.get("return_at").asText());
                LocalDateTime expiresAt = Dates.parseStringToLocalDateTime(ticketNode.get("expires_at").asText());

                // Create an instance of flight ticket.
                FlightTicket outTicket = new FlightTicket(price,this.airlineService.getSpecificAirlines(airlineIataCode),flightNumber,departureAt,returnAt,expiresAt,index);
                flightTicketsList.add(outTicket);
                this.saveFlightTickets(outTicket, flight);
            }

            flight.setTicketKeys(flightTicketsList);
        }
        else {
            logger.info("FlightService>>  generateFlightTickets: Found flight tickets, Count: "+flightTicketsList.size()+" tickets.");
            flight.setTicketKeys(flightTicketsList);
        }

        logger.info("FlightService>>  generateFlightTickets: End method.");
    }
}