package com.orengolan.CheapTrips.controller;

import com.orengolan.CheapTrips.ContriesRepository;
import com.orengolan.CheapTrips.model.City;
import com.orengolan.CheapTrips.service.Redis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trips")
public class TripsController {

    @Autowired
    Redis redis;

    @Autowired
    ContriesRepository contriesRepository;


    @RequestMapping(value = "/getKey", method = RequestMethod.GET)
    public String getKey(@RequestParam String key){
        return redis.get(key).toString();
    }

    @RequestMapping(value = "/setKey", method = RequestMethod.GET)
    public Boolean setKey(@RequestParam String key, @RequestParam String value){
        return redis.set(key,value);
    }


    @RequestMapping(value="/createCity", method = RequestMethod.POST)
    public City createCity(@RequestParam String cityName,@RequestParam String countryIATA,
                           @RequestParam String cityIATA,@RequestParam String timeZone,
                           @RequestParam Double latCoordinates, @RequestParam Double lonCoordinates){
        City newCity = new City(cityName,countryIATA,cityIATA,timeZone,latCoordinates,lonCoordinates);
        newCity = contriesRepository.insert(newCity);
        return newCity;
    }

    @RequestMapping(value = "/city/{name}", method = RequestMethod.GET)
    public City getcity(@RequestParam String CityName) {
        City city = contriesRepository.findByCityName(CityName);
        return city;
    }
}