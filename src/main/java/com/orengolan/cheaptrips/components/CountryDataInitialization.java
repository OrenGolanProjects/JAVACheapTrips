package com.orengolan.cheaptrips.components;

import com.orengolan.cheaptrips.countries.CountryService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@Order(2)
public class CountryDataInitialization implements ApplicationRunner {
    private static final Logger logger = Logger.getLogger(CountryDataInitialization.class.getName());
    private final CountryService countryService;

    public CountryDataInitialization(CountryService countryService) {
        this.countryService = countryService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("*** CountryDataInitialization>>  Run: Start method.");
        if(this.countryService.getAllCountries().isEmpty()){
            countryService.synchronizeCountryDataWithAPI();
        }
        logger.info("*** CountryDataInitialization>>  Run: End method.");
    }
}
