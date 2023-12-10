package com.orengolan.cheaptrips.usersearch;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.logging.Logger;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;


@RestController
@RequestMapping("/app/userinfo")
@Api(tags = "User Info Operations", description = "Endpoints for managing user information")
public class UserInfoController {
    private static final Logger logger = Logger.getLogger(UserInfoController.class.getName());
    private UserInfoRepository usersRepository;
    private final UserInfoService userService;

    public UserInfoController(UserInfoService userService) {
        this.userService = userService;
    }

    @RequestMapping(value="/get-specific-user-info", method = RequestMethod.GET)
    @ApiOperation(value = "Get specific user information", notes = "Retrieve information for a specific user by email.")
    public UserInfo getSpecificUser(@RequestParam String email){
        logger.info("** UserController>>  getSpecificUser: Start method.");
        return this.userService.getSpecificUserByEmail(email);
    }

    @RequestMapping(value="/create-specific-user-info", method = RequestMethod.POST)
    @ApiOperation(value = "Create specific user information", notes = "Create a new user with specific information.")
    public ResponseEntity<UserInfo> createSpecificUser(@RequestBody UserInfoRequest userInfoRequest,
                                                       @RequestParam @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Invalid email format") String email) {
        logger.info("** UserController>>  deleteAllUsers: Start method.");
        return ResponseEntity.status(201).body(this.userService.createNewUser(userInfoRequest.toUserInto(email)));
    }

    @RequestMapping(value="/delete-specific-user-info", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete specific user information", notes = "Delete a specific user by email.")
    public UserInfo deleteSpecificUser(@RequestParam String email){
        logger.info("** UserController>>  deleteSpecificUser: Start method.");
        return this.userService.deleteSpecificUser(email);
    }

}
