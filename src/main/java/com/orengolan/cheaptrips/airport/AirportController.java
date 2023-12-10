package com.orengolan.cheaptrips.airport;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * The {@code AirportController} class serves as the RESTful API controller for managing airport-related operations
 * in the CheapTrips backend application. Annotated with {@code @RestController}, this class handles HTTP requests
 * related to airport data and interacts with the {@code AirportService} and {@code AirportRepository}.
 *
 * The controller defines several endpoints under the "/api/airports" base path, facilitating the following operations:
 * - {@code getAllAirports}: Retrieves a list of all airports stored in the database.
 * - {@code synchronizeAirports}: Initiates the synchronization of airport data with an external API.
 * - {@code deleteAirports}: Deletes all airports from the database.
 * - {@code getAirportByName}: Retrieves an airport based on its name, utilizing the country IATA code as a path variable.
 *
 * The class is annotated with {@code @Api} to provide Swagger documentation, specifying the "Admin Maintenance" tag
 * and describing it as "Admin Oversight for Essential Services."
 *
 * Usage Example:
 * <pre>
 * {@code
 * GET /api/airports/airport/get-all-airports
 * POST /api/airports/airport/synchronize-airports
 * DELETE /api/airports/airport/delete-all-airports
 * GET /api/airports/airport/{countryIATACode}
 * }
 * </pre>
 *
 * This {@code AirportController} class acts as the entry point for airport-related operations, offering a RESTful API
 * interface for administration and oversight in the CheapTrips application.
 */
@RestController
@RequestMapping("/api/airports")
@Api(tags = "Admin Maintenance", description = "Admin Oversight for Essential Services.")
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

    @ApiOperation(value = "Synchronize airports", notes = "Synchronize airport data with an external API.")
    @RequestMapping(value = "/airport/synchronize-airports",method = RequestMethod.POST)
    public void synchronizeAirports() throws IOException {
        logger.info("** AirportController>>  synchronizeAirports: Start method");
        this.airportService.synchronizeAirportDataWithAPI();
    }

    @ApiOperation(value = "Delete all airports", notes = "Delete all airports from the database.")
    @RequestMapping(value = "/airport/delete-all-airports", method = RequestMethod.DELETE)
    public  void deleteAirports(){
        logger.info("** AirportController>>  deleteAirports: Start method");
        this.airportService.deleteAirports();
    }

    @ApiOperation(value = "Get airport by country IATA code", notes = "Retrieve an airport based on the country IATA code.")
    @RequestMapping(value = "/airport/{countryIATACode}", method = RequestMethod.GET)
    public Airport getAirportByName(@PathVariable String AirportName) {
        logger.info("** AirportController>>  getAirportByName: Start method");
        return this.airportRepository.findByAirportNameIgnoreCase(AirportName);
    }


}