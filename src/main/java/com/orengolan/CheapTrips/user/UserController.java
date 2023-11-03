package com.orengolan.CheapTrips.user;



import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = Logger.getLogger(UserController.class.getName());
    private final UserRepository usersRepository;
    private final UserService userService;


    public UserController(UserRepository usersRepository, UserService userService) {
        this.usersRepository = usersRepository;
        this.userService = userService;
    }

    @RequestMapping(value = "/get-all-users", method = RequestMethod.GET)
    public ResponseEntity<?> getAllUsers()  {
        logger.info("** UserController>>  getAllUsers: Start method.");
        return ResponseEntity.ok( this.usersRepository.findAll());
    }

    @RequestMapping(value="/get-specific-user", method = RequestMethod.GET)
    public ResponseEntity<?> getSpecificUser(@RequestParam String email){
        logger.info("** UserController>>  getSpecificUser: Start method.");
        return ResponseEntity.status(200).body(this.userService.getSpecificUser(email));
    }

    @RequestMapping(value="/create-specific-user", method = RequestMethod.POST)
    public ResponseEntity<?> createSpecificUser(@Valid @ModelAttribute User user) throws ChangeSetPersister.NotFoundException {
        logger.info("** UserController>>  deleteAllUsers: Start method.");
        return ResponseEntity.status(201).body(this.userService.createNewUser(user));
    }

    @RequestMapping(value = "/delete-all-users", method = RequestMethod.DELETE)
    public Boolean deleteAllUsers()  {
        logger.info("** UserController>>  deleteAllUsers: Start method.");
        this.usersRepository.deleteAll();
        return true;
    }

    @RequestMapping(value="/delete-specific-user", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteSpecificUser(@RequestParam String email){
        logger.info("** UserController>>  deleteSpecificUser: Start method.");
        return ResponseEntity.status(200).body(this.userService.deleteSpecificUser(email));
    }

}
