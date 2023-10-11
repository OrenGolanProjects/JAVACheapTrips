package com.orengolan.CheapTrips.flight;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.orengolan.CheapTrips.city.City;
import com.orengolan.CheapTrips.city.CityController;
import com.orengolan.CheapTrips.city.CityService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class FlightService {

    @Autowired
    private final FlightTicketsRepository flightTicketsRepository;
    private static final Logger logger = Logger.getLogger(CityService.class.getName());

    //TODO Needs to move the variables to Secure storage: API_URL + TOKEN
    private static final String TOKEN = "abd305be5a5291985fedd925eb223ee9";
    private static final String API_URL = "http://api.travelpayouts.com/v1/prices/cheap";
    private final OkHttpClient client = new OkHttpClient();
    private final CityController cityController;
    private final ObjectMapper objectMapper;


    // Constructor of Flight Service.
    public FlightService(FlightTicketsRepository flightTicketsRepository, CityController cityController, ObjectMapper objectMapper){
        this.flightTicketsRepository = flightTicketsRepository;
        this.cityController   = cityController;
        this.objectMapper = objectMapper;
    }

    public String getFlightTickets(String origin_cityIataCode,String destination_cityIataCode,String depart_date,String return_date,String currency) {

        logger.info("FlightService>>findFlight: Start method getFlightTickets.");
        logger.info("Send GET request to get flight ticket list.");

        // Build the Request for execute the Cheapest tickets API.
        String newURL = API_URL+"?currency="+currency+"&origin="+origin_cityIataCode+"&destination="+destination_cityIataCode;
        if (!(depart_date ==null)){
            newURL += "&depart_date="+depart_date;
        }
        if (!(return_date ==null)){
            newURL += "&return_date="+return_date;
        }


        logger.info("getFlightTickets: URL: "+newURL);
        Request request = new Request.Builder()
                .url(newURL)
                .method("GET", null)
                .addHeader("x-access-token", TOKEN)
                .build();

        // TODO before execute the request, need to activate LOCK_DOWN_MECHANISM - LOCK_DOWN_MECHANISM needs to developed.
        // Execute the Request and get the response.
        try (Response response = client.newCall(request).execute()) {
            // If in the response an error or it's not valid returns error.
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            if (response.body() != null) {

                logger.info("Finished get flight tickets.");
                return response.body().string();
            } else {
                throw new IOException("Body of response if empty. " + response);
            }
        }
        catch(IOException e) {
            logger.severe("Error get flight tickets: " + e.getMessage());
            return "Error get flight tickets: " + e.getMessage();
        }
    }


    public Flight findFlight(String origin_cityName,String destination_cityName,String depart_date,String return_date,String currency) throws JsonProcessingException {
        logger.info("FlightService>>findFlight: Start method findFlight!");

        // Method: By the inputs searches if a flight already exists in Redis by redisKey then returns the existed flight.
        // if the flight Not exists: activates the method: getFlightTickets.

        // Init the origin and destination variables.
        Flight.Location origin;
        Flight.Location destination;

        // Search for a CITY entity in MONGODB, if found City for origin and destination - Continue.
        // If CITY did not find - throw relevant error.
        ResponseEntity<?> origin_CityResponse = this.cityController.getSpecificCity(origin_cityName);
        ResponseEntity<?> destination_CityResponse = this.cityController.getSpecificCity(destination_cityName);

        logger.info("Origin City Response Status Code: " + origin_CityResponse.getStatusCodeValue());
        logger.info("Destination City Response Status Code: " + destination_CityResponse.getStatusCodeValue());

        // Origin data set up.
        City origin_City = null;
        if (origin_CityResponse.getStatusCode().is2xxSuccessful()) {
            if (origin_CityResponse.getBody() instanceof City) {
                origin_City = (City) origin_CityResponse.getBody();
            }
            // Create a new instance of origin location.
            origin = new Flight.Location(
                    origin_City.getCityName(),
                    origin_City.getCityIATACode(),
                    origin_City.getCountryIATACode(),
                    origin_City.getLonCoordinates(),
                    origin_City.getLatCoordinates()
                    );
        }
        else {
            logger.warning(origin_CityResponse.getStatusCode().toString());
            throw new RuntimeException (origin_CityResponse.getBody().toString());
        }

        // Destination data set up.
        City destination_City = null;
        if (destination_CityResponse.getStatusCode().is2xxSuccessful()) {

            if (destination_CityResponse.getBody() instanceof City) {
                destination_City = (City) destination_CityResponse.getBody();
            }
            // Create a new instance of destination location.
            destination = new Flight.Location(
                    destination_City.getCityName(), // CityName
                    destination_City.getCityIATACode(), // CityIATACode
                    destination_City.getCountryIATACode(), // CountryIATACode
                    destination_City.getLonCoordinates(), // LonCoordinates
                    destination_City.getLatCoordinates() //LatCoordinates
            );
        }
        else {
            logger.warning(destination_CityResponse.getStatusCode().toString());
            throw new RuntimeException (destination_CityResponse.getBody().toString());
        }

        logger.info("Flight: Creating flight instance..");
        // Create an instance of a flight.
        Flight flight = new Flight(origin,destination);
        logger.info("FlightTicket: Start creating flight ticket list..");


        List<FlightTicket> flightTickets = flightTicketsRepository.getTicketsByParseKey(flight.getOrigin().getCountryIataCode()+"_"+flight.getDestination().getCountryIataCode());
        logger.info("FlightService>>findFlight: flight tickets is: flightTickets == null "+(flightTickets == null));
        logger.info("FlightService>>findFlight: flight tickets is: !flightTickets.isEmpty() "+(!flightTickets.isEmpty()));


        if ( flightTickets == null || flightTickets.isEmpty()){
            logger.warning("FlightTicket: Did not found tickets.");
            // Execute findFlightTicket method to fetch and store the flight data.
            String json = getFlightTickets(
                    flight.getOrigin().getCityIataCode(),
                    flight.getDestination().getCityIataCode(),
                    depart_date,
                    return_date,
                    currency
            );
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode dataNode = rootNode.get("data");
            String cityIATACode = dataNode.fieldNames().next(); // Get the city IATA code

            logger.info("JSON: Start loop.. ");
            // Loop through airport IATA codes
            for (JsonNode cityNode : dataNode) {

                Integer indexTicket = Integer.valueOf(cityNode.fieldNames().next());

                for (JsonNode ticketNode :cityNode){
                    Double price = ticketNode.get("price").asDouble();
                    String airlineIataCode = ticketNode.get("airline").asText();
                    Integer flightNumber = ticketNode.get("flight_number").asInt();

                    String departureAtString = ticketNode.get("departure_at").asText();
                    String returnAtString = ticketNode.get("return_at").asText();
                    String expiresAtString = ticketNode.get("expires_at").asText();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

                    LocalDateTime departureAt = LocalDateTime.parse(departureAtString, formatter);
                    LocalDateTime returnAt = LocalDateTime.parse(returnAtString, formatter);
                    LocalDateTime expiresAt = LocalDateTime.parse(expiresAtString, formatter);

//                    if (expiresAt.isBefore(LocalDateTime.now())){
//                        logger.warning("Found ticket that expire date pass.");

//                        continue;
//                    }
                    logger.warning("Ticket data: price:"+price+" airlineIataCode:"+airlineIataCode+" flightNumber"+flightNumber+" departureAt:"+departureAt+" returnAt:"+returnAt+" expiresAt:"+expiresAt+" indexTicket:"+indexTicket+" currency:"+currency);
                    FlightTicket outTicket = new FlightTicket(
                            price,
                            airlineIataCode,
                            flightNumber,
                            departureAt,
                            returnAt,
                            expiresAt,
                            indexTicket,
                            currency
                    );
                    flightTickets.add(outTicket);
                    try {
                        logger.info("FlightService>>findFlight: saving ticket.");
                        this.flightTicketsRepository.saveFlightTickets(outTicket, flight);
                    }
                    catch (Exception e){
                        logger.severe("FlightService>>findFlight: Failed to save ticket, error msg: "+e.getMessage());
                    }
                }
            }
            logger.info("JSON: Found "+flightTickets.size()+" tickets.");
            flight.setTicketKeys(flightTickets);
        }
        else {
            logger.warning("FlightService>>findFlight: Found "+flightTickets.size()+" tickets.");
            flight.setTicketKeys(flightTickets);
        }
        logger.info("FlightService>>findFlight: End method findFlight!");
        return flight;
    }
}