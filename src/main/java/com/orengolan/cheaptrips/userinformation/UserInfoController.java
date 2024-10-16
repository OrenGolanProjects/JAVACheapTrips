package com.orengolan.cheaptrips.userinformation;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;


/**
 * The {@code UserInfoController} class handles endpoints related to user information management.
 * It provides functionalities to retrieve, create, update, and delete specific user information.
 * The class utilizes Spring Security for authentication and authorization.
 *
 * Endpoints:
 * - GET /app/userinfo/get-specific-user-info: Retrieve information for a specific user by email.
 * - POST /app/userinfo/create-specific-user-info: Create a new user with specific information.
 * - PUT /app/userinfo/update-specific-user-info: Update information for a specific user by email or username.
 * - DELETE /app/userinfo/delete-specific-user-info: Delete a specific user by email.
 *
 * Security:
 * - Endpoints are secured with JWT authentication. A valid JWT token in the request header is required for authorization.
 * - Invalid or missing tokens will result in an AuthenticationException.
 *
 * @see UserInfoService
 * @see UserInfoRequest
 * @see UserInfo
 */
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

    // ==============================================
    // ============= get-user-info ==================
    // ==============================================
    @RequestMapping(value="/get-specific-user-info", method = RequestMethod.GET)
    @ApiOperation(value = "Get specific user information", notes = "Retrieve information for a specific user by email.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully retrieved user information"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public UserInfo getSpecificUser(HttpServletRequest request){
        logger.info("** UserController>>  getSpecificUser: Start method.");
        return this.userService.getUserByIdentifier(this.retrieveUserIdentifier(request));
    }


    @ApiIgnore
    @RequestMapping(value="/create-specific-user-info", method = RequestMethod.POST)
    @ApiOperation(value = "Create specific user information", notes = "Create a new user with specific information.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "User successfully created"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 500, message = "Internal server error")
    })    public ResponseEntity<UserInfo> createSpecificUser(@RequestBody UserInfoRequest userInfoRequest,HttpServletRequest request) throws BindException {
        logger.info("** UserController>>  deleteAllUsers: Start method.");

        return ResponseEntity.status(201).body(this.userService.createNewUser(userInfoRequest.toUserInfo(this.retrieveUserIdentifier(request))));
    }

    // ==============================================
    // ============= UPDATE-USER-INFO ===============
    // ==============================================
    @RequestMapping(value = "/update-specific-user-info", method = RequestMethod.PUT)
    @ApiOperation(value = "Update specific user information", notes = "Update information for a specific user by email or username.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User information successfully updated"),
            @ApiResponse(code = 400, message = "Invalid request data"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<UserInfo> updateSpecificUser(@RequestBody UserInfoRequest updatedUserInfoRequest,HttpServletRequest request) {
        logger.info("** UserController>>  updateSpecificUser: Start method.");
        String email = this.retrieveUserIdentifier(request);

        UserInfo updatedUser = this.userService.updateUserInfo(email, updatedUserInfoRequest.toUserInfo(email));
        return ResponseEntity.ok(updatedUser);
    }


    // ==============================================
    // ============= DELETE-USER-INFO ===============
    // ==============================================
    @RequestMapping(value="/delete-specific-user-info", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete specific user information", notes = "Delete a specific user by email.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User successfully deleted"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })

    public ResponseEntity<UserInfo> deleteSpecificUser(HttpServletRequest request){
        logger.info("** UserController>>  deleteSpecificUser: Start method.");

        return ResponseEntity.ok(this.userService.deleteSpecificUser(this.retrieveUserIdentifier(request)));
    }

    private String retrieveUserIdentifier(HttpServletRequest request) throws AuthenticationException {

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

        return email;
    }

}
