package com.orengolan.CheapTrips.opentripmap;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/tourist")
public class OpenTripMapController {
    private static final Logger logger = Logger.getLogger(OpenTripMapController.class.getName());
    private final OpenTripMapService openTripMapService;
    private final OpenTripMapRepository openTripMapRepository;

    public OpenTripMapController(OpenTripMapService openTripMapService,OpenTripMapRepository openTripMapRepository) {
        this.openTripMapService = openTripMapService; // Service
        this.openTripMapRepository = openTripMapRepository;
    }

    @RequestMapping(value="/get-all-places", method = RequestMethod.GET)
    public List<PlacesData> getAllPlaces(){
        return this.openTripMapRepository.findAll();
    }

    @RequestMapping(value="/get-specific-place", method = RequestMethod.GET)
    public PlacesData getSpecificPlace(@RequestParam String ID){
        return this.openTripMapService.searchSpecificPlace(ID);
    }


    @RequestMapping(value="/generate-tourist-places", method = RequestMethod.POST)
    public PlacesData generateTouristPlaces(@RequestBody PlacesData_IN placesDataIN) throws JsonProcessingException {
        logger.info("** OpenTripMapController>>  getTouristPlaces: Start method.");
        PlacesData placesData = new PlacesData(
                placesDataIN.getRadius(),
                placesDataIN.getLon(),
                placesDataIN.getLat(),
                placesDataIN.getLimitPlaces(),
                placesDataIN.getCityName(),
                placesDataIN.getCountryIATACode(),
                placesDataIN.getKinds()
                );
        logger.info("** OpenTripMapController>>  getTouristPlaces: placeData object: "+placesData);
        return this.openTripMapService.generateTouristPlaces(placesData);
    }

    @RequestMapping(value="/delete-all-places-data", method = RequestMethod.DELETE)
    public Boolean deleteAllPlacesData(){
        return this.openTripMapService.deletePlacesDataAll();
    }



}
