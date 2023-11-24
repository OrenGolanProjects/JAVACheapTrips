package com.orengolan.cheaptrips.airport;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AirportRepository extends MongoRepository<Airport,String> {
    Airport findByAirportNameIgnoreCase(String airportName);
    Airport findByAirportIATACode(String airportIATACode);
}
