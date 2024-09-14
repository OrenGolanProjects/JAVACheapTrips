package com.orengolan.cheaptrips.countries;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orengolan.cheaptrips.city.CityService;
import com.orengolan.cheaptrips.util.API;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * The {@code CountryService} class is responsible for handling business logic related to
 * country data, including synchronization with an external API and interaction with the
 * {@link CountryRepository} for database operations.
 *
 * Key Features:
 * - {@code synchronizeCountryDataWithAPI}: Fetches country data from an external API,
 *   processes the information, and stores new countries in the database.
 * - {@code fetchCountry}: Retrieves a country entity by its unique IATA code from the database.
 * - {@code getAllCountries}: Retrieves a list of all countries stored in the database.
 *
 * Example:
 * The {@code synchronizeCountryDataWithAPI} method can be used to update the application's
 * country data periodically by fetching the latest information from an external source.
 * The {@code fetchCountry} and {@code getAllCountries} methods provide convenient access to
 * country data for various use cases within the application.
 *
 * Note: This class demonstrates the service layer's role in coordinating data synchronization
 * and interaction with the repository for country-related operations.
 */
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

    public String getCountriesData() throws IOException {
        logger.info("CountryService>>  getCountriesData: Start method.");
        return this.api.buildAndExecuteRequest(countryEndpoint,null);
    }


    /**
     * Fetches country data from an external API, processes the information, and stores new
     * countries in the database.
     *
     * @throws IOException If an error occurs while fetching or processing API data.
     */
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

    /**
     * Retrieves a country entity by its unique IATA code from the database.
     *
     * @param countryIATACode The IATA code of the country to be retrieved.
     * @return The country entity matching the provided IATA code, or null if not found.
     */
    public Country fetchCountry(String countryIATACode){
        return this.countryRepository.findByCountryIATACode(countryIATACode);
    }

    /**
     * Retrieves a list of all countries stored in the database.
     *
     * @return A list of country entities.
     */
    public List<Country> getAllCountries(){
        return this.countryRepository.findAll();
    }



}
