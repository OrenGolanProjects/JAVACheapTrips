package com.orengolan.CheapTrips.user;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserRepository extends MongoRepository<User,String> {
    User findUserByEmail(String email);
    User deleteByEmail(String email);
}
