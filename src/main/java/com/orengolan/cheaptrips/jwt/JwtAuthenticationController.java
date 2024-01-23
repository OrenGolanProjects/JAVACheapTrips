package com.orengolan.cheaptrips.jwt;

import com.orengolan.cheaptrips.userinformation.UserInfoRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.ArrayList;

/**
 * The {@code JwtAuthenticationController} class is a REST controller responsible for handling
 * operations related to JWT (JSON Web Token) authentication. It provides endpoints for authenticating
 * users and creating new user accounts with JWT-based authentication.
 *
 * Key Features:
 * - {@code /authenticate} Endpoint: Allows users to authenticate by providing valid credentials
 *   (email and password), generating a JWT token upon successful authentication.
 * - {@code /user} Endpoint: Enables the creation of new user accounts with JWT authentication,
 *   requiring user details such as email and password.
 *
 * Swagger Annotations:
 * - {@code @Api}: Specifies that this class is related to JWT authentication operations.
 * - {@code @ApiOperation}: Describes the purpose and key details of each endpoint.
 * - {@code @ApiResponses}: Defines possible HTTP responses for each endpoint, including success
 *   and error scenarios.
 *
 * Example:
 * The class is utilized in a Spring Boot application to manage user authentication using JWT.
 * It integrates with an {@code AuthenticationManager}, {@code JwtTokenUtil}, {@code JwtUserDetailsService},
 * and a custom {@code DBUserService}. The provided Swagger annotations offer detailed documentation
 * for API consumers, explaining the available operations and expected inputs.
 *
 * Note: Ensure that the necessary Spring beans (AuthenticationManager, JwtTokenUtil, JwtUserDetailsService,
 * DBUserService, and PasswordEncoder) are configured and injected appropriately for proper functionality.
 */
@RestController
@CrossOrigin
@Api(tags = "JWT Authentication ", description = "Operations related to JWT authentication.")
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private DBUserService userServiceJWT;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager am;

    /**
     * Endpoint for creating an authentication token by providing valid credentials.
     *
     * @param authenticationRequest The user's authentication request containing email and password.
     * @return ResponseEntity containing the generated JWT token upon successful authentication.
     * @throws Exception Thrown if authentication fails.
     */
    @ApiOperation(
            value = "Create Authentication Token",
            notes = "Authenticate and generate a JWT token.\n\n" +
                    "**User Authentication Keys:**\n" +
                    "- Email: The email address associated with the user account.\n" +
                    "- Password: The user's password for authentication."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully generated JWT token"),
            @ApiResponse(code = 401, message = "Invalid credentials or user disabled"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody JwtRequest authenticationRequest) throws Exception {
        // check for email validation
        authenticationRequest.validateEmailFormat(authenticationRequest.getEmail());
        authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    /**
     * Endpoint for creating a new user account with JWT authentication.
     *
     * @param jwtRequestNewUser The user's registration request containing email and password.
     * @return ResponseEntity containing the generated JWT token upon successful user creation.
     */
    @ApiOperation(
            value = "Create JwtUser & NewUser",
            notes = "Create a new user with JWT authentication and new user information.\n\n" +
                    "**User Registration Keys:**\n" +
                    "- Email: The email address associated with the user account.\n" +
                    "- Password: The user's password for authentication."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully created the user and generated JWT token"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@Valid @RequestBody JwtRequestNewUser jwtRequestNewUser) {
        // check for email validation
        jwtRequestNewUser.getJwtRequest().validateEmailFormat(jwtRequestNewUser.getJwtRequest().getEmail());
        String encodedPass = passwordEncoder.encode(jwtRequestNewUser.getJwtRequest().getPassword());
        DBUser user = DBUser.UserBuilder.anUser().name(jwtRequestNewUser.getJwtRequest().getEmail()).password(encodedPass).build();

        // Save the user in JWT DB.
        userServiceJWT.save(user);
        UserDetails userDetails = new User(jwtRequestNewUser.getJwtRequest().getEmail(), encodedPass, new ArrayList<>());
        String jwtToken = jwtTokenUtil.generateToken(userDetails);

        // Create & Save the user details.
        userDetailsService.createUserInfo(jwtRequestNewUser.getUserInfoRequest(),userDetails.getUsername());
        return ResponseEntity.ok(new JwtResponse(jwtToken));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
