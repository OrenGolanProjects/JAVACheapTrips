package com.orengolan.cheaptrips.city;


import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import com.mongodb.client.result.UpdateResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;


/**
 * The {@code CityController} class serves as the RESTful API controller for managing city-related operations in the
 * CheapTrips backend application. Annotated with {@code @RestController}, this class handles HTTP requests related
 * to city data and interacts with the {@code CityService} and {@code CityRepository}.
 *
 * The controller defines several endpoints under the "/api/cities" base path, facilitating the following operations:
 * - {@code getSpecificCity}: Retrieves information about a specific city by its name.
 * - {@code getAllCities}: Retrieves a list of all cities stored in the database.
 * - {@code createSpecificCity}: Creates a new city with specified details.
 * - {@code synchronizeCities}: Initiates the synchronization of city data with an external API.
 * - {@code deleteCities}: Deletes all cities from the database.
 * - {@code updateCity}: Updates details of a city based on the provided parameters.
 *
 * The class is annotated with {@code @Api} to provide Swagger documentation, specifying the "Admin Maintenance" tag
 * and describing it as "Admin Oversight for Essential Services."
 *
 * Usage Example:
 * <pre>
 * {@code
 * GET /api/cities/city/{cityName}
 * GET /api/cities/get-all-cities
 * POST /api/cities/create-specific-city
 * POST /api/cities/synchronize-cities
 * DELETE /api/cities/delete-cities
 * PUT /api/cities/update-city/{cityId}?cityName=NewCityName&countryIATA=NEW&cityIATA=NC&latCoordinates=12.34&lonCoordinates=56.78&timeZone=UTC+3
 * }
 * </pre>
 *
 * This {@code CityController} class acts as the entry point for city-related operations, offering a RESTful API
 * interface for administration and oversight in the CheapTrips application.
 */
@RestController
@RequestMapping("/api/cities")
@Api(tags = "Admin Maintenance" ,description = "Admin Oversight for Essential Services.")
public class CityController {

    private static final Logger logger = Logger.getLogger(CityController.class.getName());
    private final CityRepository cityRepository;
    private final CityService cityService;


    @Autowired
    public CityController(CityRepository cityRepository, CityService cityService){
        this.cityRepository = cityRepository;
        this.cityService = cityService;
    }

    @ApiOperation(value = "Get specific city", notes = "Retrieve information about a specific city by name.")
    @RequestMapping(value = "/city/{cityName}", method = RequestMethod.GET)
    public List<City> getSpecificCity(@PathVariable String cityName)  {
        logger.info("** CityController>>  getSpecificCity: Start method");
        return this.cityService.fetchSpecificCityByName(cityName);
    }


    @ApiOperation(value = "Get all cities", notes = "Retrieve a list of all cities.")
    @RequestMapping(value="/get-all-cities", method = RequestMethod.GET)
    public List<City> getAllCities() {
        logger.info("** CityController>>  getAllCities: Start method");
        return this.cityRepository.findAll();
    }

    @ApiOperation(value = "Create specific city", notes = "Create a new city with specified details.")
    @RequestMapping(value="/create-specific-city", method = RequestMethod.POST)
    public boolean createSpecificCity(@Valid @ModelAttribute City city)
    {
        logger.info("** CityController>>  createSpecificCity: Start method");
        return this.cityService.createNewCity(city);
    }

    @ApiOperation(value = "Synchronize cities", notes = "Synchronize city data with an external API.")
    @RequestMapping(value="/synchronize-cities", method = RequestMethod.POST)
    public Boolean synchronizeCities() throws IOException {

        logger.info("** CityController>>  synchronizeCities: Start method");
        return this.cityService.synchronizeCityDataWithAPI();
    }

    @ApiOperation(value = "Delete cities", notes = "Delete all cities.")
    @RequestMapping(value="/delete-cities", method = RequestMethod.DELETE)
    public Boolean deleteCities() {

        logger.info("** CityController>>  deleteCities: Start method");
        return this.cityService.deleteCities();
    }

    @ApiOperation(value = "Update city", notes = "Update details of a city.")
    @RequestMapping(value="/update-city/{cityId}", method = RequestMethod.PUT)
    public String updateCity
            (
                @PathVariable String cityId,
                @RequestParam(required = false) String cityName,
                @RequestParam(required = false) String countryIATA,
                @RequestParam(required = false) String cityIATA,
                @RequestParam(required = false) Double latCoordinates,
                @RequestParam(required = false) Double lonCoordinates,
                @RequestParam(required = false) String timeZone
            ) throws ChangeSetPersister.NotFoundException {

        logger.info("** CityController>>  updateCity: Start method");
        UpdateResult result =  this.cityService.updateCityData(cityId,cityName,countryIATA,cityIATA,latCoordinates,lonCoordinates,timeZone);
        return "City updated successfully, data: "+result;
    }

}