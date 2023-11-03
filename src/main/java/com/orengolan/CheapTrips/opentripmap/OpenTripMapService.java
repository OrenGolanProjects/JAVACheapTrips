package com.orengolan.CheapTrips.opentripmap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orengolan.CheapTrips.config.ConfigLoader;
import com.orengolan.CheapTrips.util.API;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class OpenTripMapService {
    private final Logger logger = Logger.getLogger(OpenTripMapService.class.getName());
    JSONObject config = ConfigLoader.loadConfig();
    private final API api;
    private final ObjectMapper objectMapper;
    private final OpenTripMapRepository openTripMapRepository;
    private PlacesData placesData;

    public OpenTripMapService(OpenTripMapRepository openTripMapRepository, API api, ObjectMapper objectMapper) {
        this.openTripMapRepository = openTripMapRepository;
        this.api = api;
        this.objectMapper = objectMapper;
    }

    public PlacesData generateTouristPlaces(PlacesData placesData) throws JsonProcessingException {
        logger.info("OpenTripMapService  >>generateTouristPlaces: Start method.");

        for (PlacesData place : this.openTripMapRepository.findAll())
        {
            if (place.getDatabaseKey().equals(placesData.getDatabaseKey())){
                return place;
            }
        }

        this.validatePlaces(placesData); // validate kinds.

        JsonNode rootNode =  this.getListTouristPlaces(placesData);
        JsonNode dataNode = rootNode.get("features");

        logger.info("OpenTripMapService  >>generateTouristPlaces: Found "+dataNode.size()+" places.");
        if(dataNode.isEmpty()){
            throw new IllegalArgumentException("Did not found Tourist places.");
        }
        for (JsonNode mainPlaceDataNode : dataNode){
            PlaceDetails placeDetails = this.generatePlaceDetails(mainPlaceDataNode);
            placesData.getPlaces().add(placeDetails);
        }

        this.savePlacesData(placesData);

        logger.info("OpenTripMapService  >>generateTouristPlaces: End method.");
        return placesData;

    }

    private JsonNode getListTouristPlaces(PlacesData placesData) throws JsonProcessingException {
        logger.info("OpenTripMapService  >>getListTouristPlaces: Start method.");

        String API_URL = (String) config.get("opentripmap_API_Geo");
        String TOKEN = (String) config.get("opentripmap_TOKEN");

        API_URL +="radius?radius="+ placesData.getRadius()+"&lon="+ placesData.getLon()+"&lat="+ placesData.getLat()+"&kinds="+String.join(",", placesData.getKinds())+"&limit="+ placesData.getLimitPlaces()+"&apikey="+TOKEN;
        String json = this.api.buildAndExecuteRequest(API_URL,null);
        logger.info("OpenTripMapService  >>getListTouristPlaces: response to get list of places: "+json);

        logger.info("OpenTripMapService  >>getListTouristPlaces: End method.");
        return this.objectMapper.readTree(json);
    }

    private JsonNode getPlaceDetails(String xid) throws JsonProcessingException {
        logger.info("OpenTripMapService  >>getPlaceDetails: Start method.");

        String API_URL = (String) config.get("opentripmap_API_TourismPlaces");
        String TOKEN = (String) config.get("opentripmap_TOKEN");
        API_URL +=xid+"?apikey="+TOKEN;
        String json = this.api.buildAndExecuteRequest(API_URL,null);
        logger.info("OpenTripMapService  >>getListTouristPlaces: response to get place details: "+json);

        logger.info("OpenTripMapService  >>getPlaceDetails: End method.");
        return this.objectMapper.readTree(json);
    }

    private PlaceDetails generatePlaceDetails(JsonNode mainPlaceDataNode) throws JsonProcessingException {


        String xid = mainPlaceDataNode.path("properties").get("xid").textValue();
        String name = mainPlaceDataNode.path("properties").get("name").textValue();
        String rate = mainPlaceDataNode.path("properties").get("rate").textValue();
        String kinds = mainPlaceDataNode.path("properties").get("kinds").textValue();
        String osm = ifFieldExists(mainPlaceDataNode,"properties","osm");

        String wikidata = ifFieldExists(mainPlaceDataNode,"properties","wikidata");

        JsonNode placeData = getPlaceDetails(xid);

        String sources  =    ifFieldExists(placeData,null,"sources");
        String otm      =    ifFieldExists(placeData,null,"otm");
        String url      =    ifFieldExists(placeData,null,"url");
        String image    =    ifFieldExists(placeData,null,"image");
        String preview  =    ifFieldExists(placeData,null,"preview");
        String wikipedia_extract = ifFieldExists(placeData,null,"wikipedia_extract");

        Double lon = placeData.path("point").get("lon").asDouble();
        Double lat = placeData.path("point").get("lat").asDouble();
        PlaceDetails.Point point = new PlaceDetails.Point(lon,lat);

        PlaceDetails.Address address = new PlaceDetails.Address(
                placeData.path("address").get("city").textValue(),
                ifFieldExists(placeData,"address","state"),
                placeData.path("address").get("country").textValue(),
                placeData.path("address").get("postcode").textValue(),
                placeData.path("address").get("country_code").textValue(),
                ifFieldExists(placeData,"address","road"),
                ifFieldExists(placeData,"address","suburb"),
                ifFieldExists(placeData,"address","house_number"),
                ifFieldExists(placeData,"address","neighbourhood")
        );

        return new PlaceDetails( xid, name, rate, kinds, sources, otm, osm, url, wikidata, image, preview, wikipedia_extract, point, address );
    }

    private String ifFieldExists(JsonNode jsonNode,String pathValue,String getValue){
        if(pathValue==null && jsonNode.has(getValue)){
                return jsonNode.get(getValue).textValue();
        }
        if (jsonNode.path(pathValue).has(getValue)){
            return jsonNode.path(pathValue).get(getValue).textValue();
        }

        return "";
    }

    private void savePlacesData(PlacesData placesData){
        logger.info("OpenTripMapService  >>savePlacesData: Start method.");
        this.openTripMapRepository.save(placesData);
        logger.info("OpenTripMapService  >>savePlacesData: End method.");
    }

    public PlacesData searchSpecificPlace(String ID){
        logger.info("OpenTripMapService  >>searchSpecificPlace: Start method.");

        for (PlacesData place : this.openTripMapRepository.findAll())
        {
            if (place.getId().equals(ID)){
                return place;
            }
        }

        throw new IllegalArgumentException("Did not Found trip places.");
    }

    public Boolean deletePlacesDataAll(){
        logger.info("OpenTripMapService  >>deletePlacesDataAll: Start method.");
        this.openTripMapRepository.deleteAll();
        logger.info("OpenTripMapService  >>deletePlacesDataAll: End method.");
        return Boolean.TRUE;
    }

    private void validatePlaces(PlacesData placesData){

        List<String> allKinds = Arrays.asList("interesting places", "amusements", "sport", "tourist facilities", "accomodations","adult");
        List<String> currentKinds = placesData.getKinds().stream().map(String::toLowerCase).collect(Collectors.toList());
        logger.info("OpenTripMapService  >>validateObject: Start method.");

        for (String item : currentKinds) {
            if(!allKinds.contains(item)){
                logger.severe("OpenTripMapService  >>validateObject: Invalid kind: "+ item);
                throw new IllegalStateException("Invalid kind input:"+item+", Must be one of: interesting places, amusements ,sport ,tourist facilities ,accomodations ,adult");
            }
        }
        logger.info("OpenTripMapService  >>validateObject: kinds are valid.");

    }
}