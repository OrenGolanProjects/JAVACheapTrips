package com.orengolan.cheaptrips.components;


import com.orengolan.cheaptrips.airline.AirlineRepository;
import com.orengolan.cheaptrips.airline.AirlineService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.logging.Logger;

/**
 * The {@code AirlineDataInitialization} class is a Spring Boot component responsible for initializing airline data
 * upon application startup. It implements the {@code ApplicationRunner} interface, allowing custom logic to be executed
 * during the application's run phase. This component is annotated with {@code @Component} to be automatically detected
 * and registered by the Spring framework.
 *
 * The initialization process involves checking if the airline repository is empty, and if so, triggering the synchronization
 * of airline data with an external API using the {@code AirlineService}. This ensures that the application starts with
 * up-to-date airline information when the repository is empty.
 *
 * The {@code @Order(4)} annotation specifies the execution order of this component relative to other components.
 * A lower order value means the component is executed earlier during the application startup process.
 *
 * Usage Example:
 * <pre>
 * {@code
 * // During application startup, the AirlineDataInitialization component checks if the airline repository is empty.
 * // If empty, it synchronizes airline data with an external API using the AirlineService.
 * }
 * </pre>
 *
 * This {@code AirlineDataInitialization} class plays a crucial role in ensuring the availability of current airline data
 * at the start of the application, contributing to a seamless user experience in the CheapTrips application.
 */
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
