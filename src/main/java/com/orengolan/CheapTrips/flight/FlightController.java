package com.orengolan.CheapTrips.flight;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Size;
import java.text.ParseException;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/flight")
public class FlightController {
    private static final Logger logger = Logger.getLogger(FlightController.class.getName());
    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @RequestMapping(value = "/search-flight",method = RequestMethod.GET)
    public ResponseEntity<?> searchFlight(
            @Size(max = 10) @RequestParam(defaultValue ="TLV") String origin_cityName,
            @Size(max = 10) @RequestParam(defaultValue ="AMS") String destination_cityName,
            @RequestParam(defaultValue ="2023-XX-XX",required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String depart_date,
            @RequestParam(defaultValue ="2023-XX-XX",required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String return_date,
            @Size(max = 3) @RequestParam(defaultValue = "USD") String currency
    ) throws ChangeSetPersister.NotFoundException, ParseException, JsonProcessingException {
        logger.info("** FlightController>>  searchFlight: Start method.");
        Flight flight = this.flightService.findFlight(origin_cityName,destination_cityName, depart_date, return_date,currency);
        return ResponseEntity.ok(flight);
    }

    @RequestMapping(value = "/get-specific-ticket",method = RequestMethod.GET)
    public ResponseEntity<?> getSpecificTicket(
            @Size(max =3) @RequestParam String Origin_CountryIataCode,
            @Size(max =3) @RequestParam String Destination_CountryIataCode){
        logger.info("** FlightController>>  getSpecificTicket: Start method.");
        return ResponseEntity.ok(this.flightService.getTicketByParseKey(Origin_CountryIataCode+"_"+Destination_CountryIataCode));
    }
}