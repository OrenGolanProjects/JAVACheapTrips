package com.orengolan.cheaptrips.airline;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orengolan.cheaptrips.util.API;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Service
public class AirlineService {

    private final String airlineEndpoint;
    private static final Logger logger = Logger.getLogger(AirlineService.class.getName());
    private final ObjectMapper objectMapper;
    private final AirlineRepository airlineRepository;
    private final API api;


    public AirlineService(Dotenv dotenv, ObjectMapper objectMapper, AirlineRepository airlineRepository, API api) {
        this.airlineEndpoint = dotenv.get("airline_ENDPOINT");
        this.objectMapper = objectMapper;
        this.airlineRepository = airlineRepository;
        this.api = api;
    }


    private String getAirlineData() throws IOException {
        logger.info("AirlineService>>  getAirlineData: Start method.");
        return this.api.buildAndExecuteRequest(airlineEndpoint, null);
    }


    public void synchronizeAirlineDataWithAPI(@Nullable String in_AirlineIATACode) throws IOException {
        logger.info("AirlineService>>  synchronizeAirlineDataWithAPI: Start synchronize airlines data from API.");
        String json = this.getAirlineData();

        JsonNode airlineList = objectMapper.readTree(json);
        for (JsonNode airline : airlineList) {
            String airlineName = airline.path("name_translations").get("en").asText();
            String airlineIATACode = airline.get("code").asText();
            Boolean isLowCost = airline.get("is_lowcost").asBoolean();

            Airline newAirline = new Airline(airlineName, airlineIATACode, isLowCost);
            if (!(this.airlineRepository.findByAirlineIATACode(airlineIATACode) == null)) {
                continue;
            }
            if((in_AirlineIATACode !=null) && (!in_AirlineIATACode.equals(airlineIATACode))){
                continue;
            }
            this.airlineRepository.save(newAirline);
            if(in_AirlineIATACode !=null){
                break;
            }
        }
        logger.info("AirlineService>>  synchronizeAirlineDataWithAPI: End synchronize airlines data from API.");
    }

    public List<Airline> getAllAirlines() {
        logger.info("AirlineService>>  getAllAirlines: Start method.");
        return this.airlineRepository.findAll();
    }

    public Airline getSpecificAirlines(String airlineIATACode) {
        logger.info("AirlineService>>  getSpecificAirlines: Start method.");
        return this.airlineRepository.findByAirlineIATACode(airlineIATACode);
    }

    public Airline searchAirline(String airlineIATACode) throws IOException {
        logger.info("AirlineService>>  searchAirline: Start method.");
        Airline airline = this.getSpecificAirlines(airlineIATACode);
        if(airline==null) {
            logger.warning("AirlineService>>  searchAirline: Did not found airline IATA code:"+airlineIATACode);
            this.synchronizeAirlineDataWithAPI(airlineIATACode);
            airline = this.getSpecificAirlines(airlineIATACode);
        }
        logger.info("AirlineService>>  searchAirline: End method.");
        return airline;
    }
}
