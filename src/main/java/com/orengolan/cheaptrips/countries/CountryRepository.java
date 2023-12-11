package com.orengolan.cheaptrips.countries;


import org.springframework.data.mongodb.repository.MongoRepository;


/**
 * The {@code CountryRepository} interface is a Spring Data MongoDB repository
 * responsible for database operations related to the {@link Country} entity.
 * It extends the {@link org.springframework.data.mongodb.repository.MongoRepository}
 * interface, providing CRUD (Create, Read, Update, Delete) operations for country entities.
 *
 * Key Features:
 * - {@code findByCountryIATACode}: Retrieves a country entity by its unique IATA code.
 *
 * Example:
 * A query using the {@code findByCountryIATACode} method may return a country entity
 * with the specified IATA code, allowing for efficient retrieval of country information.
 *
 * Note: This interface is part of the repository pattern, enabling seamless interaction
 * with MongoDB for storing and retrieving country-related data.
 */
public interface CountryRepository extends MongoRepository<Country, String> {

    /**
     * Retrieves a country entity by its unique IATA code.
     *
     * @param countryIATACode The IATA code of the country to be retrieved.
     * @return The country entity matching the provided IATA code, or null if not found.
     */
    Country findByCountryIATACode(String countryIATACode);
}
