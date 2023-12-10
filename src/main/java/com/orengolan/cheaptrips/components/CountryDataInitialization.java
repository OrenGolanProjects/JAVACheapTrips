package com.orengolan.cheaptrips.components;

import com.orengolan.cheaptrips.countries.CountryService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.logging.Logger;

/**
 * The {@code CountryDataInitialization} class is a Spring Boot component responsible for initializing country data
 * upon application startup. It implements the {@code ApplicationRunner} interface, allowing custom logic to be executed
 * during the application's run phase. This component is annotated with {@code @Component} to be automatically detected
 * and registered by the Spring framework.
 *
 * The initialization process involves checking if the list of countries is empty in the application, and if so,
 * triggering the synchronization of country data with an external API using the {@code CountryService}.
 * This ensures that the application starts with up-to-date country information when the list is empty.
 *
 * The {@code @Order(2)} annotation specifies the execution order of this component relative to other components.
 * A lower order value means the component is executed earlier during the application startup process.
 *
 * Usage Example:
 * <pre>
 * {@code
 * // During application startup, the CountryDataInitialization component checks if the list of countries is empty.
 * // If empty, it synchronizes country data with an external API using the CountryService.
 * }
 * </pre>
 *
 * This {@code CountryDataInitialization} class plays a crucial role in ensuring the availability of current country data
 * at the start of the application, contributing to a comprehensive dataset in the CheapTrips application.
 */
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
