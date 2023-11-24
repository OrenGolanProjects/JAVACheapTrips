package com.orengolan.cheaptrips.cheaptripsapp;

import com.orengolan.cheaptrips.usersearch.UserInfo;
import com.orengolan.cheaptrips.usersearch.UserInfoRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Logger;

@RestController
@RequestMapping("/app/cheap-trip")
public class CheapTripController {

    private static final Logger logger = Logger.getLogger(CheapTripController.class.getName());
    private final CheapTripsService cheapTripsService;
    public CheapTripController(CheapTripsService cheapTripsService) {
        this.cheapTripsService = cheapTripsService;
    }

    @RequestMapping(value="/generate-monthly-trip", method = RequestMethod.POST)
    public UserInfo generateMonthlyTrip(@Valid @RequestBody CheapTripsRequestMonthly cheapTripsRequestMonthly, HttpServletRequest request) throws ParseException, IOException {

        logger.info("** CheapTripController>>  generateTrip: Start method.");
        UserInfo user = this.retrieveUserInfoByToken(request);
        return this.cheapTripsService.generateTripMonthly(cheapTripsRequestMonthly,user);

    }
    @RequestMapping(value="/generate-trip-by-dates", method = RequestMethod.POST)
    public UserInfo generateTripByDates(@Valid @RequestBody CheapTripsRequestByDates cheapTripsRequestByDates, HttpServletRequest request) throws ParseException, IOException {

        logger.info("** CheapTripController>>  generateTrip: Start method.");
        UserInfo user = this.retrieveUserInfoByToken(request);
        return this.cheapTripsService.generateTripByDates(cheapTripsRequestByDates,user);

    }

    @RequestMapping(value="/create-user", method = RequestMethod.POST)
    public UserInfo createUser(@Valid @RequestBody UserInfoRequest userInfoRequest){
        logger.info("** CheapTripController>>  createUser: Start method.");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return this.cheapTripsService.newUser(userInfoRequest,username);

    }


    private UserInfo retrieveUserInfoByToken(HttpServletRequest request) throws AuthenticationException {

        String authorizationHeader = request.getHeader("Authorization");

        String username;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            username = authentication.getName();

        } else {
            logger.warning("No valid JWT token found in the request.");
            throw new AuthenticationException("Invalid JWT token") {
            };
        }
        return this.cheapTripsService.retrieveUserByIdentified(username);

    }



}
