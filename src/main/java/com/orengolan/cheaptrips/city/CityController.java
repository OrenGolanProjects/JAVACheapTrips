package com.orengolan.cheaptrips.city;


import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/cities")
public class CityController {

    private static final Logger logger = Logger.getLogger(CityController.class.getName());
    private final CityRepository cityRepository;
    private final CityService cityService;


    @Autowired
    public CityController(CityRepository cityRepository, CityService cityService){
        this.cityRepository = cityRepository;
        this.cityService = cityService;
    }

    @RequestMapping(value = "/city/{cityName}", method = RequestMethod.GET)
    public List<City> getSpecificCity(@PathVariable String cityName)  {
        logger.info("** CityController>>  getSpecificCity: Start method");
        return this.cityService.fetchSpecificCityByName(cityName);
    }

    @RequestMapping(value="/get-all-cities", method = RequestMethod.GET)
    public List<City> getAllCities() {
        logger.info("** CityController>>  getAllCities: Start method");
        return this.cityRepository.findAll();
    }

    @RequestMapping(value="/create-specific-city", method = RequestMethod.POST)
    public boolean createSpecificCity(@Valid @ModelAttribute City city)
    {
        logger.info("** CityController>>  createSpecificCity: Start method");
        return this.cityService.createNewCity(city);
    }

    @RequestMapping(value="/synchronize-cities", method = RequestMethod.POST)
    public Boolean synchronizeCities() throws IOException {

        logger.info("** CityController>>  synchronizeCities: Start method");
        return this.cityService.synchronizeCityDataWithAPI();
    }

    @RequestMapping(value="/delete-cities", method = RequestMethod.DELETE)
    public Boolean deleteCities() {

        logger.info("** CityController>>  deleteCities: Start method");
        return this.cityService.deleteCities();
    }

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