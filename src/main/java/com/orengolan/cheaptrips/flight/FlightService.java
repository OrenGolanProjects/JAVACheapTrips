package com.orengolan.cheaptrips.flight;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.orengolan.cheaptrips.airline.Airline;
import com.orengolan.cheaptrips.airline.AirlineService;
import com.orengolan.cheaptrips.countries.Country;
import com.orengolan.cheaptrips.countries.CountryService;
import com.orengolan.cheaptrips.service.Redis;
import com.orengolan.cheaptrips.city.*;
import com.orengolan.cheaptrips.util.API;
import com.orengolan.cheaptrips.util.Dates;
import io.github.cdimascio.dotenv.Dotenv;
import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDateTime;
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
    private final ObjectMapper objectMapper;
    private final API api;
    private final AirlineService airlineService;
    private final CityService cityService;
    private final CountryService countryService;
    private final String flightmonthlyENDPOINT;
    private final String flightcheapENDPOINT;
    private final String flightToken;

    public FlightService(Dotenv dotenv, Redis redis, ObjectMapper objectMapper, API api, AirlineService airlineService, CityService cityService, CountryService countryService){
        this.objectMapper = objectMapper;
        this.redis = redis;
        this.api = api;
        this.airlineService = airlineService;
        this.cityService = cityService;
        this.countryService = countryService;
        this.flightmonthlyENDPOINT = dotenv.get("flight_monthlyENDPOINT");
        this.flightcheapENDPOINT = dotenv.get("flight_cheapENDPOINT");
        this.flightToken = dotenv.get("flight_TOKEN");
    }

    public String getFlightTickets(String origin_cityIataCode, String destination_cityIataCode,String departure_at, String return_at) throws IOException {
        logger.info("FlightService>>  getFlightTickets: Start method.");
        logger.info("FlightService>>  getFlightTickets: Send GET request to get flight ticket list.");

        String newURL;
        if(departure_at !=null && return_at !=null){
            newURL = flightcheapENDPOINT+origin_cityIataCode+"&destination="+destination_cityIataCode+"&currency=USD";
        }
        else {
            // Build the Request for execute the Cheapest tickets API.
            newURL = flightmonthlyENDPOINT+origin_cityIataCode+"&destination="+destination_cityIataCode+"&depart_date="+departure_at+"&return_date="+return_at+"&currency=USD";
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("x-access-token", flightToken);

        logger.info("FlightService>>  getFlightTickets: URL: "+newURL);
        logger.info("FlightService>>  getFlightTickets: End method.");
        return this.api.buildAndExecuteRequest(newURL,headers);
    }

    public Flight findFlight(String origin_cityIATACode,String destination_cityIATACode,String departure_at, String return_at) throws ParseException, IOException {


        logger.info("FlightService>>  findFlight: Start method.");
        Flight flight = this.generateFlight(origin_cityIATACode,destination_cityIATACode);
        if(departure_at !=null && return_at !=null){
            flight.setDeparture_at(departure_at);
            flight.setReturn_at(return_at);
        }

        this.generateFlightTickets(flight);

        logger.info("FlightService>>  findFlight: End method.");
        return flight;
    }


    public void saveFlightTickets(@NotNull FlightTicket flightTicket, @NotNull Flight flight ) throws JsonProcessingException {
        logger.info("FlightService>>  saveFlightTickets: Start method.");
        String generateTicketKey = flightTicket.generateTicketKey(flight.getOrigin().getCity().getCityIATACode(),flight.getDestination().getCity().getCityIATACode());

        String existFlightTicket = (String) this.redis.get(generateTicketKey);

        if (existFlightTicket == null )
        {
            this.redis.set(generateTicketKey,flightTicket.toJson(), flightTicket.generateExpireTime());
            logger.info("FlightService>>  saveFlightTickets: Successfully saved.");
        }
        logger.info("FlightService>>  saveFlightTickets: End method.");
    }

    public List<FlightTicket> getTicketByParseKey(String partKey) throws JsonProcessingException {
        logger.info("FlightService >> getTicketByParseKey: Start method.");
        List<FlightTicket> ticketKeysList = new ArrayList<>();

        Set<String> result = this.redis.getKeys(partKey);
        for (String key : result) {
            String value = (String) this.redis.get(key);

            FlightTicket flightTicket = FlightTicket.fromJson(value);
            ticketKeysList.add(flightTicket);
        }
        logger.info("FlightService >> getTicketByParseKey: End method.");
        return ticketKeysList;
    }

    @NotNull
    private List<FlightTicket> getTicketsByFlight(@NotNull Flight flight) throws JsonProcessingException {
        logger.info("FlightService>>  getTicketsByFlight: Start method.");

        String partKey = flight.getOrigin().getCity().getCityIATACode()+"_"+flight.getDestination().getCity().getCityIATACode(); // Create the partial value to search tickets.
        List<FlightTicket> tickets = this.getTicketByParseKey(partKey);  // Use your custom method to get keys

        logger.info("FlightService>>  getTicketsByFlight: Found: "+tickets.size()+" flight tickets.");
        logger.info("FlightService>>  getTicketsByFlight: End method.");
        return tickets;
    }

    @NotNull
    private Flight generateFlight(String origin_cityIATACode,String destination_cityIATACode) {
        logger.info("FlightService>>  generateFlight: Start method.");

        Location origin_Location = this.generateLocation(origin_cityIATACode);
        Location destination_Location = this.generateLocation(destination_cityIATACode);

        logger.info("FlightService>>  generateFlight: End method.");
        return new Flight(origin_Location,destination_Location);
    }

    private void generateFlightTickets(Flight flight) throws IOException, ParseException {
        logger.info("FlightService>>  generateFlightTickets: Start method.");

        List<FlightTicket> flightTicketsList = this.getTicketsByFlight(flight);

        if (flightTicketsList.isEmpty()){

            String json = getFlightTickets(flight.getOrigin().getCity().getCityIATACode(),flight.getDestination().getCity().getCityIATACode(),flight.getDeparture_at(),flight.getReturn_at());

            JsonNode rootNode = this.objectMapper.readTree(json);
            JsonNode dataNode = rootNode.get("data");
            if(dataNode.isEmpty()){
                throw new IllegalArgumentException("Did not found flight tickets for "+flight);
            }

            String index = dataNode.fieldNames().next();

            for (JsonNode ticketNode : dataNode) {
                String value = ticketNode.fieldNames().next();
                double price;
                String airlineIataCode;
                int flightNumber;
                int transfers;
                LocalDateTime departureAt;
                LocalDateTime returnAt;
                LocalDateTime expiresAt;
                LocalDateTime newExpiresAt;

                if( value.matches("-?\\d+(\\.\\d+)?")){
                    price = ticketNode.path(value).get("price").asDouble();
                    airlineIataCode = ticketNode.path(value).get("airline").asText();
                    flightNumber = ticketNode.path(value).get("flight_number").asInt();
                    transfers = Integer.parseInt(value);
                    departureAt = Dates.parseStringToLocalDateTime(ticketNode.path(value).get("departure_at").asText());
                    returnAt = Dates.parseStringToLocalDateTime(ticketNode.path(value).get("return_at").asText());
                    expiresAt = Dates.parseStringToLocalDateTime(ticketNode.path(value).get("expires_at").asText());
                }else {

                    price = ticketNode.get("price").asDouble();
                    airlineIataCode = ticketNode.get("airline").asText();
                    flightNumber = ticketNode.get("flight_number").asInt();
                    transfers = ticketNode.get("transfers").asInt();
                    departureAt = Dates.parseStringToLocalDateTime(ticketNode.get("departure_at").asText());
                    returnAt = Dates.parseStringToLocalDateTime(ticketNode.get("return_at").asText());
                    expiresAt = Dates.parseStringToLocalDateTime(ticketNode.get("expires_at").asText());
                }


                newExpiresAt = Dates.atLocalTime(Dates.atUtc(expiresAt));
                // Create an instance of flight ticket.
                FlightTicket outTicket = new FlightTicket(price,this.airlineService.getSpecificAirlines(airlineIataCode),flightNumber,departureAt,returnAt,newExpiresAt,index,transfers);
                Airline airline = this.airlineService.searchAirline(airlineIataCode);

                outTicket.setAirlineDetails(airline);
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

    private Location generateLocation(String cityIATACode){
        City city = this.cityService.fetchSpecificCityByIATA(cityIATACode);

        if(city==null){
            throw new IllegalArgumentException("Invalid city, did not found.");
        }

        Country country = this.countryService.fetchCountry(city.getCountryIATACode());
        if(country==null){
            throw new IllegalArgumentException("Invalid country, did not found.");
        }
        return new Location(city,country);
    }

}