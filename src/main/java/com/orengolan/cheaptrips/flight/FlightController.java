package com.orengolan.cheaptrips.flight;


import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.text.ParseException;
import java.util.Set;
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
    public Flight  searchFlight(
            @Size(max = 20) @RequestParam(defaultValue ="TLV") String origin_cityIATACODE,
            @Size(max = 20) @RequestParam(defaultValue ="AMS") String destination_cityIATACODE,
            @RequestParam(defaultValue ="2023-XX-XX",required = false) @JsonFormat(pattern = "yyyy-MM-dd") String depart_date,
            @RequestParam(defaultValue ="2023-XX-XX",required = false) @JsonFormat(pattern = "yyyy-MM-dd") String return_date,
            @Size(min=3,max = 3) @RequestParam(defaultValue = "USD") String currency
    ) throws  ParseException, IOException {

        logger.info("** FlightController>>  searchFlight: Start method.");
        return this.flightService.findFlight(
                origin_cityIATACODE,
                destination_cityIATACODE,null,null
        );
    }

    @RequestMapping(value = "/get-specific-ticket",method = RequestMethod.GET)
    public Set<String> getSpecificTicket(
            @Size(max =3) @RequestParam String Origin_CountryIataCode,
            @Size(max =3) @RequestParam String Destination_CountryIataCode){
        logger.info("** FlightController>>  getSpecificTicket: Start method.");
        return this.flightService.getTicketByParseKey(Origin_CountryIataCode+"_"+Destination_CountryIataCode);
    }

}