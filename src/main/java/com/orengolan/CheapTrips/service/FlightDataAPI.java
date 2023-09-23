package com.orengolan.CheapTrips.service;


import com.orengolan.CheapTrips.model.City;
import com.orengolan.CheapTrips.repository.ContriesRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

@Service
public class FlightDataAPI {


    @Autowired
    ContriesRepository contriesRepository;

    @Autowired
    ObjectMapper objectMapper;

    private final OkHttpClient client = new OkHttpClient();

    public String getCityList() throws IOException {
        String apiUrl = "https://api.travelpayouts.com/data/en/cities.json";
        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            assert response.body() != null;
            return response.body().string();
        } catch(IOException e) {
            e.printStackTrace();
            return e.toString();
        }
    }


    public String processCityList() {
        try {
            // Clear the existing collection of cities
            contriesRepository.deleteAll();

            String json = getCityList();

            // Parse the JSON array
            JsonNode cityList = objectMapper.readTree(json);

            for (JsonNode cityNode : cityList) {
                JsonNode nameNode = cityNode.path("name_translations").path("en");
                if (nameNode.isMissingNode() || nameNode.isNull()) {
                    // Skip processing this city if name is null
                    continue;
                }

                String name = nameNode.asText();
                String countryCode = cityNode.path("country_code").asText();
                String code = cityNode.path("code").asText();
                String timeZone = cityNode.path("time_zone").asText();
                double lat = cityNode.path("coordinates").path("lat").asDouble();
                double lon = cityNode.path("coordinates").path("lon").asDouble();

                // Check if a city with the same name already exists
                City existingCity = contriesRepository.findByCityName(name);
                if (existingCity != null) {
                    // Handle the case where the city already exists (update or skip)
                    // For example, you might update existingCity with the new data
                    existingCity.setCountryIATA(countryCode);
                    existingCity.setCityIATA(code);
                    existingCity.setTimeZone(timeZone);
                    existingCity.setLatCoordinates(lat);
                    existingCity.setLonCoordinates(lon);
                    contriesRepository.save(existingCity);
                } else {
                    // Create a new City object and save it to the repository
                    City city = new City(name, countryCode, code, timeZone, lat, lon);
                    contriesRepository.save(city);
                }
            }

            return "City list processed successfully."; // Return success message
        } catch (IOException e) {
            e.printStackTrace();
            return "Error processing city list: " + e.getMessage(); // Return error message
        }
    }



}