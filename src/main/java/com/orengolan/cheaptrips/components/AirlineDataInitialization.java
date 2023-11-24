package com.orengolan.cheaptrips.components;


import com.orengolan.cheaptrips.airline.AirlineRepository;
import com.orengolan.cheaptrips.airline.AirlineService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.logging.Logger;

@Component
@Order(4)
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
        if(this.airlineRepository.findAll().isEmpty()){
            airlineService.synchronizeAirlineDataWithAPI(null);
        }
        logger.info("*** AirlineDataInitialization>>  Run: End method.");
    }
}
