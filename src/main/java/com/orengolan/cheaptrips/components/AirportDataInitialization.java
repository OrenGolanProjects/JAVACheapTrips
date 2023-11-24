package com.orengolan.cheaptrips.components;

import com.orengolan.cheaptrips.airport.AirportService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@Order(1)
public class AirportDataInitialization implements ApplicationRunner {

    private static final Logger logger = Logger.getLogger(AirlineDataInitialization.class.getName());
    private final AirportService airportService;

    public AirportDataInitialization(AirportService airportService) {
        this.airportService = airportService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("*** AirportDataInitialization>>  Run: Start method.");
        if(this.airportService.fetchAllAirports().isEmpty()){
            airportService.synchronizeAirportDataWithAPI();
        }

        logger.info("*** AirportDataInitialization>>  Run: End method.");
    }
}
