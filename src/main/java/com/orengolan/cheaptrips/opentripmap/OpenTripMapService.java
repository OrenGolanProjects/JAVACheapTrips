package com.orengolan.cheaptrips.opentripmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orengolan.cheaptrips.util.API;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Service
public class OpenTripMapService {
    private final Logger logger = Logger.getLogger(OpenTripMapService.class.getName());
    private final API api;
    private final ObjectMapper objectMapper;
    private final OpenTripMapRepository openTripMapRepository;
    private final String opentripmapeEndpoint;
    private final String opentripmapENDPOINTGeo;
    private final String opentripmapeToken;

    public OpenTripMapService(Dotenv dotenv, OpenTripMapRepository openTripMapRepository, API api, ObjectMapper objectMapper) {
        this.openTripMapRepository = openTripMapRepository;
        this.api = api;
        this.objectMapper = objectMapper;
        this.opentripmapENDPOINTGeo = dotenv.get("opentripmap_ENDPOINT_Geo");
        this.opentripmapeEndpoint = dotenv.get("opentripmap_ENDPOINT_Places");
        this.opentripmapeToken = dotenv.get("opentripmap_TOKEN");
    }


    private JsonNode getPlaceDetails(String xid) throws IOException {
        logger.info("OpenTripMapService  >>getPlaceDetails: Start method.");

        String URL= opentripmapENDPOINTGeo+xid+"?apikey="+opentripmapeToken;
        String json = this.api.buildAndExecuteRequest(URL,null);
        logger.info("OpenTripMapService  >>getListTouristPlaces: response to get place details: "+json);

        logger.info("OpenTripMapService  >>getPlaceDetails: End method.");
        return this.objectMapper.readTree(json);
    }

    public PlacesData generatePlaces(PlacesData placesData,List<String> kinds) throws IOException {
        logger.info("OpenTripMapService  >>generatePlaces: Start method.");

        if (
                (this.openTripMapRepository.findByCity(placesData.getCity())!=null) // if PlacesData in database not null.
            &&  (!(this.openTripMapRepository.findByCity(placesData.getCity()).getKindsCategory().getCategories().isEmpty())) // AND if the list of categories are not empty.
            &&  this.openTripMapRepository.findByCity(placesData.getCity()).getKindsCategory().getCategories()==placesData.getKindsCategory().getCategories() // if the existing categories EQUALS to INPUT categories.
        )
        {
            logger.info("OpenTripMapService  >>generatePlaces: Found a existing places.");
            return placesData;
        }

        logger.info("OpenTripMapService  >>generatePlaces: Did found a places, start validation & generate places.");

        for (String kind : kinds) {
            kind = kind.replace(" ","");
            int countPlacesCurrent ;
            if (placesData.getKindsCategory().getCategories().isEmpty() || placesData.getKindsCategory().getCategory(kind).getPlaces().isEmpty()) {
                countPlacesCurrent=0;
            }else {
                countPlacesCurrent = placesData.getKindsCategory().getCategory(kind).getPlaces().size();
            }

            int counterPlaces = (placesData.getLimitPlaces() - countPlacesCurrent );
            logger.info("OpenTripMapService  >>generatePlaces: Get "+counterPlaces+" places for category: "+kind);

            if(counterPlaces>0){
                JsonNode rootNode = this.getListPlaces(placesData, kind,counterPlaces);
                JsonNode dataNode = rootNode.get("features");
                if (dataNode.isEmpty()) {
                    throw new IllegalArgumentException("Did not found places.");
                }

                for (JsonNode mainPlaceDataNode : dataNode) {
                    PlaceDetails placeDetails = this.generatePlaceDetails(mainPlaceDataNode);
                    placesData.getKindsCategory().updateCategories(placeDetails, kind);
                }
            }
        }
        this.savePlacesData(placesData);
        logger.info("OpenTripMapService  >>generatePlaces: Places generated and saved successfully.");
        logger.info("OpenTripMapService  >>generatePlaces: End method.");
        return placesData;
}

    public Boolean deletePlacesDataAll(){
        logger.info("OpenTripMapService  >>deletePlacesDataAll: Start method.");
        this.openTripMapRepository.deleteAll();
        logger.info("OpenTripMapService  >>deletePlacesDataAll: End method.");
        return Boolean.TRUE;
    }

    private JsonNode getListPlaces(PlacesData placesData,String kind, Integer counterPlaces) throws IOException {
        logger.info("OpenTripMapService  >>generatePlaces: Start method.");


        String URL = opentripmapeEndpoint+"radius?radius="+ placesData.getRadius()+"&lon="+ placesData.getLon()+"&lat="+ placesData.getLat()+"&kinds="+kind+"&limit="+counterPlaces+"&apikey="+opentripmapeToken;
        String json = this.api.buildAndExecuteRequest(URL,null);
        logger.info("OpenTripMapService  >>generatePlaces: response to get list of places: "+json);

        logger.info("OpenTripMapService  >>generatePlaces: End method.");
        return this.objectMapper.readTree(json);
    }


    private PlaceDetails generatePlaceDetails(JsonNode mainPlaceDataNode) throws IOException {

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
                ifFieldExists(placeData,"address","postcode"),
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

    private void savePlacesData(PlacesData newPlacesData){
        logger.info("OpenTripMapService  >>savePlacesData: Start method.");
        PlacesData existedPlacesData = this.openTripMapRepository.findByCity(newPlacesData.getCity());
        if (existedPlacesData != null){
            this.openTripMapRepository.delete(existedPlacesData);
        }
        this.openTripMapRepository.save(newPlacesData);
        logger.info("OpenTripMapService  >>savePlacesData: End method.");
    }

    public PlacesData getSpecificPlace(String cityName){
        return this.openTripMapRepository.findByCity(cityName);

    }

}