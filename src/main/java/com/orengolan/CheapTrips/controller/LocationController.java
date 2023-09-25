package com.orengolan.CheapTrips.controller;

import com.mongodb.client.result.UpdateResult;
import com.orengolan.CheapTrips.repository.CountriesRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.orengolan.CheapTrips.model.City;
import com.orengolan.CheapTrips.service.FlightDataAPI;
import com.orengolan.CheapTrips.service.Redis;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final CountriesRepository countriesRepository;
    private final Redis redis;
    private final FlightDataAPI flightDataAPI;
    private final MongoTemplate mongoTemplate;


    @Autowired
    public LocationController(CountriesRepository countriesRepository, Redis redis, FlightDataAPI flightDataAPI,MongoTemplate mongoTemplate ){
        this.countriesRepository = countriesRepository;
        this.flightDataAPI = flightDataAPI;
        this.redis = redis;
        this.mongoTemplate = mongoTemplate;
    }

    private static final Logger logger = Logger.getLogger(LocationController.class.getName());

    @RequestMapping(value = "/get-key-redis-test", method = RequestMethod.GET)
    public String getKey(@RequestParam String key){
        logger.info("Getting key from Redis: " + key);
        return redis.get(key).toString();
    }

    @RequestMapping(value = "/set-key-redis-test", method = RequestMethod.GET)
    public Boolean setKey(@RequestParam String key, @RequestParam String value){
        logger.info("Setting key in Redis: " + key);
        return redis.set(key, value);
    }

    @RequestMapping(value = "/city/{cityName}", method = RequestMethod.GET)
    public ResponseEntity<String> getSpecificCity(@PathVariable String cityName) {
        try{
        logger.info("Getting information for city: " + cityName);
        City city = this.countriesRepository.findByCityName(cityName.toLowerCase());
        if (city == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("City name is not found.");
        }
        return ResponseEntity.ok(city.toString());}
        catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving city information");
        }

    }


    @RequestMapping(value="/get-all-cities", method = RequestMethod.GET)
    public List<City> getAllCities() {
        logger.info("Getting all cities");
        return this.countriesRepository.findAll();
    }

    @RequestMapping(value="/create-specific-city", method = RequestMethod.POST)
    public City createSpecificCity(@RequestParam String cityName, @RequestParam String countryIATA,
                                   @RequestParam String cityIATA, @RequestParam String timeZone,
                                   @RequestParam Double latCoordinates, @RequestParam Double lonCoordinates) {
        if (    cityName == null || cityName.isEmpty() ||
                countryIATA == null || countryIATA.isEmpty() ||
                cityIATA == null || cityIATA.isEmpty() ||
                timeZone == null || timeZone.isEmpty() ||
                latCoordinates == null || latCoordinates == 0 ||
                lonCoordinates == null || lonCoordinates == 0 ){
            throw new IllegalArgumentException("Invalid input parameters");
        }

        logger.info("Creating a new city: " + cityName);
        City newCity = new City(cityName, countryIATA, cityIATA, timeZone, latCoordinates, lonCoordinates);
        newCity = this.countriesRepository.insert(newCity);
        return newCity;
    }

    @RequestMapping(value="/synchronize-cities", method = RequestMethod.POST)
    public String synchronizeCities() {
        logger.info("Rebuilding cities data");
        return this.flightDataAPI.synchronizeCityDataWithAPI();
    }

    @RequestMapping(value="/delete-cities", method = RequestMethod.DELETE)
    public Boolean deleteCities() {
        logger.info("Deleting cities data");
        return this.flightDataAPI.deleteCities();
    }

    @RequestMapping(value="/update-city/{cityId}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateCity(
            @PathVariable Long cityId,
            @RequestBody Map<String, Object> updatedFields) {

        try {
            Query query = new Query(Criteria.where("_id").is(cityId.toString()));
            Update update = new Update();

            if (updatedFields.containsKey("cityName")) {
                update.set("cityName", updatedFields.get("cityName").toString());
            }

            if (updatedFields.containsKey("countryIATA")) {
                update.set("countryIATA", updatedFields.get("countryIATA").toString());
            }

            if (updatedFields.containsKey("cityIATA")) {
                update.set("cityIATA", updatedFields.get("cityIATA").toString());
            }
            if (updatedFields.containsKey("latCoordinates")) {
                update.set("latCoordinates", updatedFields.get("latCoordinates").toString());
            }
            if (updatedFields.containsKey("lonCoordinates")) {
                update.set("lonCoordinates", updatedFields.get("lonCoordinates").toString());
            }
            if (updatedFields.containsKey("timeZone")) {
                update.set("timeZone", updatedFields.get("timeZone").toString());
            }

            UpdateResult result = mongoTemplate.updateFirst(query, update, City.class);

            if (result.getModifiedCount() > 0) {
                return ResponseEntity.ok("City updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("City not found");
            }
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating city: " + e.getMessage());
        }
    }


}