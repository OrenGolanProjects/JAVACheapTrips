package com.orengolan.cheaptrips.flight;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Logger;

/**
 * The {@code FlightController} class serves as the RESTful controller for handling flight-related
 * operations. It provides endpoints for searching available flights based on specified criteria
 * and retrieving specific ticket information. Admin oversight for essential services is emphasized.
 *
 * Key Features:
 * - {@code searchFlight}: Endpoint to search for available flights based on origin, destination,
 *   departure date, return date, and currency.
 * - {@code getSpecificTicket}: Endpoint to retrieve specific ticket information based on country IATA codes.
 *
 * Example:
 * The controller can be accessed through specified RESTful endpoints to perform flight-related tasks.
 * For example, searching for available flights by providing origin, destination, and date criteria.
 * Additionally, retrieving specific ticket information based on country IATA codes is supported.
 *
 * Note: This class is part of the application's API, providing essential services for flight management.
 */
@RestController
@RequestMapping("/api/flight")
@Api(tags = "Admin Maintenance" ,description = "Admin Oversight for Essential Services.")
public class FlightController {
    private static final Logger logger = Logger.getLogger(FlightController.class.getName());
    private final FlightService flightService;
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }


    /**
     * Endpoint for searching available flights based on specified criteria.
     *
     * @param origin_cityIATACODE The IATA code of the origin city (default: TLV).
     * @param destination_cityIATACODE The IATA code of the destination city (default: AMS).
     * @param depart_date The departure date in the format 'yyyy-MM-dd' (default: 2023-XX-XX).
     * @param return_date The return date in the format 'yyyy-MM-dd' (default: 2023-XX-XX).
     * @param currency The currency for pricing (default: USD).
     * @return A {@code Flight} object representing the search result.
     * @throws Exception If an error occurs during the flight search process.
     */
    @ApiOperation(value = "Search for flights", notes = "Search for available flights based on specified criteria.")
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

    /**
     * Endpoint for retrieving specific ticket information based on country IATA codes.
     *
     * @param Origin_CountryIataCode The IATA code of the origin country.
     * @param Destination_CountryIataCode The IATA code of the destination country.
     * @return A set of specific ticket information.
     */
    @ApiOperation(value = "Get specific ticket", notes = "Retrieve specific ticket information based on country IATA codes.")
    @RequestMapping(value = "/get-specific-ticket",method = RequestMethod.GET)
    public List<FlightTicket> getSpecificTicket(
            @Size(max =3) @RequestParam String Origin_CountryIataCode,
            @Size(max =3) @RequestParam String Destination_CountryIataCode) throws JsonProcessingException {
        logger.info("** FlightController>>  getSpecificTicket: Start method.");
        return this.flightService.getTicketByParseKey(Origin_CountryIataCode+"_"+Destination_CountryIataCode);
    }

}