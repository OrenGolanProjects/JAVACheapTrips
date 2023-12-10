package com.orengolan.cheaptrips.cheaptripsapp;

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
import java.util.logging.Logger;

@Service
public class CheapTripsService {
    private static final Logger logger = Logger.getLogger(CheapTripsService.class.getName());
    private final UserInfoService userInfoService;
    private final FlightService flightService;
    private final NewsService newsService;
    private final OpenTripMapService openTripMapService;

    public CheapTripsService(UserInfoService userInfoService, FlightService flightService, NewsService newsService, OpenTripMapService openTripMapService) {
        this.userInfoService = userInfoService;
        this.flightService = flightService;
        this.newsService = newsService;
        this.openTripMapService = openTripMapService;
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

    public CheapTripsResponse generateNewByTrip(CheapTripsRequest cheapTripsRequest,String departure_at,String return_at,UserInfo userInfo) throws ParseException, IOException {
        logger.info("CheapTripsService>>  generateNewTrip: Start method.");

        Flight flight = this.flightService.findFlight(
                cheapTripsRequest.getOrigin_cityIATACode(),
                cheapTripsRequest.getDestination_cityIATACode(),
                departure_at,
                return_at
        );
        logger.info("CheapTripsService>>  generateNewTrip: Flight created successfully.");

        News news = this.newsService.getNews(flight.getDestination().getCity().getCityName(),20);
        logger.info("CheapTripsService>>  generateNewTrip: News created successfully.");

        PlacesData placesData;
        placesData = this.openTripMapService.getSpecificPlace(flight.getDestination().getCity().getCityName());

        if (placesData == null){
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

    private UserInfo saveUserTrip(UserInfo userInfo, CheapTripsResponse cheapTripsResponse) {

        logger.info("CheapTripsService>>  saveUserTrip: Start method.");
        userInfo.setTripHistory(cheapTripsResponse);
        this.userInfoService.deleteSpecificUser(userInfo.getEmail());
        logger.info("CheapTripsService>>  saveUserTrip: End method.");
        return this.userInfoService.createNewUser(userInfo);
    }



}
