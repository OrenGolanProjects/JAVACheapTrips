package com.orengolan.CheapTrips.controller;

import com.orengolan.CheapTrips.service.Redis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/general")
public class GeneralController {

    private static final Logger logger = Logger.getLogger(CityController.class.getName());
    private final Redis redis;
    @Autowired
    public GeneralController(Redis redis){
        this.redis = redis;
    }


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


}
