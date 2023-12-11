package com.orengolan.cheaptrips.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The {@code DotenvConfig} class is a Spring configuration class responsible for creating and configuring
 * a Dotenv instance using the "dotenv" library. Dotenv is used to load environment variables from a .env file
 * into the application, providing a convenient way to manage configuration properties during development
 * and deployment.
 *
 * The class defines a Spring Bean method, {@code dotenv()}, annotated with {@code @Bean}, to instantiate
 * and configure the Dotenv instance. This allows the application to access environment variables defined
 * in the .env file, facilitating dynamic configuration without hardcoding sensitive information.
 *
 * Usage Example:
 * <pre>
 * {@code
 * // In other configuration classes or components, access environment variables using the Dotenv instance.
 * @Autowired
 * private Dotenv dotenv;
 * String apiKey = dotenv.get("API_KEY");
 * }
 * </pre>
 *
 * The {@code DotenvConfig} class enhances the configurability and security of the application by utilizing
 * Dotenv to manage environment variables and external configurations.
 */
@Configuration
public class DotenvConfig {

    /**
     * Creates and configures a Dotenv instance to load environment variables from a .env file.
     *
     * @return Dotenv instance for accessing environment variables.
     */
    @Bean
    public Dotenv dotenv() {
        return Dotenv.configure().load();
    }
}