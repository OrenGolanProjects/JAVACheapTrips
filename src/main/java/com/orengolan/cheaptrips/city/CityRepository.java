package com.orengolan.cheaptrips.city;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface CityRepository extends MongoRepository<City, String> {
    City findByCityIATACode(String cityIATACode);
    List<City> findCityByCityIATACode(String cityIATACode);
    List<City> findBycityName(String cityName);

    // search for a city with partial value with min 4 chars to match.
    @Query("{ 'cityName' : { $regex: '^?0', $options: 'i' } }")
    City findCityByPartialName(String partialName);
}
