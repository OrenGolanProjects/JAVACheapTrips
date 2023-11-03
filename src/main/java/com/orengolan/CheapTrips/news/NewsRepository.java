package com.orengolan.CheapTrips.news;


import org.springframework.data.mongodb.repository.MongoRepository;

public interface NewsRepository  extends MongoRepository<News, String>{
    News findByCityName(String cityString);
}
