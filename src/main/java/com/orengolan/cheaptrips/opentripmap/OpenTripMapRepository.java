package com.orengolan.cheaptrips.opentripmap;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * The {@code OpenTripMapRepository} interface extends the MongoDB repository and provides methods for interacting with the database
 * to retrieve and manipulate place data related to cities in the OpenTripMap service.
 *
 * Key Features:
 * - Extends {@link org.springframework.data.mongodb.repository.MongoRepository} for MongoDB data access.
 * - {@code findByCity}: Retrieves place data based on the provided city name.
 *
 * Example Usage:
 * The interface is used to define methods for querying and managing place data in the OpenTripMap database.
 * By extending {@link org.springframework.data.mongodb.repository.MongoRepository}, it inherits common CRUD operations.
 * The {@code findByCity} method allows searching for place data associated with a specific city.
 *
 * Note: This interface is part of the data access layer and facilitates seamless interaction with the MongoDB database
 * for storing and retrieving place-related information in the OpenTripMap service.
 */
public interface OpenTripMapRepository extends MongoRepository<PlacesData,String> {
    /**
     * Retrieves place data based on the provided city name.
     *
     * @param city The name of the city for which place data is requested.
     * @return {@link PlacesData} containing information about places in the specified city.
     */
    PlacesData findByCity(String city);
}
