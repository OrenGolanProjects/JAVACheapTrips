package com.orengolan.cheaptrips.airport;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
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
    public List<Airport> getAirportByCountry(@PathVariable String countryIATACode) {
        try{
            logger.info("Getting information for country IATA code: " + countryIATACode);
            List<Airport> airport = this.airportRepository.findByCountryIATACode(countryIATACode.toUpperCase());
            if (airport == null){
                throw new IllegalArgumentException("Airports did not found at IATA Code: "+ countryIATACode+".");
            }
            return airport;
        }
        catch(Exception e) {
            throw new IllegalArgumentException("Error retrieving airports at IATA Code: "+ countryIATACode+ " information.");
        }
    }

    @RequestMapping(value = "/airport/{countryIATACode}/{cityIATACode}", method = RequestMethod.GET)
    public Airport getAirportByCountryAndCity(@PathVariable String countryIATACode,@PathVariable String cityIATACode) {
        try {
            logger.info("Getting information for Country IATA code: " + countryIATACode + ", City IATA code: " + cityIATACode);

            List<Airport> airports = this.airportRepository.findByCountryIATACodeAndCityIATACode(
                    countryIATACode.toUpperCase(),
                    cityIATACode.toUpperCase()
            );

            if (airports.isEmpty()) {
                logger.warning("No airports found for Country IATA Code: " + countryIATACode+ " and City IATA Code: " + cityIATACode + ".");
                throw new IllegalArgumentException("No airports found for Country IATA Code: " + countryIATACode+ " and City IATA Code: " + cityIATACode + ".");
            }
            if (airports.size()>1){
                logger.warning("Multiple airports found for Country IATA code: " + countryIATACode + ", City IATA code: " + cityIATACode);
                throw new IllegalArgumentException("Multiple airports found for Country IATA code: " + countryIATACode + ", City IATA code: " + cityIATACode);
            }

            return airports.get(0);

        } catch (Exception e) {
            logger.warning("Error retrieving airport information for Country IATA Code: " + countryIATACode+ " and City IATA Code: " + cityIATACode + ".");
            throw new IllegalArgumentException("Error retrieving airport information for Country IATA Code: " + countryIATACode+ " and City IATA Code: " + cityIATACode + ".");
        }
    }
}