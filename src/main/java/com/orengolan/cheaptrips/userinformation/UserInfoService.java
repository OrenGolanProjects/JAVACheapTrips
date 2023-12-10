package com.orengolan.cheaptrips.userinformation;


import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.logging.Logger;

/**
 * The {@code UserInfoService} class provides business logic for managing user information in the CheapTrips application.
 * It acts as an intermediary between the controller layer and the repository, handling user-related operations.
 *
 * Key Features:
 * - Implements methods for creating new users, deleting users, fetching user details, and updating user information.
 * - Uses the {@link UserInfoRepository} to interact with the underlying data store (MongoDB).
 * - Employs logging with {@link java.util.logging.Logger} to record key events and actions.
 * - Ensures data integrity by validating and handling user-related operations.
 *
 * Example Usage:
 * This service class is used by the controller layer to handle user-related operations. It interacts with the repository
 * to persist and retrieve user information. Logging is employed to capture important events during user management.
 *
 * Note: Exception handling and validation ensure that user operations are performed securely and consistently.
 */
@Service
public class UserInfoService {

    private final UserInfoRepository userInfoRepository;
    private static final Logger logger = Logger.getLogger(UserInfoService.class.getName());

    @Autowired
    public UserInfoService(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    /**
     * Creates a new user in the system.
     *
     * @param user The {@link UserInfo} object representing the new user.
     * @return The created user.
     * @throws IllegalArgumentException If the user already exists.
     */
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

    /**
     * Fetches user information based on the user identifier (username or email).
     *
     * @param userIdentifier The username or email of the user.
     * @return The user information.
     * @throws IllegalArgumentException If the user is not found.
     */
    public UserInfo getUserByIdentifier(String userIdentifier) {
        logger.info("UserService>>  getUserByIdentifier: Start method.");

        UserInfo userByEmail = this.userInfoRepository.findUserByEmail(userIdentifier);
        UserInfo userByName = this.userInfoRepository.findUserByUserName(userIdentifier);

        if(userByEmail==null && userByName==null){
            throw new IllegalArgumentException("User did not found, create a new user.");
        }

        return (userByEmail != null) ? userByEmail : userByName;
    }

    /**
     * Deletes a specific user based on the provided email.
     *
     * @param email The email of the user to be deleted.
     */
    public UserInfo deleteSpecificUser(String email) {
        logger.info("UserService>>  deleteSpecificUser: Start method.");
        return this.userInfoRepository.deleteByEmail(email);
    }


    /**
     * Updates user information based on the provided email.
     *
     * @param userIdentifier        The email of the user to be updated.
     * @param updatedUserInfo       The updated user information.
     * @return The updated user.
     * @throws IllegalArgumentException If the user is not found.
     */
    public UserInfo updateUserInfo(String userIdentifier, UserInfo updatedUserInfo) {
        logger.info("UserService>>  updateUserInfo: Start method.");

        // Check if the user exists
        UserInfo existingUser = this.getUserByIdentifier(userIdentifier);

        if (existingUser != null) {
            // Perform the update with the new information
            existingUser.setUserName(updatedUserInfo.getUserName());
            existingUser.setFirstName(updatedUserInfo.getFirstName());
            existingUser.setSurName(updatedUserInfo.getSurName());
            existingUser.setPhone(updatedUserInfo.getPhone());

            // Save the updated user back to the repository
            userInfoRepository.save(existingUser);

            logger.info("UserService >> updateUserInfoByEmail: User updated successfully.");
            return existingUser;
        } else {
            logger.severe("UserService >> updateUserInfoByEmail: User not found.");
            throw new IllegalArgumentException("User not found. Cannot update information.");
        }
    }

}
