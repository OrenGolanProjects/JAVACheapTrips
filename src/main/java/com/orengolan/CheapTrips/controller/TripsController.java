package com.orengolan.CheapTrips.controller;

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

    @RequestMapping(value = "/getKey", method = RequestMethod.GET)
    public String getKey(@RequestParam String key){
        return redis.get(key).toString();
    }

    @RequestMapping(value = "/setKey", method = RequestMethod.GET)
    public Boolean setKey(@RequestParam String key, @RequestParam String value){
        return redis.set(key,value);
    }


}