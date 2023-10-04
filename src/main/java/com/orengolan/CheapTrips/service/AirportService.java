package com.orengolan.CheapTrips.service;

import com.orengolan.CheapTrips.model.Airport;
import com.orengolan.CheapTrips.repository.AirportRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import okhttp3.OkHttpClient;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;


@Service
public class AirportService {
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final Logger logger = Logger.getLogger(AirportService.class.getName());
    private static final String API_URL = "https://api.travelpayouts.com/data/en/airports.json";
    private static final String SUCCESS_MESSAGE = "Airport list processed successfully.";
    private final AirportRepository airportRepository;
    private final ObjectMapper objectMapper;
    private final OkHttpClient client = new OkHttpClient();

    @Autowired
    public AirportService(AirportRepository airportRepository, ObjectMapper objectMapper) {
        this.airportRepository = airportRepository;
        this.objectMapper = objectMapper;
    }

    public String getAirportList() throws IOException {
        logger.info("Starting get airport list");
        Request request = new Request.Builder().url(API_URL).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            if (response.body() != null) {
                logger.info("Finished get airports list.");
                return response.body().string();
            } else {
                throw new IOException("Body of response if empty. " + response);
            }
        } catch(IOException e) {
            logger.severe("Error get airport list: " + e.getMessage());
            return "Error get airport list: " + e.getMessage();
        }
    }

    public String synchronizeAirportDataWithAPI() {
        try {
            logger.info("Starting synchronize airports data from API.");
            String json = getAirportList();

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
        this.airportRepository.deleteAll();
        mongoTemplate.dropCollection(Airport.class);
        logger.info("Finished delete airports data from DB.");
        return Boolean.TRUE;
    }

}
