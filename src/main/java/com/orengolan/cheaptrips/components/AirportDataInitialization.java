package com.orengolan.cheaptrips.components;

import com.orengolan.cheaptrips.airport.AirportService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.logging.Logger;

/**
 * The {@code AirportDataInitialization} class is a Spring Boot component responsible for initializing airport data
 * upon application startup. It implements the {@code ApplicationRunner} interface, allowing custom logic to be executed
 * during the application's run phase. This component is annotated with {@code @Component} to be automatically detected
 * and registered by the Spring framework.
 *
 * The initialization process involves checking if the airport repository is empty, and if so, triggering the synchronization
 * of airport data with an external API using the {@code AirportService}. This ensures that the application starts with
 * up-to-date airport information when the repository is empty.
 *
 * The {@code @Order(1)} annotation specifies the execution order of this component relative to other components.
 * A lower order value means the component is executed earlier during the application startup process.
 *
 * Usage Example:
 * <pre>
 * {@code
 * // During application startup, the AirportDataInitialization component checks if the airport repository is empty.
 * // If empty, it synchronizes airport data with an external API using the AirportService.
 * }
 * </pre>
 *
 * This {@code AirportDataInitialization} class plays a crucial role in ensuring the availability of current airport data
 * at the start of the application, contributing to a seamless user experience in the CheapTrips application.
 */
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
