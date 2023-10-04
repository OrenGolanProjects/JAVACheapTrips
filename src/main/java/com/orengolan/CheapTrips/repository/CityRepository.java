package com.orengolan.CheapTrips.repository;

import com.orengolan.CheapTrips.model.City;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CityRepository extends MongoRepository<City, String> {
    City findByCityIATACode(String cityIATACode);
    List<City> findBycityName(String cityName);
}
