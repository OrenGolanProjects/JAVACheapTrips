package com.orengolan.cheaptrips.airline;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * The {@code AirlineRepository} interface serves as a Spring Data MongoDB repository for managing {@link Airline}
 * entities in the CheapTrips backend application. It extends the {@code MongoRepository} interface, providing
 * convenient methods for CRUD (Create, Read, Update, Delete) operations on the {@code Airline} collection in MongoDB.
 *
 * The repository includes a custom query method:
 * - {@code findByAirlineIATACode}: Retrieves a list of {@code Airline} entities based on the provided IATA code.
 *
 * Usage Example:
 * <pre>
 * {@code
 * List<Airline> airlines = airlineRepository.findByAirlineIATACode("XYZ");
 * }
 * </pre>
 *
 * This interface seamlessly integrates with Spring Data MongoDB, leveraging its repository support to simplify
 * database interactions and reduce boilerplate code. By extending {@code MongoRepository}, it inherits methods for
 * common database operations, and the custom query method adds flexibility for specific retrieval requirements.
 *
 * Note: Ensure that the corresponding MongoDB collection is named "airline" to align with the entity structure.
 */
public interface AirlineRepository extends MongoRepository<Airline ,String> {

    /**
     * Retrieves a list of {@code Airline} entities based on the provided IATA code.
     *
     * @param airlineIATACode The IATA code of the airline.
     * @return A list of {@code Airline} entities matching the given IATA code.
     */
    Airline findByAirlineIATACode(String airlineIATACode);

}
