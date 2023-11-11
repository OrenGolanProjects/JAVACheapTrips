package com.orengolan.cheaptrips.airport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.orengolan.cheaptrips.util.API;
import java.io.IOException;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;


@Service
public class AirportService {

    private final MongoTemplate mongoTemplate;
    private static final Logger logger = Logger.getLogger(AirportService.class.getName());
    private static final String API_URL = System.getenv("airport_ENDPOINT");
    private static final String SUCCESS_MESSAGE = "Airport list processed successfully.";
    private final AirportRepository airportRepository;
    private final ObjectMapper objectMapper;
    private final API api;



    @Autowired
    public AirportService(AirportRepository airportRepository, ObjectMapper objectMapper, MongoTemplate mongoTemplate,API api) {
        this.airportRepository = airportRepository;
        this.objectMapper = objectMapper;
        this.mongoTemplate = mongoTemplate;
        this.api = api;
    }

    private String getAirports() throws IOException {
        logger.info("Starting get airport list");
        return this.api.buildAndExecuteRequest(API_URL,null);
    }

    public String synchronizeAirportDataWithAPI() {
        try {
            logger.info("Starting synchronize airports data from API.");
            String json = getAirports();

            // Parse the JSON array
            JsonNode airportList = objectMapper.readTree(json);

            for (JsonNode airportNode : airportList) {
                JsonNode airportCodeNode = airportNode.path("city_code");
                // if the node is empty or miss.
                if (airportCodeNode.isMissingNode() || airportCodeNode.isNull()) {
                    // Skip processing this airport if name is null
                    continue;
                }

                if (!airportNode.path("flightable").asBoolean() ){
                    // Skip processing this flight is not available
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



                if (cityIATACode.isEmpty()) {
                    continue; // Skip processing this airport if cityIATACode is null or empty
                }

                // Check if an airport with the same name exists
                Airport existingAirport =  this.airportRepository.findByAirportIATACode(airportIATACode);
                if (existingAirport != null){
                    logger.warning("Airport IATA Code already exists: "+airportIATACode+" ,skipped.");
                    continue;
                }

                // Create a new City object and save it to the repository
                Airport airport = new Airport(airportIATACode,airportName,lonCoordinates,latCoordinates,timeZone,countryIATACode,cityIATACode);

                if (!airportName.toLowerCase().contains("airport")) {
                    logger.warning("Not valid Airport name: "+airport);
                    continue;
                }
                airportRepository.save(airport);
            }
            logger.info("Finished synchronize airports data from API.");
            return SUCCESS_MESSAGE;
        }catch(IOException e) {
            logger.severe("Error synchronize airports data: " + e.getMessage());
            return "Error processing synchronize airports data: " + e.getMessage();
        }
    }

    public boolean deleteAirports(){
        logger.info("Starting delete airports list");
        mongoTemplate.dropCollection("airports");
        logger.info("Finished delete airports data from DB.");
        return Boolean.TRUE;
    }

}
