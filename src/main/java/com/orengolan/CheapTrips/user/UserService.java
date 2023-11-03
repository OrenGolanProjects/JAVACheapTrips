package com.orengolan.CheapTrips.user;


import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import java.util.logging.Logger;
import org.apache.commons.validator.routines.EmailValidator;

@Service
public class UserService {

    private final UserRepository userRepository;
    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createNewUser(@NotNull User user) throws ChangeSetPersister.NotFoundException {
        logger.info("UserService>>  createNewUser: Start method.");

        if(this.userRepository.findUserByEmail(user.getEmail()) ==null){
            this.userRepository.save(user);
            logger.info("UserService>>  createNewUser: End method.");
            return user;

        }
        logger.severe("UserService>>  createNewUser: Did not found user.");
        throw new ChangeSetPersister.NotFoundException();
    }

    public User getSpecificUser(String email) {
        logger.info("UserService>>  getSpecificUser: Start method.");
        if(!isValidEmail(email)){
            throw new IllegalArgumentException("Invalid email.");
        }
        logger.info("UserService>>  getSpecificUser: End method.");
        return this.userRepository.findUserByEmail(email);
    }

    public static boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public User deleteSpecificUser(String email) {
        logger.info("UserService>>  deleteSpecificUser: Start method.");
        return this.userRepository.deleteByEmail(email);
    }

}
