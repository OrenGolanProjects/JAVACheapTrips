package com.orengolan.cheaptrips.airport;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/airports")
public class AirportController {

    private static final Logger logger = Logger.getLogger(AirportController.class.getName());
    private final AirportRepository airportRepository;
    private final AirportService airportService;


    @Autowired
    public AirportController(AirportRepository airportRepository, AirportService airportService){
        this.airportRepository = airportRepository;
        this.airportService = airportService;
    }

    @RequestMapping(value = "/airport/get-all-airports",method = RequestMethod.GET)
    public List<Airport> getAllAirports(){
        logger.info("** AirportController>>  getAllAirports: Start method");
        return this.airportRepository.findAll();
    }

    @RequestMapping(value = "/airport/synchronize-airports",method = RequestMethod.POST)
    public void synchronizeAirports() throws IOException {
        logger.info("** AirportController>>  synchronizeAirports: Start method");
        this.airportService.synchronizeAirportDataWithAPI();
    }

    @RequestMapping(value = "/airport/delete-all-airports", method = RequestMethod.DELETE)
    public  void deleteAirports(){
        logger.info("** AirportController>>  deleteAirports: Start method");
        this.airportService.deleteAirports();
    }

    @RequestMapping(value = "/airport/{countryIATACode}", method = RequestMethod.GET)
    public Airport getAirportByName(@PathVariable String AirportName) {
        logger.info("** AirportController>>  getAirportByName: Start method");
        return this.airportRepository.findByAirportNameIgnoreCase(AirportName);
    }


}