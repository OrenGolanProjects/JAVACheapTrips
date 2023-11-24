package com.orengolan.cheaptrips.components;


import com.orengolan.cheaptrips.city.CityRepository;
import com.orengolan.cheaptrips.city.CityService;
import com.orengolan.cheaptrips.countries.CountryService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@Order(3)
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
        if(this.cityRepository.findAll().isEmpty()){
            cityService.synchronizeCityDataWithAPI();
        }
        logger.info("*** CityDataInitialization>>  Run: End method.");
    }
}
