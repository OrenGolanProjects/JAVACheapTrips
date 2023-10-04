package com.orengolan.CheapTrips.controller;


import com.orengolan.CheapTrips.model.Airport;
import com.orengolan.CheapTrips.repository.AirportRepository;
import com.orengolan.CheapTrips.service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/airports")
public class AirportController {


    private static final Logger logger = Logger.getLogger(CityController.class.getName());
    private final AirportRepository airportRepository;
    private final AirportService airportService;

    @Autowired
    public AirportController(AirportRepository airportRepository, AirportService airportService, MongoTemplate mongoTemplate ){
        this.airportRepository = airportRepository;
        this.airportService = airportService;
    }

    @RequestMapping(value = "/airport/get-all-airports",method = RequestMethod.GET)
    public List<Airport> getAllAirports(){
        logger.info("*** Getting all airports");
        return this.airportRepository.findAll();
    }

    @RequestMapping(value = "/airport/synchronize-airports",method = RequestMethod.POST)
    public String synchronizeAirports(){
        logger.info("*** Rebuilding airports data");
        return this.airportService.synchronizeAirportDataWithAPI();
    }

    @RequestMapping(value = "/airport/delete-all-airports", method = RequestMethod.DELETE)
    public  Boolean deleteAirports(){
        logger.info("*** Deleting airports data");
        return this.airportService.deleteAirports();
    }

    @RequestMapping(value = "/airport/{countryIATACode}", method = RequestMethod.GET)
    public ResponseEntity<?> getAirportByCountry(@PathVariable String countryIATACode) {
        try{
            logger.info("Getting information for country IATA code: " + countryIATACode);
            List<Airport> airport = this.airportRepository.findByCountryIATACode(countryIATACode.toUpperCase());
            if (airport == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Airports did not found at IATA Code: "+ countryIATACode+".");
            }
            if (airport.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Airports did not found at IATA Code: "+ countryIATACode+".");
            }
            return ResponseEntity.ok(airport);
        }

        catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving airports at IATA Code: "+ countryIATACode+ " information.");
        }
    }

}
