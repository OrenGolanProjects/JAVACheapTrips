package com.orengolan.CheapTrips.repository;

import com.orengolan.CheapTrips.model.Airport;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AirportRepository extends MongoRepository<Airport,String> {
    Airport findByAirportIATACode(String airportIATACode);
    List<Airport> findByCountryIATACode(String countryIATACode);
}
