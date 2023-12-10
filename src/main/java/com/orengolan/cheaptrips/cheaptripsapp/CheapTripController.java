package com.orengolan.cheaptrips.cheaptripsapp;

import com.orengolan.cheaptrips.userinformation.UserInfo;
import com.orengolan.cheaptrips.userinformation.UserInfoRequest;
import io.swagger.annotations.Api;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

@RestController
@RequestMapping("/app/cheap-trip")
@Api (tags = "CheapTripsApp", description = "User & Travel Services Management.")
public class CheapTripController {

    private static final Logger logger = Logger.getLogger(CheapTripController.class.getName());
    private final CheapTripsService cheapTripsService;

    public CheapTripController(CheapTripsService cheapTripsService) {
        this.cheapTripsService = cheapTripsService;
    }

    @RequestMapping(value = "/generate-monthly-trip", method = RequestMethod.POST)
    public CheapTripsResponse generateMonthlyTrip(@Valid @RequestBody CheapTripsRequest cheapTripsRequest, HttpServletRequest request) throws ParseException, IOException {

        logger.info("** CheapTripController>>  generateTrip: Start method.");
        UserInfo user = this.retrieveUserInfoByToken(request);
        return this.cheapTripsService.generateNewByTrip(cheapTripsRequest, null, null,user);
    }

    @RequestMapping(value = "/generate-trip-by-dates", method = RequestMethod.POST)
    public CheapTripsResponse generateTripByDates(
            @Valid @RequestBody CheapTripsRequest cheapTripsRequest,
            @RequestParam(defaultValue = "yyyy-MM-dd", required = true) String depart_date,
            @RequestParam(defaultValue = "yyyy-MM-dd", required = true) String return_date,
            HttpServletRequest request) throws ParseException, IOException {

        logger.info("** CheapTripController>>  generateTrip: Start method.");
        // Validate depart_date
        if (depart_date == null || !isValidDateFormat(depart_date)) {
            throw new IllegalArgumentException("Invalid depart_date format. Please use yyyy-MM-dd.");
        }
        // Validate return_date
        if (return_date == null || !isValidDateFormat(return_date)) {
            throw new IllegalArgumentException("Invalid return_date format. Please use yyyy-MM-dd.");
        }

        UserInfo user = this.retrieveUserInfoByToken(request);
        return this.cheapTripsService.generateNewByTrip(cheapTripsRequest, depart_date, return_date,user);
    }

    @RequestMapping(value = "/create-user", method = RequestMethod.POST)
    public UserInfo createUser(@Valid @RequestBody UserInfoRequest userInfoRequest) {
        logger.info("** CheapTripController>>  createUser: Start method.");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return this.cheapTripsService.newUser(userInfoRequest, username);

    }

    private boolean isValidDateFormat(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            Date parsedDate = sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private UserInfo retrieveUserInfoByToken(HttpServletRequest request) throws AuthenticationException {

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
        return this.cheapTripsService.retrieveUserByIdentified(email);

    }

}