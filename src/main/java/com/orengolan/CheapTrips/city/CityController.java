package com.orengolan.CheapTrips.city;


import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import com.mongodb.client.result.UpdateResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/locations")
public class CityController {


    //TODO Needs to developer a validation layer + User Identification.
    private static final Logger logger = Logger.getLogger(CityController.class.getName());
    private final CityRepository cityRepository;
    private final CityService cityService;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public CityController(CityRepository cityRepository, CityService cityService, MongoTemplate mongoTemplate ){
        this.cityRepository = cityRepository;
        this.cityService = cityService;
        this.mongoTemplate = mongoTemplate;
    }

    @RequestMapping(value = "/city/{cityName}", method = RequestMethod.GET)
    public ResponseEntity<?> getSpecificCity(@PathVariable String cityName) {
        try {
            logger.info("Getting information for city: " + cityName);
            City city = null;
            try {
                // Start by searching for search the EXACT string.
                city = this.cityRepository.findCityByPartialName(cityName.toLowerCase());
            }catch(Exception e){
                logger.warning(e.getMessage());
            }
            if (city == null) {

                List<City> cities = this.cityRepository.findBycityName(cityName.toLowerCase());

                if (!cities.isEmpty()){
                logger.warning("Found "+cities.size()+" matching cities found for city name: " + cityName);
                logger.warning("Returning the first one.");

                return ResponseEntity.ok(cities.get(0));
                }

                // Try search again but with partial name.
                logger.warning("City " + cityName + " is not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("City " + cityName + " is not found.");
                }

            // Success found a city
            return ResponseEntity.ok(city);

        }catch(Exception e) {
            logger.warning("Error retrieving city information, "+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving city information, "+e.getMessage());
        }
    }

    @RequestMapping(value="/get-all-cities", method = RequestMethod.GET)
    public List<City> getAllCities() {
        logger.info("Getting all cities");
        return this.cityRepository.findAll();
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
        if (this.cityRepository.findBycityName(cityName).isEmpty()){
            newCity = this.cityRepository.insert(newCity);
        }
        return newCity;
    }

    @RequestMapping(value="/synchronize-cities", method = RequestMethod.POST)
    public String synchronizeCities() throws IOException {
        logger.info("Rebuilding cities data");
        return this.cityService.synchronizeCityDataWithAPI();
    }

    @RequestMapping(value="/delete-cities", method = RequestMethod.DELETE)
    public Boolean deleteCities() {
        logger.info("Deleting cities data");
        return this.cityService.deleteCities();
    }

    @RequestMapping(value="/update-city/{cityId}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateCity(
            @PathVariable String cityId,
            @RequestParam(required = false) String cityName,
            @RequestParam(required = false) String countryIATA,
            @RequestParam(required = false) String cityIATA,
            @RequestParam(required = false) Double latCoordinates,
            @RequestParam(required = false) Double lonCoordinates,
            @RequestParam(required = false) String timeZone
    ) {
        try {
            Query query = new Query(Criteria.where("_id").is(cityId));
            Update update = new Update();

            if (!(cityName == null) && !cityName.isEmpty() && !cityName.trim().isEmpty()){
                update.set("cityName", cityName);
            }

            if (countryIATA != null && !countryIATA.isEmpty() && !countryIATA.trim().isEmpty()){
                update.set("countryIATA", countryIATA);
            }


            if (!(cityIATA == null) && !cityIATA.isEmpty() && !cityIATA.trim().isEmpty()){
                update.set("cityIATA", cityIATA);
            }
            if (latCoordinates != null && latCoordinates > 0) {
                update.set("latCoordinates", latCoordinates);
            }
            if (lonCoordinates != null && lonCoordinates > 0) {
                update.set("lonCoordinates", lonCoordinates);
            }
            if (!(timeZone == null) && !timeZone.isEmpty() && !timeZone.trim().isEmpty()){
                update.set("timeZone", timeZone);
            }

            UpdateResult result = mongoTemplate.updateFirst(query, update, City.class);

            if (result.getModifiedCount() > 0) {
                return ResponseEntity.ok("City updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("City not found");
            }
        } catch(NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid cityId format");
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating city: " + e.getMessage());
        }
    }
}