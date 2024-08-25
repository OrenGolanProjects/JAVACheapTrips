package com.orengolan.cheaptrips.userinformation;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * The {@code UserInfoRepository} interface is a Spring Data MongoDB repository for managing user information in the CheapTrips application.
 * It extends the {@link org.springframework.data.mongodb.repository.MongoRepository} interface, providing CRUD operations for the "userinfo" collection.
 *
 * Key Features:
 * - Utilizes Spring Data MongoDB to simplify database interactions.
 * - Defines custom query methods for finding user information by email and username.
 * - Inherited methods include save, findById, findAll, deleteById, and more.
 *
 * Example Usage:
 * Developers can use this interface to perform database operations related to user information.
 * Methods such as findUserByEmail and findUserByUserName enable retrieval of user details based on specific criteria.
 * The interface seamlessly integrates with MongoDB, allowing developers to focus on application logic.
 *
 * Note: Custom query methods follow Spring Data JPA naming conventions, providing a convenient way to express queries.
 */
public interface UserInfoRepository extends MongoRepository<UserInfo,String> {

    /**
     * Find a user by their email address.
     *
     * @param email The email address of the user.
     * @return The user information if found, null otherwise.
     */
    UserInfo findUserByEmail(String email);


    /**
     * Delete a user by their email address.
     *
     * @param email The email address of the user to be deleted.
     * @return The user information before deletion.
     */
    UserInfo deleteByEmail(String email);



    /**
     * Find a user by their username.
     *
     * @param userName The username of the user.
     * @return The user information if found, null otherwise.
     */
    UserInfo findUserByUserName(String userName);
}
