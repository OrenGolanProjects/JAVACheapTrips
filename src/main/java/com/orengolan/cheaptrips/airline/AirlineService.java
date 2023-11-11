package com.orengolan.cheaptrips.airline;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orengolan.cheaptrips.config.ConfigLoader;
import com.orengolan.cheaptrips.util.API;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Service
public class AirlineService {

    private static final Logger logger = Logger.getLogger(AirlineService.class.getName());
    JSONObject config = ConfigLoader.loadConfig();
    private final ObjectMapper objectMapper;
    private final AirlineRepository airlineRepository;
    private final API api;

    public AirlineService(ObjectMapper objectMapper, AirlineRepository airlineRepository, API api) {
        this.objectMapper = objectMapper;
        this.airlineRepository = airlineRepository;
        this.api = api;
    }

    private String getAirlineData() throws IOException {
        logger.info("AirlineService>>  getAirlineData: Start method.");
        String ENDPOINT_URL = (String) config.get("airline_ENDPOINT");
        logger.info("AirlineService>>  getAirlineData: ENDPOINT URL: "+ENDPOINT_URL);
        return this.api.buildAndExecuteRequest(ENDPOINT_URL,null);
    }


    public void synchronizeAirlineDataWithAPI() throws IOException {
        logger.info("AirlineService>>  synchronizeAirlineDataWithAPI: Start synchronize airlines data from API.");
        String json = this.getAirlineData();
        JsonNode airlineList = objectMapper.readTree(json);
        for (JsonNode airline : airlineList){
            String airlineName = airline.path("name_translations").get("en").asText();
            String airlineIATACode = airline.get("code").asText();
            Boolean isLowCost = airline.get("is_lowcost").asBoolean();

            Airline newAirline = new Airline(airlineName,airlineIATACode,isLowCost);
            if(this.airlineRepository.findByAirlineIATACode(airlineIATACode) == null) {
                this.airlineRepository.save(newAirline);
            }
        }
        logger.info("AirlineService>>  synchronizeAirlineDataWithAPI: End synchronize airlines data from API.");
    }

    public List<Airline> getAllAirlines(){
        logger.info("AirlineService>>  getAllAirlines: Start method.");
        return this.airlineRepository.findAll();
    }
    public Airline getSpecificAirlines(String airlineIATACode){
        logger.info("AirlineService>>  getSpecificAirlines: Start method.");
        return this.airlineRepository.findByAirlineIATACode(airlineIATACode);
    }

}
