package com.orengolan.CheapTrips.service;


import com.orengolan.CheapTrips.model.City;
import com.orengolan.CheapTrips.repository.CountriesRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.logging.Logger;

@Service
public class FlightDataAPI {
    private static final Logger logger = Logger.getLogger(FlightDataAPI.class.getName());
    private static final String API_URL = "https://api.travelpayouts.com/data/en/cities.json";
    private static final String SUCCESS_MESSAGE = "City list processed successfully.";
    private final CountriesRepository countriesRepository;
    private final ObjectMapper objectMapper;
    private final OkHttpClient client = new OkHttpClient();

    @Autowired
    public FlightDataAPI(CountriesRepository contriesRepository, ObjectMapper objectMapper) {
        this.countriesRepository = contriesRepository;
        this.objectMapper = objectMapper;
    }

    public String getCitiesList() throws IOException {
        logger.info("Starting get cities list");
        Request request = new Request.Builder().url(API_URL).build();

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

    public String synchronizeCityDataWithAPI() {
        try {
            logger.info("Starting synchronize cities data from API.");
            String json = getCitiesList();

            // Parse the JSON array
            JsonNode cityList = objectMapper.readTree(json);

            for (JsonNode cityNode : cityList) {
                JsonNode nameNode = cityNode.path("name_translations").path("en");
                if (nameNode.isMissingNode() || nameNode.isNull()) {
                    // Skip processing this city if name is null
                    continue;
                }

                String name = nameNode.asText().toLowerCase();
                String countryCode = cityNode.path("country_code").asText().toUpperCase();
                String code = cityNode.path("code").asText().toUpperCase();
                String timeZone = cityNode.path("time_zone").asText();
                double lat = cityNode.path("coordinates").path("lat").asDouble();
                double lon = cityNode.path("coordinates").path("lon").asDouble();

                // Check if a city with the same name already exists
                City existingCity = countriesRepository.findByCityName(name);
                if (existingCity != null) {
                    // Handle the case where the city already exists (update or skip)
                    // For example, you might update existingCity with the new data
                    existingCity.setCountryIATA(countryCode);
                    existingCity.setCityIATA(code);
                    existingCity.setTimeZone(timeZone);
                    existingCity.setLatCoordinates(lat);
                    existingCity.setLonCoordinates(lon);
                    countriesRepository.save(existingCity);
                } else {
                    // Create a new City object and save it to the repository
                    City city = new City(name, countryCode, code, timeZone, lat, lon);
                    countriesRepository.save(city);
                }
            }
            logger.info("Finished synchronize cities data from API.");
            return SUCCESS_MESSAGE;
        }catch(IOException e) {
            logger.severe("Error synchronize city data: " + e.getMessage());
            return "Error processing synchronize city data: " + e.getMessage();
        }

    }

    public Boolean deleteCities(){
        logger.info("Starting delete cities data from DB.");
        // Clear the existing collection of cities
        countriesRepository.deleteAll();
        logger.info("Finished delete cities data from DB.");
        return Boolean.TRUE;
    }
}