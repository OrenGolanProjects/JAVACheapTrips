package com.orengolan.cheaptrips.airport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.orengolan.cheaptrips.util.API;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;


@Service
public class AirportService {

    private final MongoTemplate mongoTemplate;
    private static final Logger logger = Logger.getLogger(AirportService.class.getName());
    private final AirportRepository airportRepository;
    private final ObjectMapper objectMapper;
    private final API api;
    private final String airportEndpoint;

    @Autowired
    public AirportService(Dotenv dotenv, AirportRepository airportRepository, ObjectMapper objectMapper, MongoTemplate mongoTemplate, API api) {
        this.airportRepository = airportRepository;
        this.objectMapper = objectMapper;
        this.mongoTemplate = mongoTemplate;
        this.api = api;
        this.airportEndpoint = dotenv.get("airport_ENDPOINT");

    }

    private String getAirports() throws IOException {
        logger.info("AirportService>>  getAirports: Start method");

        return this.api.buildAndExecuteRequest(this.airportEndpoint,null);
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
