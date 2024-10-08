package com.orengolan.cheaptrips.airport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.orengolan.cheaptrips.util.API;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;


/**
 * The {@code AirportService} class is a service component in the CheapTrips backend application, responsible for
 * managing airport data. It acts as an intermediary between the data access layer (MongoDB repository) and an external
 * API, handling the synchronization, deletion, and retrieval of airport information.

 * The class is annotated with {@code @Service}, indicating its role as a service component in the Spring application
 * context. It is designed to integrate with MongoDB, utilizing the {@code AirportRepository} for database interactions.

 * Key features of the {@code AirportService} class include:
 * - {@code synchronizeAirportDataWithAPI}: Initiates the synchronization of airport data with an external API,
 *   updating the database with the latest information.
 * - {@code deleteAirports}: Deletes all airports from the database, providing a clean slate for fresh data.
 * - {@code fetchSpecificAirport}: Retrieves an airport by its name from the database.
 * - {@code fetchAllAirports}: Retrieves a list of all airports stored in the database.

 * The class relies on an external API, specified by the "airport_ENDPOINT" property in the environment variables,
 * and utilizes the {@code API} utility class for making HTTP requests. Additionally, it makes use of the Spring
 * {@code MongoTemplate} and {@code AirportRepository} for MongoDB operations.

 * Usage Example:
 * <pre>
 * {@code
 * AirportService airportService = new AirportService(dotenv, airportRepository, objectMapper, mongoTemplate, api);
 * airportService.synchronizeAirportDataWithAPI();
 * List<Airport> allAirports = airportService.fetchAllAirports();
 * }
 * </pre>
 *
 * This {@code AirportService} class encapsulates the logic for managing airport data, providing methods for
 * synchronization, deletion, and retrieval, facilitating seamless integration with the CheapTrips application.
 */
@Service
public class AirportService {

    private final MongoTemplate mongoTemplate;
    private static final Logger logger = Logger.getLogger(AirportService.class.getName());
    private final AirportRepository airportRepository;
    private final ObjectMapper objectMapper;
    private final API api;
    private final String airportEndpoint;
    private final String airportsEndpoint;

    @Autowired
    public AirportService(Dotenv dotenv, AirportRepository airportRepository, ObjectMapper objectMapper, MongoTemplate mongoTemplate, API api) {
        this.airportRepository = airportRepository;
        this.objectMapper = objectMapper;
        this.mongoTemplate = mongoTemplate;
        this.api = api;
        this.airportEndpoint = dotenv.get("airport_ENDPOINT");
        this.airportsEndpoint = dotenv.get("airports_ENDPOINT");
    }

    public String getAirports() throws IOException {
        logger.info("AirportService>>  getAirports: Start method");
        return this.api.buildAndExecuteRequest(this.airportEndpoint, null);
    }


    public String getAirportsData() throws IOException {
        logger.info("AirportService>>  getAirportsData: Start method");
        String json = this.api.buildAndExecuteRequest(this.airportsEndpoint, null);
        JsonNode airportList = objectMapper.readTree(json);
        List<ObjectNode> filteredAirports = new ArrayList<>();

        for (JsonNode airportNode : airportList) {
            if ("airport".equals(airportNode.path("iata_type").asText())) {
                ObjectNode airportObject = objectMapper.createObjectNode();

                ObjectNode airport = objectMapper.createObjectNode();
                airport.put("code", airportNode.path("code").asText());
                airport.put("name", airportNode.path("name").asText());
                airport.put("city_code", airportNode.path("city_code").asText());
                airport.put("country_code", airportNode.path("country_code").asText());
                airportObject.set("airport", airport);

                filteredAirports.add(airportObject);
            }
        }

        // Convert the list of ObjectNode to a JSON string
        return objectMapper.writeValueAsString(filteredAirports);
    }


    public void synchronizeAirportDataWithAPI() throws IOException {
        logger.info("AirportService>>  synchronizeAirportDataWithAPI: Start method");
        String json = getAirports();

        // Parse the JSON array
        JsonNode airportList = objectMapper.readTree(json);

        for (JsonNode airportNode : airportList) {
            JsonNode airportCodeNode = airportNode.path("city_code");
            // if the node is empty or miss.
            if ( airportCodeNode.isMissingNode() || airportCodeNode.isNull() || !(airportNode.get("iata_type").asText().equals("airport"))
            ) {
                // Skip processing this airport if name is null
                continue;
            }

            // CITY IATA
            String cityIATACode = airportCodeNode.asText().toUpperCase();
            // COUNTRY IATA
            String countryIATACode      =  airportNode.path("country_code").asText().toUpperCase();
            // AIRPORT IATA
            String airportIATACode      =  airportNode.path("code").asText().toUpperCase();
            String timeZone             =  airportNode.path("time_zone").asText();
            String airportName          =  airportNode.path("name").asText();
            Double lonCoordinates       =  airportNode.path("coordinates").path("lon").asDouble();
            Double latCoordinates       =  airportNode.path("coordinates").path("lat").asDouble();

            // Create a new City object and save it to the repository
            Airport airport = new Airport(
                    airportName,
                    airportIATACode,
                    cityIATACode,
                    countryIATACode,
                    timeZone,
                    lonCoordinates,
                    latCoordinates
            );
            if(!(this.airportRepository.findByAirportIATACode(airportIATACode)==null)){
                continue;
            }

            airportRepository.save(airport);
        }
        logger.info("AirportService>>  synchronizeAirportDataWithAPI: End method");
    }

    public void deleteAirports(){
        logger.info("AirportService>>  deleteAirports: Start method");
        mongoTemplate.dropCollection("airports");
    }

    public Airport fetchSpecificAirport(String airportName){
        return this.airportRepository.findByAirportNameIgnoreCase(airportName);
    }

    public List<Airport> fetchAllAirports(){
        return this.airportRepository.findAll();
    }

}
