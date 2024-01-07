package com.orengolan.cheaptrips.airline.cheaptripsapp;

import com.orengolan.cheaptrips.city.City;
import com.orengolan.cheaptrips.city.CityService;
import com.orengolan.cheaptrips.flight.Flight;
import com.orengolan.cheaptrips.flight.FlightService;
import com.orengolan.cheaptrips.news.News;
import com.orengolan.cheaptrips.news.NewsService;
import com.orengolan.cheaptrips.opentripmap.OpenTripMapService;
import com.orengolan.cheaptrips.opentripmap.PlacesData;
import com.orengolan.cheaptrips.userinformation.UserInfo;
import com.orengolan.cheaptrips.userinformation.UserInfoRequest;
import com.orengolan.cheaptrips.userinformation.UserInfoService;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Logger;

/**
 * The {@code CheapTripsService} class is the central service responsible for generating and managing cheap trip data.
 * It coordinates interactions between various components such as user information, flight details, news updates,
 * OpenTripMap data, and city information. This service handles the logic for generating monthly and date-specific
 * cheap trip responses, retrieving user information, and saving user trip history.
 *
 * Key Features:
 * - Generates comprehensive trip responses combining flight details, news updates, and OpenTripMap data.
 * - Manages user information, including fetching, updating, and saving user trip history.
 * - Coordinates interactions with external services like FlightService, NewsService, OpenTripMapService, and CityService.
 * - Supports searching for city information based on the city name.
 *
 * Example Usage:
 * CheapTripsService cheapTripsService = new CheapTripsService(userInfoService, flightService, newsService, openTripMapService, cityService);
 * UserInfo userInfo = cheapTripsService.retrieveUserByIdentified("user@example.com");
 * CheapTripRequest cheapTripRequest = new CheapTripRequest();
        * CheapTripsResponse monthlyTripResponse = cheapTripsService.generateTripMonthly(cheapTripRequest, userInfo);
        * // Access and utilize the generated trip response and user information as needed.
        */
@Service
public class CheapTripsService {
    private static final Logger logger = Logger.getLogger(CheapTripsService.class.getName());
    private final UserInfoService userInfoService;
    private final FlightService flightService;
    private final NewsService newsService;
    private final OpenTripMapService openTripMapService;
    private final CityService cityService;


    public CheapTripsService(UserInfoService userInfoService, FlightService flightService, NewsService newsService, OpenTripMapService openTripMapService,CityService cityService) {
        this.userInfoService = userInfoService;
        this.flightService = flightService;
        this.newsService = newsService;
        this.openTripMapService = openTripMapService;
        this.cityService = cityService;
    }

    public UserInfo newUser(UserInfoRequest userInfoRequest,String userNameToken){
        logger.info("CheapTripsService>>  newUser: Start method.");
        UserInfo userInfo = userInfoRequest.toUserInto(userNameToken);
        logger.info("CheapTripsService>>  newUser: Created user info object.");
        this.userInfoService.createNewUser(userInfo);

        logger.info("CheapTripsService>>  newUser: End method.");
        return userInfo;
    }

    public UserInfo retrieveUserByIdentified(String userName){
        return this.userInfoService.getUserByIdentifier(userName);
    }

    public CheapTripsResponse generateNewByTrip(CheapTripsRequest cheapTripsRequest,String departure_at,String return_at,UserInfo userInfo,boolean cheak_dates) throws ParseException, IOException {
        logger.info("CheapTripsService>>  generateNewTrip: Start method.");

        if (cheak_dates){
            // Validate depart_date
            if (departure_at == null || isDateFormatInvalid(departure_at)) {
                throw new IllegalArgumentException("Invalid depart_date format. Please use yyyy-MM-dd.");
            }
            // Validate return_date
            if (return_at == null || isDateFormatInvalid(return_at)) {
                throw new IllegalArgumentException("Invalid return_date format. Please use yyyy-MM-dd.");
            }
        }

        // Create a flight object.
        Flight flight = this.flightService.findFlight(
                cheapTripsRequest.getOrigin_cityIATACode(),
                cheapTripsRequest.getDestination_cityIATACode(),
                departure_at,
                return_at
        );
        logger.info("CheapTripsService>>  generateNewTrip: Flight created successfully.");


        // Create news object by the destination city name.
        News news = this.newsService.getNews(flight.getDestination().getCity().getCityName(),10);
        logger.info("CheapTripsService>>  generateNewTrip: News created successfully.");

        // Fetches the data of tourist place from db.
        PlacesData placesData;
        placesData = this.openTripMapService.getSpecificPlace(flight.getDestination().getCity().getCityName());


        if (placesData == null){
            // Not found tourist data then by destination data fetches the data from API.
            placesData = new PlacesData(
                    cheapTripsRequest.getRadius(),
                    flight.getDestination().getCity().getLonCoordinates(),
                    flight.getDestination().getCity().getLatCoordinates(),
                    cheapTripsRequest.getLimitPlaces(),
                    flight.getDestination().getCity().getCityName(),
                    flight.getDestination().getCity().getCountryIATACode()
            );
        }
        logger.info("CheapTripsService>>  generateNewTrip: News created successfully.");

        logger.info("CheapTripsService>>  generateNewTrip: End method.");
        CheapTripsResponse cheapTripsResponse = new CheapTripsResponse(flight,news,this.openTripMapService.generatePlaces(placesData, cheapTripsRequest.getKinds()));
        this.saveUserTrip(userInfo,cheapTripsResponse);
        return cheapTripsResponse;
    }

    public List<City> searchCity(String cityName){
        return this.cityService.fetchSpecificCityByName(cityName.toLowerCase());
    }


    private UserInfo saveUserTrip(UserInfo userInfo, CheapTripsResponse cheapTripsResponse) {
        logger.info("CheapTripsService>>  saveUserTrip: Start method.");
        userInfo.setTripHistory(cheapTripsResponse);
        this.userInfoService.deleteSpecificUser(userInfo.getEmail());
        logger.info("CheapTripsService>>  saveUserTrip: End method.");
        return this.userInfoService.createNewUser(userInfo);
    }


    private boolean isDateFormatInvalid(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        sdf.parse(date); // Try to parse the date; if successful, the method won't throw an exception
        return false;
    }

}
