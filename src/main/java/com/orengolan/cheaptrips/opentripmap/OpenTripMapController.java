package com.orengolan.cheaptrips.opentripmap;


import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/tourist")
public class OpenTripMapController {
    private static final Logger logger = Logger.getLogger(OpenTripMapController.class.getName());
    private final OpenTripMapService openTripMapService;
    private final OpenTripMapRepository openTripMapRepository;

    public OpenTripMapController(OpenTripMapService openTripMapService,OpenTripMapRepository openTripMapRepository) {
        this.openTripMapService = openTripMapService;
        this.openTripMapRepository = openTripMapRepository;
    }

    @RequestMapping(value="/get-all-places", method = RequestMethod.GET)
    public List<PlacesData> getAllPlaces(){
        return this.openTripMapRepository.findAll();
    }

    @RequestMapping(value="/get-specific-place", method = RequestMethod.GET)
    public PlacesData getSpecificPlace(@RequestParam String cityName){
        return this.openTripMapService.getSpecificPlace(cityName);
    }

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

    @RequestMapping(value="/delete-all-places-data", method = RequestMethod.DELETE)
    public Boolean deleteAllPlacesData(){
        return this.openTripMapService.deletePlacesDataAll();
    }


}
