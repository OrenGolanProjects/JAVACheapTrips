package com.orengolan.CheapTrips.news;


import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NewsRepository  extends MongoRepository<News, String>{
    News findByCityName(String cityString);
}
