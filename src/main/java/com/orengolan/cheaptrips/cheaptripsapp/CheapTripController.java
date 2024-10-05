package com.orengolan.cheaptrips.cheaptripsapp;

import com.orengolan.cheaptrips.city.City;
import com.orengolan.cheaptrips.service.RateLimitExceededException;
import com.orengolan.cheaptrips.userinformation.UserInfo;
import io.github.bucket4j.Bucket;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.JsonNode;


/**
 * The {@code CheapTripController} class handles the RESTful API endpoints related to user and travel services management.
 * It provides operations for generating monthly and date-specific trip responses, searching for cities, and retrieving
 * user information using JSON Web Tokens (JWT) for authentication.
 * <br><br>
 * Key Features:
 * - Exposes endpoints for generating monthly and date-specific trip responses based on user-provided parameters.
 * - Implements JWT-based authentication to securely retrieve user information.
 * - Supports searching for cities based on the city name.
 * - Utilizes Swagger annotations for API documentation.
 * <br><br>
 * Example Usage:
 * // Access the endpoints defined in this controller to perform operations related to user and travel services.
 * // Ensure proper authentication by including a valid JWT token in the request headers.
 */
@RestController
@RequestMapping("/cheap-trip")
@Api(tags = "CheapTripsApp", description = "User & Travel Services Management.")
public class CheapTripController {

    private static final Logger logger = Logger.getLogger(CheapTripController.class.getName());
    private final CheapTripsService cheapTripsService;
    private final Bucket rateLimitBucket;  // Inject the Bucket bean

    public CheapTripController(CheapTripsService cheapTripsService, Bucket rateLimitBucket) {
        this.cheapTripsService = cheapTripsService;
        this.rateLimitBucket = rateLimitBucket;
    }



    @RequestMapping(value="/generate-monthly-trip", method = RequestMethod.POST)
    @ApiOperation(
            value = "Generate Monthly Trip",
            notes = "**Travel Search Parameters:**\n" +
                    "- Origin_cityIATACode: IATA code representing the departure city(e.g., TLV).\n" +
                    "- Destination_cityIATACode: IATA code representing the destination city(e.g., AMS).\n" +
                    "- Destination_cityName: City name used for searching news related to the destination.\n" +
                    "- Radius: Specifies the search distance for identifying travel destinations, measured in meters (e.g., 10,000 to 50,000).\n" +
                    "- LimitPlaces: The maximum number of travel places to be returned in the results."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully generated monthly trip"),
            @ApiResponse(code = 400, message = "Bad request, check the request parameters"),
            @ApiResponse(code = 401, message = "Unauthorized, authentication failure"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public CheapTripsResponse generateMonthlyTrip(@Valid @RequestBody CheapTripsRequest cheapTripsRequest, HttpServletRequest request) throws ParseException, IOException, BindException {
        logger.info("** CheapTripController>>  generateTrip: Start method.");

        // Rate limiting check
        if (!rateLimitBucket.tryConsume(1)) {
            throw new RateLimitExceededException("Rate limit exceeded. Try again later.");
        }

        UserInfo user = this.retrieveUserInfoByToken(request);
        return this.cheapTripsService.generateNewByTrip(cheapTripsRequest, null, null,user,false);
    }

    @RequestMapping(value="/generate-trip-by-dates", method = RequestMethod.POST)
    @ApiOperation(
            value = "Generate Trip by Dates",
            notes = "**Travel Search Parameters:**\n" +
                    "- Origin_cityIATACode: IATA code representing the departure city(e.g., TLV).\n" +
                    "- Destination_cityIATACode: IATA code representing the destination city(e.g., AMS).\n" +
                    "- Destination_cityName: City name used for searching news related to the destination.\n" +
                    "- Radius: Specifies the search distance for identifying travel destinations, measured in meters (e.g., 10,000 to 50,000).\n" +
                    "- LimitPlaces: The maximum number of travel places to be returned in the results.\n" +
                    "- **Departure_at**: Date of departure in the format yyyy-mm-dd.\n" +
                    "- **Return_at**: Date of return in the format yyyy-mm-dd."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully generated trip by dates"),
            @ApiResponse(code = 400, message = "Bad request, check the request parameters"),
            @ApiResponse(code = 401, message = "Unauthorized, authentication failure"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public CheapTripsResponse generateTripByDates(
            @Valid @RequestBody CheapTripsRequest cheapTripsRequest,
            @RequestParam(defaultValue = "yyyy-MM-dd") String depart_date,
            @RequestParam(defaultValue = "yyyy-MM-dd") String return_date,
            HttpServletRequest request) throws ParseException, IOException, BindException {
        logger.info("** CheapTripController>>  generateTrip: Start method.");

        // Rate limiting check
        if (!rateLimitBucket.tryConsume(1)) {
            throw new RateLimitExceededException("Rate limit exceeded. Try again later.");
        }

        UserInfo user = this.retrieveUserInfoByToken(request);
        return this.cheapTripsService.generateNewByTrip(cheapTripsRequest, depart_date, return_date,user,true);
    }


    @RequestMapping(value="/city-search", method = RequestMethod.GET)
    @ApiOperation(
            value = "Search Cities",
            notes = "**Search city by name:**\n" +
                    "- CityName: The name of the target city for the search."

    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully retrieved cities"),
            @ApiResponse(code = 400, message = "Bad request, check the request parameters"),
            @ApiResponse(code = 401, message = "Unauthorized, authentication failure"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public List<City> CitySearchResult(@RequestParam String cityName) {
        // Rate limiting check
        if (!rateLimitBucket.tryConsume(1)) {
            throw new RateLimitExceededException("Rate limit exceeded. Try again later.");
        }
        return this.cheapTripsService.searchCity(cityName);
    }

    @RequestMapping(value="/combined-city-airport-data", method = RequestMethod.GET)
    @ApiOperation(
            value = "Get Combined City and Airport Data",
            notes = "Retrieves a combined list of airports and their corresponding city data."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully retrieved combined city and airport data"),
            @ApiResponse(code = 429, message = "Rate limit exceeded"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<List<JsonNode>> getCombinedCityAirportData(HttpServletRequest request) {
        logger.info("CheapTripController>> getCombinedCityAirportData: Start method");

        // Rate limiting check
        if (!rateLimitBucket.tryConsume(1)) {
            throw new RateLimitExceededException("Rate limit exceeded. Try again later.");
        }

        try {
            List<JsonNode> combinedData = cheapTripsService.getCombinedCityAirportData();
            return ResponseEntity.ok(combinedData);
        } catch (IOException e) {
            logger.severe("Error occurred while fetching combined city and airport data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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