package com.orengolan.cheaptrips.news;


import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * The {@code NewsRepository} interface extends Spring Data MongoDB's {@code MongoRepository} and serves as
 * the data access layer for interacting with the MongoDB database to perform CRUD operations on {@code News} entities.
 *
 * Key Features:
 * - {@code @MongoRepository<News, String>}: Inherits Spring Data MongoDB's repository functionality for the
 *   {@code News} entity with a primary key of type {@code String}.
 * - {@code findByCityName(String cityString)}: Declares a custom query method to retrieve news entries based on
 *   the provided city name. Spring Data MongoDB automatically generates the query implementation.
 *
 * Example Usage:
 * The interface is used by the Spring framework to create a runtime proxy implementing the CRUD operations defined
 * in the {@code MongoRepository} interface. It allows seamless interaction with the MongoDB database for storing,
 * retrieving, updating, and deleting news entries based on their city names.
 *
 * Note: Ensure proper configuration of Spring Data MongoDB, including the connection settings in the application properties.
 */
public interface NewsRepository  extends MongoRepository<News, String>{

    /**
     * Retrieves news entries based on the provided city name.
     *
     * @param cityString The name of the city for which news entries are requested.
     * @return The {@code News} object containing news details for the specified city.
     */
    News findByCityName(String cityString);
}
