package com.orengolan.cheaptrips.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * The {@code MongoDBConfig} class is a Spring configuration class responsible for configuring
 * the MongoDB client connection settings. It extends the {@code AbstractMongoClientConfiguration}
 * class provided by Spring Data MongoDB to customize the MongoDB client instantiation.
 *
 * The class uses the Spring property placeholder annotation {@code @Value} to inject the MongoDB
 * connection URI from the application properties. The MongoDB connection URI typically includes
 * details such as host, port, authentication credentials, and database name.
 *
 * The {@code getDatabaseName()} method specifies the name of the MongoDB database to be used by the
 * application. Adjust the database name according to your MongoDB setup.
 *
 * The {@code mongoClient()} method creates and returns a configured {@code MongoClient} instance
 * using the provided connection settings. The {@code MongoClient} is responsible for connecting
 * to the MongoDB server and executing database operations.
 *
 * Example Configuration:
 * <pre>
 * {@code
 * # application.properties or application.yml
 * spring.data.mongodb.uri=mongodb://username:password@localhost:27017/cheaptrips
 * }
 * </pre>
 *
 * Usage Example:
 * <pre>
 * {@code
 * // In MongoDB repositories or services, autowire MongoClient for database operations.
 * @Autowired
 * private MongoClient mongoClient;
 * }
 * </pre>
 *
 * The {@code MongoDBConfig} class facilitates the integration of MongoDB with the Spring application,
 * allowing seamless connectivity and interaction with MongoDB databases.
 */
@Configuration
public class MongoDBConfig extends AbstractMongoClientConfiguration {

    /**
     * MongoDB's connection URI injected from application properties.
     */
    @Value("${spring.data.mongodb.uri}")
    private String uri;

    /**
     * Specifies the name of the MongoDB database to be used.
     *
     * @return The name of the MongoDB database.
     */
    @Override
    protected String getDatabaseName() {
        return "cheaptrips"; // Modify this according to your database name
    }

    /**
     * Creates and configures a MongoClient instance based on the provided connection URI.
     *
     * @return MongoClient instance for connecting to MongoDB.
     */
    @Bean
    @Override
    public MongoClient mongoClient() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .build();

        return MongoClients.create(settings);
    }
}

