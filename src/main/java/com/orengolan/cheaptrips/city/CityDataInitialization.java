package com.orengolan.cheaptrips.city;


import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class CityDataInitialization implements ApplicationRunner {
    private static final Logger logger = Logger.getLogger(CityDataInitialization.class.getName());

    private final CityRepository cityRepository;
    private final CityService cityService;

    public CityDataInitialization(CityRepository cityRepository, CityService cityService) {
        this.cityRepository = cityRepository;
        this.cityService = cityService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("*** CityDataInitialization>>  Run: Start method.");

        // Check if there are any cities in the database
        if (cityRepository.count() == 0) {
            logger.info("CityDataInitialization>>  Run: Did not found cities at DB starts, Activate synchronize method.");
            cityService.synchronizeCityDataWithAPI();
        }
        logger.info("*** CityDataInitialization>>  Run: End method.");
    }
}
