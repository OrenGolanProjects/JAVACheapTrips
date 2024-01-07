package com.orengolan.cheaptrips.city;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

/**
 * The {@code CityRepository} interface serves as the data access layer for the City entity in the CheapTrips backend
 * application. Extending the {@code MongoRepository} interface provided by Spring Data MongoDB, this repository
 * facilitates standard CRUD (Create, Read, Update, Delete) operations on City entities stored in the MongoDB database.
 *
 * The repository defines two query methods:
 * - {@code findByCityIATACode}: Retrieves a list of cities based on their IATA code.
 * - {@code findByCityName}: Retrieves a list of cities based on a case-insensitive regular expression match on their names.
 *
 * Additionally, the repository supports custom queries through the {@code @Query} annotation, allowing flexible search
 * capabilities for City entities.
 *
 * Usage Example:
 * <pre>
 * {@code
 * // Retrieve cities by their IATA code
 * List<City> citiesByIATACode = cityRepository.findByCityIATACode("XYZ");
 *
 * // Retrieve cities by a case-insensitive regular expression match on their names
 * List<City> citiesByName = cityRepository.findByCityName("lond");
 * }
 * </pre>
 *
 * This {@code CityRepository} interface simplifies interactions with the MongoDB database, providing convenient
 * methods for querying and managing City entities in the CheapTrips application.
 */
public interface CityRepository extends MongoRepository<City, String> {
    City findByCityIATACode(String cityIATACode);


    @Query("{'cityName': { $regex: ?0, $options: 'i' }}")
    List<City> findByCityName(String cityName);
}
