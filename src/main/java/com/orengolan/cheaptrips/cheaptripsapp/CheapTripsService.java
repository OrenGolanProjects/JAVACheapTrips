package com.orengolan.cheaptrips.cheaptripsapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.orengolan.cheaptrips.airport.AirportService;
import com.orengolan.cheaptrips.city.City;
import com.orengolan.cheaptrips.city.CityService;
import com.orengolan.cheaptrips.flight.Flight;
import com.orengolan.cheaptrips.flight.FlightService;
import com.orengolan.cheaptrips.countries.CountryService;
import com.orengolan.cheaptrips.news.News;
import com.orengolan.cheaptrips.news.NewsService;
import com.orengolan.cheaptrips.opentripmap.OpenTripMapService;
import com.orengolan.cheaptrips.opentripmap.PlacesData;
import com.orengolan.cheaptrips.userinformation.UserInfo;
import com.orengolan.cheaptrips.userinformation.UserInfoRequest;
import com.orengolan.cheaptrips.userinformation.UserInfoService;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * The {@code CheapTripsService} class is the central service responsible for generating and managing cheap trip data.
 * It coordinates interactions between various components such as user information, flight details, news updates,
 * OpenTripMap data, and city information. This service handles the logic for generating monthly and date-specific
 * cheap trip responses, retrieving user information, and saving user trip history.

 * Key Features:
 * - Generates comprehensive trip responses combining flight details, news updates, and OpenTripMap data.
 * - Manages user information, including fetching, updating, and saving user trip history.
 * - Coordinates interactions with external services like FlightService, NewsService, OpenTripMapService, and CityService.
 * - Supports searching for city information based on the city name.

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
    private final AirportService airportService;
    private final ObjectMapper objectMapper;
    private final CountryService countryService;


    public CheapTripsService(UserInfoService userInfoService, FlightService flightService, NewsService newsService, OpenTripMapService openTripMapService,CityService cityService,AirportService airportService,ObjectMapper objectMapper,CountryService countryService) {
        this.userInfoService = userInfoService;
        this.flightService = flightService;
        this.newsService = newsService;
        this.openTripMapService = openTripMapService;
        this.cityService = cityService;
        this.airportService = airportService;
        this.objectMapper = objectMapper;
        this.countryService = countryService;
    }

    public UserInfo newUser(UserInfoRequest userInfoRequest,String userNameToken) throws BindException {
        logger.info("CheapTripsService>>  newUser: Start method.");
        UserInfo userInfo = userInfoRequest.toUserInfo(userNameToken);
        logger.info("CheapTripsService>>  newUser: Created user info object.");
        this.userInfoService.createNewUser(userInfo);

        logger.info("CheapTripsService>>  newUser: End method.");
        return userInfo;
    }

    public UserInfo retrieveUserByIdentified(String userName){
        return this.userInfoService.getUserByIdentifier(userName);
    }

    public CheapTripsResponse generateNewByTrip(CheapTripsRequest cheapTripsRequest,String departure_at,String return_at,UserInfo userInfo,boolean cheak_dates) throws ParseException, IOException, BindException {
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

    private void saveUserTrip(UserInfo userInfo, CheapTripsResponse cheapTripsResponse) throws BindException {
        logger.info("CheapTripsService>>  saveUserTrip: Start method.");
        userInfo.setTripHistory(cheapTripsResponse);
        this.userInfoService.deleteSpecificUser(userInfo.getEmail());
        logger.info("CheapTripsService>>  saveUserTrip: End method.");
        this.userInfoService.createNewUser(userInfo);
    }

    private boolean isDateFormatInvalid(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        sdf.parse(date); // Try to parse the date; if successful, the method won't throw an exception
        return false;
    }

    public List<JsonNode> getCombinedCityAirportData() throws IOException {
        logger.info("CheapTripsService>> getCombinedCityAirportData: Start method");

        String fileName = "/tmp/combined_city_airport_data.json";
        File jsonFile = new File(fileName);

        // If the file exists and is not empty, read from the JSON file
        if (jsonFile.exists() && Files.size(Paths.get(fileName)) > 0) {
            logger.info("CheapTripsService>> getCombinedCityAirportData: File exists and is not empty, reading from JSON.");
            return readFromJson(jsonFile);
        }

        // Fetch city, airport, and country data
        JsonNode cityList = objectMapper.readTree(cityService.getCitiesData());
        JsonNode airportList = objectMapper.readTree(airportService.getAirportsData());
        JsonNode countryList = objectMapper.readTree(countryService.getCountriesData());

        // Create maps to store cities and countries by their code for quick lookup
        Map<String, JsonNode> cityMap = new HashMap<>();
        for (JsonNode city : cityList) {
            String cityCode = city.path("code").asText();
            cityMap.put(cityCode, city);
        }

        Map<String, JsonNode> countryMap = new HashMap<>();
        for (JsonNode country : countryList) {
            String countryCode = country.path("code").asText();
            countryMap.put(countryCode, country);
        }

        // Combine city, airport, and country data
        List<JsonNode> combinedData = new ArrayList<>();
        for (JsonNode airport : airportList) {
            String cityCode = airport.path("airport").get("city_code").asText();
            JsonNode city = cityMap.get(cityCode);
            String countryCode = airport.path("airport").get("country_code").asText();
            JsonNode country = countryMap.get(countryCode);

            if (city != null && country != null) {
                ObjectNode combinedNode = objectMapper.createObjectNode();

                // Add airport data if it exists
                if (airport.has("airport") ) {
                    ObjectNode airportNode = objectMapper.createObjectNode();
                    airportNode.put("code", airport.path("airport").get("code").asText());
                    airportNode.put("name", airport.path("airport").get("name").asText());
                    combinedNode.set("airport", airportNode);
                }

                // Add city data if it exists
                if (city.has("code") && city.has("name_translations") && city.get("name_translations").has("en")) {
                    ObjectNode cityNode = objectMapper.createObjectNode();
                    cityNode.put("code", city.path("code").asText());
                    cityNode.put("name", city.get("name_translations").path("en").asText());
                    combinedNode.set("city", cityNode);
                }

                // Add country data if it exists
                if (country.has("code") && country.has("name_translations") && country.get("name_translations").has("en")) {
                    ObjectNode countryNode = objectMapper.createObjectNode();
                    countryNode.put("code", country.path("code").asText());
                    countryNode.put("name", country.get("name_translations").path("en").asText());
                    combinedNode.set("country", countryNode);
                }

                // Only add to combinedData if at least one of the fields was added
                if (!combinedNode.isEmpty()) {
                    combinedData.add(combinedNode);
                }
            }
        }

        // Write the combined data to JSON file
        writeToJson(combinedData, jsonFile);
        return combinedData;
    }


    private List<JsonNode> readFromJson(File jsonFile) throws IOException {
        // Read JSON array from file and convert it to a List<JsonNode>
        JsonNode jsonNodeArray = objectMapper.readTree(jsonFile);
        List<JsonNode> result = new ArrayList<>();
        if (jsonNodeArray.isArray()) {
            for (JsonNode node : jsonNodeArray) {
                result.add(node);
            }
        }
        return result;
    }

    private void writeToJson(List<JsonNode> data, File jsonFile) throws IOException {
        // Write the List<JsonNode> to a JSON file
        try (FileWriter writer = new FileWriter(jsonFile)) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, data);
        }
    }
}
