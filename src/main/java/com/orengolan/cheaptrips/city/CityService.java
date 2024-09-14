package com.orengolan.cheaptrips.city;

import com.mongodb.DuplicateKeyException;
import com.mongodb.client.result.UpdateResult;
import com.orengolan.cheaptrips.util.API;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * The {@code CityService} class provides business logic and service operations related to City entities in the
 * CheapTrips backend application. It acts as an intermediary between the CityController (presentation layer)
 * and CityRepository (data access layer), encapsulating functionalities such as data synchronization with an external API,
 * CRUD operations, and city data management.
 *
 * The service offers the following main functionalities:
 * - Synchronize City data with an external API.
 * - Delete all existing cities from the database.
 * - Fetch specific cities by name or IATA code.
 * - Create a new city with specified details.
 * - Update details of an existing city.
 *
 * The class employs the CityRepository for database interactions, MongoDB for data storage, and the API class
 * for making requests to external endpoints.
 *
 * Usage Example:
 * <pre>
 * {@code
 * // Synchronize city data with an external API
 * cityService.synchronizeCityDataWithAPI();
 *
 * // Delete all existing cities from the database
 * cityService.deleteCities();
 *
 * // Fetch specific city by name
 * List<City> specificCities = cityService.fetchSpecificCityByName("London");
 *
 * // Create a new city
 * City newCity = new City("New City", "NEW", "UTC+0", 40.7128, -74.0060, "USA");
 * cityService.createNewCity(newCity);
 *
 * // Update city details
 * UpdateResult result = cityService.updateCityData("cityId123", "Updated City", "USA", "NYC", 40.7128, -74.0060, "UTC-5");
 * }
 * </pre>
 *
 * This {@code CityService} class serves as a central component for managing City entities, providing a clean separation
 * of concerns within the CheapTrips application.
 */
@Service
public class CityService {
    private static final Logger logger = Logger.getLogger(CityService.class.getName());
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;
    private final CityRepository cityRepository;
    private final API api;
    private final String cityEndpoint;


    public CityService(Dotenv dotenv, MongoTemplate mongoTemplate, ObjectMapper objectMapper, CityRepository cityRepository, API api) {
        this.mongoTemplate = mongoTemplate;
        this.objectMapper = objectMapper;
        this.cityRepository = cityRepository;
        this.api = api;
        this.cityEndpoint = dotenv.get("city_ENDPOINT");
    }

    public String getCitiesData() throws IOException {
        logger.info("CityService>>  getCitiesList: Start method.");
        logger.info("CityService>>  getCitiesList: ENDPOINT URL: "+cityEndpoint);
        return this.api.buildAndExecuteRequest(cityEndpoint,null);
    }


    public Boolean synchronizeCityDataWithAPI() throws IOException {
        logger.info("CityService>>  synchronizeCityDataWithAPI: Starting synchronize cities data from API.");
        String jsonCity = getCitiesData();

        // Parse the JSON array
        JsonNode cityJsonNode = objectMapper.readTree(jsonCity);
        for (JsonNode cityNode : cityJsonNode) {
            JsonNode cityIataCodeNode = cityNode.path("code");
            if (cityIataCodeNode.isMissingNode() || cityIataCodeNode.isNull()) {
                logger.info("CityService>>  synchronizeCityDataWithAPI: SKIPPED.");
                logger.info("is missing: "+cityIataCodeNode.isMissingNode()+" is null: "+cityIataCodeNode.isNull());
                continue;
            }

            String cityName = cityNode.path("name_translations").path("en").asText().toLowerCase();
            String countryIataCode = cityNode.path("country_code").asText().toUpperCase();
            String cityIataCode = cityIataCodeNode.asText().toUpperCase();
            String timeZone = cityNode.path("time_zone").asText();
            double lat = cityNode.path("coordinates").path("lat").asDouble();
            double lon = cityNode.path("coordinates").path("lon").asDouble();
            City city = new City(cityName,cityIataCode,timeZone,lat,lon,countryIataCode);
            try {
                cityRepository.save(city);
            }
            catch(DuplicateKeyException error){
                continue;
            }
        }

        logger.info("CityService>>  synchronizeCityDataWithAPI: Finished synchronize cities data from API.");
        return Boolean.TRUE;
    }


    public Boolean deleteCities(){
        logger.info("CityService>>  deleteCities: Starting delete cities data from DB.");
        // Clear the existing collection of cities
        cityRepository.deleteAll();
        mongoTemplate.dropCollection("cities");
        logger.info("CityService>>  deleteCities: Finished delete cities data from DB.");
        return Boolean.TRUE;
    }

    public List<City> fetchSpecificCityByName(String cityName) {
        logger.info("CityService>>  fetchSpecificCityByName: Start method fetch Specific City:"+ cityName);
        // Start by searching for search the EXACT string.
        List<City> cities = this.cityRepository.findByCityName(cityName);
        if (cities.isEmpty()) {
            throw new IllegalArgumentException("City with name " + cityName + " not found.");
        }
        return cities;
    }

    public City fetchSpecificCityByIATA(String cityIATACode) {
        logger.info("CityService>>  fetchSpecificCityByIATA: Start method fetch Specific City:"+ cityIATACode);
        // Start by searching for search the EXACT string.
        return this.cityRepository.findByCityIATACode(cityIATACode);
    }

    public boolean createNewCity(City city){
        logger.info("CityService>>  createNewCity: Start method");
        logger.info("CityService>>  createNewCity: Creating a new city: " + city);
        if (this.cityRepository.findByCityName(city.getCityName()) == null){
            this.cityRepository.insert(city);
            logger.info("CityService>>  createNewCity: End method");
            return true;
        }
        return false;
    }

    public UpdateResult updateCityData(String cityId, String cityName, String countryIATA, String cityIATA, Double latCoordinates, Double lonCoordinates, String timeZone) throws ChangeSetPersister.NotFoundException {
        logger.info("CityService>>  updateCityData: Start method");

        Query query = new Query(Criteria.where("_id").is(cityId));
        Update update = new Update();

        addFieldIfNotEmpty(update, "cityName", cityName);
        addFieldIfNotEmpty(update, "countryIATA", countryIATA);
        addFieldIfNotEmpty(update, "cityIATA", cityIATA);
        addFieldIfNotEmpty(update, "timeZone", timeZone);
        addFieldIfNotNullAndPositive(update, "latCoordinates", latCoordinates);
        addFieldIfNotNullAndPositive(update, "lonCoordinates", lonCoordinates);

        UpdateResult result = mongoTemplate.updateFirst(query, update, City.class);

        if (result.getMatchedCount() == 0) {
            throw new ChangeSetPersister.NotFoundException();
        }
        logger.info("CityService>>  updateCityData: End method");
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
