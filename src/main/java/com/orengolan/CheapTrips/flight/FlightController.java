package com.orengolan.CheapTrips.flight;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Size;
import java.text.ParseException;
import java.util.logging.Logger;



@RestController
@RequestMapping("/api/flight")
public class FlightController {
    //TODO Needs to developer a validation layer + User Identification.
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
            @Size(max = 3) @RequestParam(defaultValue = "USD") String currency) throws ParseException, JsonProcessingException, ChangeSetPersister.NotFoundException {

        logger.info("FlightController: Start Method: findFlight.");
        Flight flight = this.flightService.findFlight(origin_cityName,destination_cityName, depart_date, return_date,currency);
        logger.info("FlightController: Flight object data: "+flight.toString());
        return ResponseEntity.ok(flight);

    }

    @RequestMapping(value = "/get-specific-ticket",method = RequestMethod.GET)
    public ResponseEntity<?> getSpecificTicket(
            @Size(max =3) @RequestParam String Origin_CountryIataCode,
            @Size(max =3) @RequestParam String Destination_CountryIataCode){
        return ResponseEntity.ok(this.flightService.getTicketByParseKey(Origin_CountryIataCode+"_"+Destination_CountryIataCode));
    }

}
