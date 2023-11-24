package com.orengolan.cheaptrips.opentripmap;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface OpenTripMapRepository extends MongoRepository<PlacesData,String> {
    PlacesData findByCity(String city);
}
