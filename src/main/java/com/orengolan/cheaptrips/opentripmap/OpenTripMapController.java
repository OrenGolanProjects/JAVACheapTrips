package com.orengolan.cheaptrips.opentripmap;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * The {@code OpenTripMapController} class serves as the RESTful API controller for managing and retrieving information about tourist places.
 * It provides endpoints for retrieving all tourist places, fetching details about a specific place, generating tourist places based on
 * provided parameters, and deleting all stored tourist places data. This controller collaborates with the {@code OpenTripMapService}
 * and {@code OpenTripMapRepository} to interact with the OpenTripMap service and handle data operations.
 *
 * Key Features:
 * - Implements RESTful API endpoints for managing and retrieving tourist places information.
 * - Utilizes Swagger annotations like {@code @ApiOperation} and {@code @Api} for API documentation and description.
 * - Collaborates with the {@code OpenTripMapService} to handle business logic related to tourist places.
 * - Provides endpoints for retrieving all places, generating new places, and deleting all stored places data.
 *
 * Example Usage:
 * The class is used to handle HTTP requests related to tourist places, providing functionalities such as retrieving all places,
 * fetching details about a specific place, generating new places based on user input, and deleting all stored places data.
 * It ensures seamless communication between the client and server for managing tourist places information.
 *
 * Note: This class plays a crucial role in the application's backend, exposing APIs that allow users to interact with and retrieve
 * information about tourist places from the OpenTripMap service.
 */
@RestController
@RequestMapping("/api/tourist")
@Api(tags = "Admin Maintenance", description = "Admin Oversight for Essential Services.")
public class OpenTripMapController {
    private static final Logger logger = Logger.getLogger(OpenTripMapController.class.getName());
    private final OpenTripMapService openTripMapService;
    private final OpenTripMapRepository openTripMapRepository;

    public OpenTripMapController(OpenTripMapService openTripMapService,OpenTripMapRepository openTripMapRepository) {
        this.openTripMapService = openTripMapService;
        this.openTripMapRepository = openTripMapRepository;
    }

    @ApiOperation(value = "Get all tourist places", notes = "Retrieve information about all tourist places.")
    @RequestMapping(value="/get-all-places", method = RequestMethod.GET)
    public List<PlacesData> getAllPlaces(){
        return this.openTripMapRepository.findAll();
    }

    @ApiOperation(value = "Get specific tourist place", notes = "Retrieve information about a specific tourist place based on the city name.")
    @RequestMapping(value="/get-specific-place", method = RequestMethod.GET)
    public PlacesData getSpecificPlace(@RequestParam String cityName){
        return this.openTripMapService.getSpecificPlace(cityName);
    }

    @ApiOperation(value = "Generate tourist places", notes = "Generate tourist places based on provided parameters.")
    @RequestMapping(value="/generate-tourist-places", method = RequestMethod.POST)
    public PlacesData generateTouristPlaces(@RequestBody PlacesDataRequest placesDataRequest) throws IOException {
        logger.info("** OpenTripMapController>>  getTouristPlaces: Start method.");

        PlacesData placesData = new PlacesData(
                placesDataRequest.getRadius(),
                placesDataRequest.getLon(),
                placesDataRequest.getLat(),
                placesDataRequest.getLimitPlaces(),
                placesDataRequest.getCityName(),
                placesDataRequest.getCountryIATACode()
                );


        for (String kindName : placesDataRequest.getKinds()){
            KindsCategory.Category category = new KindsCategory.Category(kindName);
            if(!(placesData.getKindsCategory().getCategories().contains(category))){
                placesData.getKindsCategory().getCategories().add(category);
            }
        }
        logger.info("** OpenTripMapController>>  getTouristPlaces: placeData object: "+placesData);
        return this.openTripMapService.generatePlaces(placesData,placesDataRequest.getKinds());
    }

    @ApiOperation(value = "Delete all tourist places data", notes = "Delete all stored tourist places data.")
    @RequestMapping(value="/delete-all-places-data", method = RequestMethod.DELETE)
    public Boolean deleteAllPlacesData(){
        return this.openTripMapService.deletePlacesDataAll();
    }


}
