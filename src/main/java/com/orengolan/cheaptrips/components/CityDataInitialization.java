package com.orengolan.cheaptrips.components;


import com.orengolan.cheaptrips.city.CityRepository;
import com.orengolan.cheaptrips.city.CityService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.logging.Logger;

/**
 * The {@code CityDataInitialization} class is a Spring Boot component responsible for initializing city data
 * upon application startup. It implements the {@code ApplicationRunner} interface, allowing custom logic to be executed
 * during the application's run phase. This component is annotated with {@code @Component} to be automatically detected
 * and registered by the Spring framework.
 *
 * The initialization process involves checking if the city repository is empty, and if so, triggering the synchronization
 * of city data with an external API using the {@code CityService}. This ensures that the application starts with
 * up-to-date city information when the repository is empty.
 *
 * The {@code @Order(3)} annotation specifies the execution order of this component relative to other components.
 * A lower order value means the component is executed earlier during the application startup process.
 *
 * Usage Example:
 * <pre>
 * {@code
 * // During application startup, the CityDataInitialization component checks if the city repository is empty.
 * // If empty, it synchronizes city data with an external API using the CityService.
 * }
 * </pre>
 *
 * This {@code CityDataInitialization} class plays a crucial role in ensuring the availability of current city data
 * at the start of the application, contributing to a seamless user experience in the CheapTrips application.
 */
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
