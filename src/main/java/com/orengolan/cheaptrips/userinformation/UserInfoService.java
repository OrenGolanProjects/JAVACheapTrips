package com.orengolan.cheaptrips.userinformation;


import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.logging.Logger;

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

    public UserInfo getUserByIdentifier(String userIdentifier) {
        logger.info("UserService>>  getUserByIdentifier: Start method.");

        UserInfo userByEmail = this.userInfoRepository.findUserByEmail(userIdentifier);
        UserInfo userByName = this.userInfoRepository.findUserByUserName(userIdentifier);

        if(userByEmail==null && userByName==null){
            throw new IllegalArgumentException("User did not found, create a new user.");
        }

        return (userByEmail != null) ? userByEmail : userByName;
    }

    public UserInfo deleteSpecificUser(String email) {
        logger.info("UserService>>  deleteSpecificUser: Start method.");
        return this.userInfoRepository.deleteByEmail(email);
    }


    public UserInfo updateUserInfo(String userIdentifier, UserInfo updatedUserInfo) {
        logger.info("UserService>>  updateUserInfo: Start method.");

        // Check if the user exists
        UserInfo existingUser = getUserByIdentifier(userIdentifier);

        // If the user does not exist, throw an exception or handle it as needed
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found for update.");
        }

        existingUser.setUserName(updatedUserInfo.getUserName());
        existingUser.setFirstName(updatedUserInfo.getFirstName());
        existingUser.setSurName(updatedUserInfo.getSurName());
        existingUser.setPhone(updatedUserInfo.getPhone());

        // Save the updated user information
        UserInfo updatedUser = this.userInfoRepository.save(existingUser);

        logger.info("UserService>>  updateUserInfo: End method.");
        return updatedUser;
    }

}
