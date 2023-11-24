package com.orengolan.cheaptrips.city;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CityRepository extends MongoRepository<City, String> {
    City findByCityIATACode(String cityIATACode);
    List<City> findByCityName(String cityName);

}
