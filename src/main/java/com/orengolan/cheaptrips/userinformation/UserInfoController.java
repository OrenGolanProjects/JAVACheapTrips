package com.orengolan.cheaptrips.userinformation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Pattern;
import java.util.logging.Logger;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping("/app/userinfo")
@Api(tags = "User Management", description = "Endpoints for user information management.")
public class UserInfoController {
    private static final Logger logger = Logger.getLogger(UserInfoController.class.getName());
    private UserInfoRepository usersRepository;
    private final UserInfoService userService;

    public UserInfoController(UserInfoService userService) {
        this.userService = userService;
    }

    @RequestMapping(value="/get-specific-user-info", method = RequestMethod.GET)
    @ApiOperation(value = "Get specific user information", notes = "Retrieve information for a specific user by email.")
    public UserInfo getSpecificUser(@RequestParam String userIdentifier){
        logger.info("** UserController>>  getSpecificUser: Start method.");
        return this.userService.getUserByIdentifier(userIdentifier);
    }

    @RequestMapping(value="/create-specific-user-info", method = RequestMethod.POST)
    @ApiOperation(value = "Create specific user information", notes = "Create a new user with specific information.")
    public ResponseEntity<UserInfo> createSpecificUser(@RequestBody UserInfoRequest userInfoRequest, @RequestParam @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Invalid email format") String email) {
        logger.info("** UserController>>  deleteAllUsers: Start method.");
        return ResponseEntity.status(201).body(this.userService.createNewUser(userInfoRequest.toUserInto(email)));
    }

    @RequestMapping(value = "/update-specific-user-info", method = RequestMethod.PUT)
    @ApiOperation(value = "Update specific user information", notes = "Update information for a specific user by email or username.")
    public ResponseEntity<UserInfo> updateSpecificUser(@RequestBody UserInfoRequest updatedUserInfoRequest,HttpServletRequest request) {
        logger.info("** UserController>>  updateSpecificUser: Start method.");

        String authorizationHeader = request.getHeader("Authorization");

        String email;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            email = authentication.getName();

        } else {
            logger.warning("No valid JWT token found in the request.");
            throw new AuthenticationException("Invalid JWT token") {
            };
        }
        UserInfo updatedUser = this.userService.updateUserInfo(email, updatedUserInfoRequest.toUserInto(email));
        return ResponseEntity.ok(updatedUser);
    }


    @RequestMapping(value="/delete-specific-user-info", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete specific user information", notes = "Delete a specific user by email.")
    public UserInfo deleteSpecificUser(HttpServletRequest request){
        logger.info("** UserController>>  deleteSpecificUser: Start method.");
        String authorizationHeader = request.getHeader("Authorization");

        String email;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            email = authentication.getName();

        } else {
            logger.warning("No valid JWT token found in the request.");
            throw new AuthenticationException("Invalid JWT token") {
            };
        }
        return this.userService.deleteSpecificUser(email);
    }


}
