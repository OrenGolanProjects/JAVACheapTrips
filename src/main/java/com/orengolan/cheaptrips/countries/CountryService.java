package com.orengolan.cheaptrips.countries;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orengolan.cheaptrips.city.CityService;
import com.orengolan.cheaptrips.util.API;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Service
public class CountryService {

    private static final Logger logger = Logger.getLogger(CityService.class.getName());
    private final API api;
    private final ObjectMapper objectMapper;

    private final CountryRepository countryRepository;

    private final String countryEndpoint;

    public CountryService(Dotenv dotenv, API api, ObjectMapper objectMapper, CountryRepository countryRepository) {
        this.api = api;
        this.objectMapper = objectMapper;
        this.countryRepository = countryRepository;
        this.countryEndpoint = dotenv.get("country_ENDPOINT");
    }

    private String getCountriesData() throws IOException {
        logger.info("CountryService>>  getCountriesData: Start method.");
        return this.api.buildAndExecuteRequest(countryEndpoint,null);
    }


    public void synchronizeCountryDataWithAPI() throws IOException {
        logger.info("CountryService>>  synchronizeCountryDataWithAPI: Start method.");
        String jsonCountry = getCountriesData();
        JsonNode countryNode = objectMapper.readTree(jsonCountry);

        for (JsonNode node : countryNode) {
            String currency = node.get("currency").asText();
            String nameEn =  node.path("name_translations").get("en").asText();
            String IATACode = node.get("code").asText();
            Country newCountry = new Country(nameEn,IATACode,currency);
            saveCountryAtRedis(newCountry);
        }
        logger.info("CountryService>>  synchronizeCountryDataWithAPI: End method.");
    }
    private void saveCountryAtRedis(Country country){
        if(this.countryRepository.findByCountryIATACode(country.getCountryIATACode()) == null) {
            this.countryRepository.save(country);
        }
    }

    public Country fetchCountry(String countryIATACode){
        return this.countryRepository.findByCountryIATACode(countryIATACode);
    }
    public List<Country> getAllCountries(){
        return this.countryRepository.findAll();
    }



}
