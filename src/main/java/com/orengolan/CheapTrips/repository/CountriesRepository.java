package com.orengolan.CheapTrips.repository;



import com.orengolan.CheapTrips.model.City;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface CountriesRepository extends MongoRepository<City, String> {
    City findByCityName(String cityName); // Updated method name
}

