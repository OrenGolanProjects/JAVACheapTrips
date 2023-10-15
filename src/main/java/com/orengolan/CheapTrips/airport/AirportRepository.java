package com.orengolan.CheapTrips.airport;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AirportRepository extends MongoRepository<Airport,String> {
    Airport findByAirportIATACode(String airportIATACode);
    List<Airport> findByCountryIATACode(String countryIATACode);
    List<Airport> findByCountryIATACodeAndCityIATACode(String countryIATACode, String cityIATACode);
}
