package com.orengolan.CheapTrips.opentripmap;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OpenTripMapRepository extends MongoRepository<PlacesData,String> {
}
