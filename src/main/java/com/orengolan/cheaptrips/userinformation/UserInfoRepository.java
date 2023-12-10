package com.orengolan.cheaptrips.userinformation;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserInfoRepository extends MongoRepository<UserInfo,String> {
    UserInfo findUserByEmail(String email);
    UserInfo deleteByEmail(String email);
    UserInfo findUserByUserName(String userName);
}
