package com.orengolan.cheaptrips.usersearch;


import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.logging.Logger;
import org.apache.commons.validator.routines.EmailValidator;

@Service
public class UserInfoService {

    private final UserInfoRepository userInfoRepository;
    private static final Logger logger = Logger.getLogger(UserInfoService.class.getName());

    @Autowired
    public UserInfoService(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    public UserInfo createNewUser(@NotNull UserInfo user)  {
        logger.info("UserService>>  createNewUser: Start method.");

        UserInfo userInfo = this.userInfoRepository.findUserByEmail(user.getEmail());

        if( userInfo==null){
            this.userInfoRepository.save(user);
            logger.info("UserService>>  createNewUser: End method.");
            return user;
        }
        logger.severe("UserService>>  createNewUser: Found user from DB!.");

        return userInfo;
    }

    public UserInfo getSpecificUserByEmail(String email) {
        logger.info("UserService>>  getSpecificUserByEmail: Start method.");
        if(!isValidEmail(email)){
            throw new IllegalArgumentException("Invalid email.");
        }
        logger.info("UserService>>  getSpecificUserByEmail: End method.");

        UserInfo user = this.userInfoRepository.findUserByEmail(email);
        if(user==null){
            throw new IllegalArgumentException("Email did not found, Please create new user.");
        }
        return user;
    }

    public UserInfo getSpecificUserByUserName(String UserName) {
        logger.info("UserService>>  getSpecificUserByUserName: Start method.");
        return this.userInfoRepository.findUserByUserName(UserName);
    }

    public static boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public UserInfo deleteSpecificUser(String email) {
        logger.info("UserService>>  deleteSpecificUser: Start method.");
        return this.userInfoRepository.deleteByEmail(email);
    }

}
