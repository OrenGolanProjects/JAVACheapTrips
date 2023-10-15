package com.orengolan.CheapTrips.city;


import com.mongodb.DuplicateKeyException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Service
public class CityService {

    //TODO Needs to move the variables to Secure storge: API_URL

    @Autowired
    private MongoTemplate mongoTemplate;
    private static final Logger logger = Logger.getLogger(CityService.class.getName());
    private static final String API_URL = "https://api.travelpayouts.com/data/en/cities.json";
    private final CityRepository cityRepository;
    private final ObjectMapper objectMapper;
    private final OkHttpClient client = new OkHttpClient();



    @Autowired
    public CityService(CityRepository cityRepository, ObjectMapper objectMapper) {
        this.cityRepository = cityRepository;
        this.objectMapper = objectMapper;
    }

    public String getCitiesList() throws IOException {
        logger.info("Starting get cities list");
        Request request = new Request.Builder().url(API_URL).build();


        // TODO before execute the request, need to activate LOCK_DOWN_MECHANISM - LOCK_DOWN_MECHANISM needs to developed.
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            if (response.body() != null) {
                logger.info("Finished get cities list.");
                return response.body().string();
            } else {
                throw new IOException("Body of response if empty. " + response);
            }
        } catch(IOException e) {
            logger.severe("Error get city list: " + e.getMessage());
            return "Error get city list: " + e.getMessage();
        }
    }

    public String synchronizeCityDataWithAPI() throws IOException {
        logger.info("Starting synchronize cities data from API.");
        String json = getCitiesList();

        // Parse the JSON array
        JsonNode cityList = objectMapper.readTree(json);
        for (JsonNode cityNode : cityList) {
            try {
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
                if (existingCity != null) {
                    logger.info("Found existing city, City:" + cityName + " IATA Code: " + cityIataCode + " Skipped.");
                    continue;
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
            } catch (Exception e) {
                logger.severe("Error synchronize city data: " + e.getMessage());
                return "Error processing synchronize city data: " + e.getMessage();
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
}
