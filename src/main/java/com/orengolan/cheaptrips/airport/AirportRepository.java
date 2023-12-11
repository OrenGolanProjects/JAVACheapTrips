package com.orengolan.cheaptrips.airport;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * The {@code AirportRepository} interface extends the Spring Data MongoDB {@code MongoRepository} to provide
 * convenient methods for CRUD (Create, Read, Update, Delete) operations on the {@code Airport} collection in MongoDB.
 * It specializes in the persistence of airport entities in the CheapTrips backend application.
 *
 * The repository includes the following custom query methods:
 * - {@code findByAirportNameIgnoreCase}: Retrieves an airport by its name, ignoring case sensitivity.
 * - {@code findByAirportIATACode}: Retrieves a list of airports based on their IATA code.
 *
 * Usage Example:
 * <pre>
 * {@code
 * AirportRepository airportRepository = // ... instantiate repository
 * Airport airport = airportRepository.findByAirportNameIgnoreCase("Example Airport");
 * List<Airport> airportsByIATACode = airportRepository.findByAirportIATACode("XYZ");
 * }
 * </pre>
 *
 * This interface seamlessly integrates with Spring Data MongoDB, providing a high-level abstraction for interacting
 * with the underlying MongoDB database. It simplifies the implementation of data access operations related to airports
 * in the CheapTrips application.
 */
public interface AirportRepository extends MongoRepository<Airport,String> {
    /**
     * Retrieves an airport by its name, ignoring case sensitivity.
     *
     * @param airportName The name of the airport.
     * @return The {@code Airport} entity matching the provided name, ignoring case.
     */
    Airport findByAirportNameIgnoreCase(String airportName);
    /**
     * Retrieves a list of airports based on their IATA code.
     *
     * @param airportIATACode The IATA code of the airport.
     * @return A list of {@code Airport} entities with the given IATA code.
     */
    Airport findByAirportIATACode(String airportIATACode);
}
