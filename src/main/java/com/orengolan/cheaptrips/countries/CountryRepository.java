package com.orengolan.cheaptrips.countries;


import org.springframework.data.mongodb.repository.MongoRepository;

public interface CountryRepository extends MongoRepository<Country, String> {

    Country findByCountryIATACode(String countryIATACode);
}
