package com.orengolan.cheaptrips.airline;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AirlineRepository extends MongoRepository<Airline ,String> {
    Airline findByAirlineIATACode(String airlineIATACode);

}
