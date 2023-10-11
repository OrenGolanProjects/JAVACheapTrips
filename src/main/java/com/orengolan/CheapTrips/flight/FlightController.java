package com.orengolan.CheapTrips.flight;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Size;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    public ResponseEntity<?> findFlight(
            @Size(max = 10) @RequestParam String origin_cityName,
            @Size(max = 10) @RequestParam String destination_cityName,
            @RequestParam(required = false) String depart_date,
            @RequestParam(required = false) String return_date,
            @Size(max = 3) @RequestParam String currency
    ) {
        try {

            String regex = "\\d{4}-\\d{2}-\\d{2}";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher;
            if (!(depart_date==null)){
                matcher = pattern.matcher(return_date);
                if(!(matcher.matches())){
                    throw new Exception("Invalid return date format: "+return_date+". Please use the format yyyy-MM-dd.");
                }
            }
            if (!(return_date==null)){
                matcher = pattern.matcher(depart_date);
                if(!(matcher.matches())){
                    throw new Exception("Invalid depart date format: "+depart_date+". Please use the format yyyy-MM-dd.");
                }
            }

            logger.info("FlightController: Start Method: findFlight.");
            logger.info("Request data: "+
                    origin_cityName+
                    destination_cityName+
                    depart_date+
                    return_date+
                    currency
            );
            Flight flight = this.flightService.findFlight(
                    origin_cityName,
                    destination_cityName,
                    depart_date,
                    return_date,
                    currency
            );

            if (flight.getTicketKeys().isEmpty()){
                return ResponseEntity.status(201).body(flight);
            }
            return ResponseEntity.ok(flight);
        } catch (Exception e) {
            logger.severe("Failed to find a flight: "+e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
