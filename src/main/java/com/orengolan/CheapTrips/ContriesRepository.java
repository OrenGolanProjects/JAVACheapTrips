package com.orengolan.CheapTrips;



import com.orengolan.CheapTrips.model.City;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContriesRepository extends MongoRepository<City, String> {
    City findByCityName(String cityName); // Updated method name
}

