package com.orengolan.cheaptrips.airline;


import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class AirlineDataInitialization implements ApplicationRunner {
    private static final Logger logger = Logger.getLogger(AirlineDataInitialization.class.getName());
    private final AirlineRepository airlineRepository;
    private final AirlineService airlineService;

    public AirlineDataInitialization(AirlineRepository airlineRepository, AirlineService airlineService) {
        this.airlineRepository = airlineRepository;
        this.airlineService = airlineService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("*** AirlineDataInitialization>>  Run: Start method.");

        // Check if there are any cities in the database
        if (airlineRepository.count() == 0) {
            logger.info("AirlineDataInitialization>>  Run: Did not found airlines at DB starts, Activate synchronize method.");
            airlineService.synchronizeAirlineDataWithAPI();
        }
        logger.info("*** AirlineDataInitialization>>  Run: End method.");
    }
}
