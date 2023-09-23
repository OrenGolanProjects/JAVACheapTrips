package com.orengolan.CheapTrips.controller;

import com.orengolan.CheapTrips.repository.ContriesRepository;
import com.orengolan.CheapTrips.model.City;
import com.orengolan.CheapTrips.service.FlightDataAPI;
import com.orengolan.CheapTrips.service.Redis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripsController {

    @Autowired
    Redis redis;

    @Autowired
    ContriesRepository contriesRepository;
    @Autowired
    FlightDataAPI flightDataAPI;



    @RequestMapping(value = "/get_key", method = RequestMethod.GET)
    public String getKey(@RequestParam String key){
        return redis.get(key).toString();
    }

    @RequestMapping(value = "/set_key", method = RequestMethod.GET)
    public Boolean setKey(@RequestParam String key, @RequestParam String value){
        return redis.set(key,value);
    }


    @RequestMapping(value="/create_city", method = RequestMethod.POST)
    public City createCity(@RequestParam String cityName,@RequestParam String countryIATA,
                           @RequestParam String cityIATA,@RequestParam String timeZone,
                           @RequestParam Double latCoordinates, @RequestParam Double lonCoordinates){
        City newCity = new City(cityName,countryIATA,cityIATA,timeZone,latCoordinates,lonCoordinates);
        newCity = contriesRepository.insert(newCity);
        return newCity;
    }

    @RequestMapping(value = "/city/{name}", method = RequestMethod.GET)
    public ResponseEntity<String> getCity(@RequestParam String CityName) throws IOException {
        City city = contriesRepository.findByCityName(CityName);
        if (city == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("City name is not found.");
        }
        return ResponseEntity.ok(city.toString());
    }

    @RequestMapping(value="/get_all_cities", method = RequestMethod.GET)
    public List<City> getAllCities() {
        return contriesRepository.findAll();
    }

    @RequestMapping(value="/rebuild_cities", method = RequestMethod.POST)
    public String fetchAndInsertCountries(){
        return flightDataAPI.processCityList();
    }
}