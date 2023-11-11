package com.orengolan.cheaptrips.usersearch;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/userinfo")
public class UserInfoController {
    private static final Logger logger = Logger.getLogger(UserInfoController.class.getName());
    private UserInfoRepository usersRepository;
    private final UserInfoService userService;

    public UserInfoController(UserInfoService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/get-all-users-info", method = RequestMethod.GET)
    public List<UserInfo> getAllUsers()  {
        logger.info("** UserController>>  getAllUsers: Start method.");
        return this.usersRepository.findAll();
    }

    @RequestMapping(value="/get-specific-user-info", method = RequestMethod.GET)
    public UserInfo getSpecificUser(@RequestParam String email){
        logger.info("** UserController>>  getSpecificUser: Start method.");
        return this.userService.getSpecificUserByEmail(email);
    }

    @RequestMapping(value="/create-specific-user-info", method = RequestMethod.POST)
    public ResponseEntity<UserInfo> createSpecificUser(@RequestBody UserInfoRequest userInfoRequest,
                                                       @RequestParam @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Invalid email format") String email) {


        logger.info("** UserController>>  deleteAllUsers: Start method.");
        return ResponseEntity.status(201).body(this.userService.createNewUser(userInfoRequest.toUserInto(email)));
    }

    @RequestMapping(value = "/delete-all-users-info", method = RequestMethod.DELETE)
    public Boolean deleteAllUsers()  {
        logger.info("** UserController>>  deleteAllUsers: Start method.");
        this.usersRepository.deleteAll();
        return true;
    }

    @RequestMapping(value="/delete-specific-user-info", method = RequestMethod.DELETE)
    public UserInfo deleteSpecificUser(@RequestParam String email){
        logger.info("** UserController>>  deleteSpecificUser: Start method.");
        return this.userService.deleteSpecificUser(email);
    }

}
