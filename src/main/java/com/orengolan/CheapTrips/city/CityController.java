package com.orengolan.CheapTrips.city;


import java.util.List;
import java.util.logging.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/locations")
public class CityController {


    //TODO Needs to developer a validation layer + User Identification.
    private static final Logger logger = Logger.getLogger(CityController.class.getName());
    private final CityRepository cityRepository;
    private final CityService cityService;


    @Autowired
    public CityController(CityRepository cityRepository, CityService cityService){
        this.cityRepository = cityRepository;
        this.cityService = cityService;

    }

    @RequestMapping(value = "/city/{cityName}", method = RequestMethod.GET)
    public ResponseEntity<?> getSpecificCity(@PathVariable String cityName)  {
        return ResponseEntity.ok( this.cityService.fetchSpecificCity(cityName));
    }

    @RequestMapping(value="/get-all-cities", method = RequestMethod.GET)
    public List<City> getAllCities() {
        logger.info("Getting all cities");
        return this.cityRepository.findAll();
    }

    @RequestMapping(value="/create-specific-city", method = RequestMethod.POST)
    public boolean createSpecificCity(@RequestParam String cityName, @RequestParam String countryIATA,
                                   @RequestParam String cityIATA, @RequestParam String timeZone,
                                   @RequestParam Double latCoordinates, @RequestParam Double lonCoordinates) {

        //TODO: update exception global handler
        return this.cityService.createNewCity(cityName,countryIATA,cityIATA,timeZone,latCoordinates,lonCoordinates);
    }

    @RequestMapping(value="/synchronize-cities", method = RequestMethod.POST)
    public String synchronizeCities() throws JsonProcessingException {

        //TODO: update exception global handler
        logger.info("Rebuilding cities data");
        return this.cityService.synchronizeCityDataWithAPI();
    }

    @RequestMapping(value="/delete-cities", method = RequestMethod.DELETE)
    public Boolean deleteCities() {

        //TODO: update exception global handler
        logger.info("Deleting cities data");
        return this.cityService.deleteCities();
    }

    @RequestMapping(value="/update-city/{cityId}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateCity
            (
                @PathVariable String cityId,@RequestParam(required = false) String cityName,@RequestParam(required = false) String countryIATA,@RequestParam(required = false) String cityIATA,
                @RequestParam(required = false) Double latCoordinates,@RequestParam(required = false) Double lonCoordinates,@RequestParam(required = false) String timeZone
            ) {

        UpdateResult result =  this.cityService.updateCityData(cityId,cityName,countryIATA,cityIATA,latCoordinates,lonCoordinates,timeZone);
        return ResponseEntity.ok("City updated successfully, data: "+result);

    }

}