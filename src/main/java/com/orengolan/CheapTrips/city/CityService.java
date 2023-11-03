package com.orengolan.CheapTrips.city;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.result.UpdateResult;
import com.orengolan.CheapTrips.util.API;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.logging.Logger;

@Service
public class CityService {

    //TODO Needs to move the variables to Secure storage: API_URL

    private final MongoTemplate mongoTemplate;
    private static final Logger logger = Logger.getLogger(CityService.class.getName());
    private static final String API_URL = "https://api.travelpayouts.com/data/en/cities.json";
    private final CityRepository cityRepository;
    private final ObjectMapper objectMapper;
    private final API api;



    @Autowired
    public CityService(CityRepository cityRepository, ObjectMapper objectMapper, MongoTemplate mongoTemplate,API api) {
        this.cityRepository = cityRepository;
        this.objectMapper = objectMapper;
        this.mongoTemplate = mongoTemplate;
        this.api =api;
    }

    private String getCitiesData() {
        logger.info("CityService>>getCitiesList: Start method.");
        logger.info("CityService>>getCitiesList: Data: "+API_URL);

        return this.api.buildAndExecuteRequest(API_URL,null);
    }

    public String synchronizeCityDataWithAPI() throws JsonProcessingException {
        logger.info("Starting synchronize cities data from API.");
        String json = getCitiesData();

        // Parse the JSON array
        JsonNode cityList = objectMapper.readTree(json);
        for (JsonNode cityNode : cityList) {
                JsonNode cityIataCodeNode = cityNode.path("code");
                if (cityIataCodeNode.isMissingNode() || cityIataCodeNode.isNull()) {
                    // Skip processing this city if name is null
                    continue;
                }

                String cityName = cityNode.path("name_translations").path("en").asText().toLowerCase();
                String countryIataCode = cityNode.path("country_code").asText().toUpperCase();
                String cityIataCode = cityIataCodeNode.asText().toUpperCase();
                String timeZone = cityNode.path("time_zone").asText();
                double lat = cityNode.path("coordinates").path("lat").asDouble();
                double lon = cityNode.path("coordinates").path("lon").asDouble();

                // Check if a city with the same name already exists
                City existingCity = cityRepository.findByCityIATACode(cityIataCode);
                if (existingCity == null) {
                    logger.info("Found existing city, City:" + cityName + " IATA Code: " + cityIataCode + " Skipped.");
                }
                else {
                    // Create a new City object and save it to the repository
                    List<City> listCitiesByIataCode = this.cityRepository.findCityByCityIATACode(cityIataCode);
                    if (listCitiesByIataCode.size() > 1) {
                        logger.warning("Multiple cities: found more then one IATA Code. " + cityName + " " + cityIataCode);
                        continue;
                    }
                    City city = new City(cityName, countryIataCode, cityIataCode, timeZone, lat, lon);
                    cityRepository.save(city);
                }
        }
        logger.info("Finished synchronize cities data from API.");
        return "Finished synchronize cities data from API.";
    }


    public Boolean deleteCities(){
        logger.info("Starting delete cities data from DB.");
        // Clear the existing collection of cities
        cityRepository.deleteAll();
        mongoTemplate.dropCollection("cities");
        logger.info("Finished delete cities data from DB.");
        return Boolean.TRUE;
    }

    public City fetchSpecificCity(String cityName) {
        logger.info("CityService>>fetchSpecificCity: Start method fetch SpecificCity, city:"+ cityName);

        // Start by searching for search the EXACT string.
        City city = this.cityRepository.findCityByPartialName(cityName.toLowerCase());
        if (city == null) {

            List<City> cities = this.cityRepository.findBycityName(cityName.toLowerCase());
            if (!cities.isEmpty()){
                logger.warning("Found "+cities.size()+" matching cities found for city name: " + cityName);
                logger.warning("Returning the first one.");
                return cities.get(0);
            }
            throw new IllegalArgumentException("City " + cityName + " is not found.");
        }
        // Success found a city
        return city;
    }

    public boolean createNewCity(String cityName,String countryIATA,String cityIATA,String timeZone,Double latCoordinates,Double lonCoordinates
    ){
        if (    cityName == null || cityName.isEmpty() ||
                countryIATA == null || countryIATA.isEmpty() ||
                cityIATA == null || cityIATA.isEmpty() ||
                timeZone == null || timeZone.isEmpty() ||
                latCoordinates == null || latCoordinates == 0 ||
                lonCoordinates == null || lonCoordinates == 0 ){
            throw new IllegalArgumentException("Invalid input parameters");
        }

        logger.info("Creating a new city: " + cityName);
        City newCity = new City(cityName, countryIATA, cityIATA, timeZone, latCoordinates, lonCoordinates);
        if (this.cityRepository.findBycityName(cityName).isEmpty()){
            this.cityRepository.insert(newCity);
            return true;
        }
        return false;
    }

    public UpdateResult updateCityData(String cityId, String cityName, String countryIATA, String cityIATA, Double latCoordinates, Double lonCoordinates, String timeZone){

        Query query = new Query(Criteria.where("_id").is(cityId));
        Update update = new Update();

        addFieldIfNotEmpty(update, "cityName", cityName);
        addFieldIfNotEmpty(update, "countryIATA", countryIATA);
        addFieldIfNotEmpty(update, "cityIATA", cityIATA);
        addFieldIfNotEmpty(update, "timeZone", timeZone);
        addFieldIfNotNullAndPositive(update, "latCoordinates", latCoordinates);
        addFieldIfNotNullAndPositive(update, "lonCoordinates", lonCoordinates);
        return mongoTemplate.updateFirst(query, update, City.class);
    }


    private void addFieldIfNotEmpty(Update update, String field, String value) {
        if (value != null && !value.trim().isEmpty()) {
            update.set(field, value);
        }
    }
    private void addFieldIfNotNullAndPositive(Update update, String field, Double value) {
        if (value != null && value > 0) {
            update.set(field, value);
        }
    }

}
