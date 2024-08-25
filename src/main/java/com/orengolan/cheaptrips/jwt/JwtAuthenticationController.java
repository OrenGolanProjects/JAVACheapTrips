package com.orengolan.cheaptrips.jwt;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindException;
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
 * DBUserService) are configured and injected appropriately for proper functionality.
 */
@RestController
@CrossOrigin
@Api(tags = "JWT Authentication ")
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
    AuthenticationManager am;



    // ===========================
    // ==== authenticate User ====
    // ===========================
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
                    "- **Email:** Must be a valid email format (e.g., user@example.com).\n" +
                    "- **Password:** Must contain at least one digit, one lowercase letter, one uppercase letter, no whitespace, and be at least 8 characters long."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully generated JWT token"),
            @ApiResponse(code = 201, message = "Successfully generated JWT token"),
            @ApiResponse(code = 401, message = "Invalid credentials or user disabled"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody JwtRequest authenticationRequest) {
        try {
            authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());
            final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
            final String token = jwtTokenUtil.generateToken(userDetails);
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User account is disabled");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A user with this email and password does not exist");
        }
    }


    // ===========================
    // ======= Create User =======
    // ===========================
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
                    "- **Email:** Must be a valid email format (e.g., user@example.com).\n" +
                    "- **Password:** Password must be at least 8 characters long and contain at least one letter and one number.\n"+
                    "- **UserName:** Cannot be blank, must be between 3 and 10 characters, and must be unique.\n" +
                    "- **FirstName:** Cannot be blank, must be between 2 and 10 characters.\n" +
                    "- **SurName:** Cannot be blank, must be between 2 and 10 characters.\n" +
                    "- **Phone:** Cannot be blank, must be a 10-digit number."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created the user and generated JWT token"),
            @ApiResponse(code = 500, message = "Internal server error")
    })

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@Valid @RequestBody JwtRequestNewUser jwtRequestNewUser) throws BindException, BindException {

        DBUser user = DBUser.UserBuilder.anUser().email(jwtRequestNewUser.getJwtRequest().getEmail()).password(jwtRequestNewUser.getJwtRequest().getPassword()).build();

        // Save the user in JWT DB.
        userServiceJWT.saveDBUser(user);

        UserDetails userDetails = new User(jwtRequestNewUser.getJwtRequest().getEmail(), user.getPassword(), new ArrayList<>());
        String jwtToken = jwtTokenUtil.generateToken(userDetails);

        // Create & Save the user details.
        userDetailsService.createUserInfo(jwtRequestNewUser.getUserInfoRequest(),userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(new JwtResponse(jwtToken));

    }

    private void authenticate(String email, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
