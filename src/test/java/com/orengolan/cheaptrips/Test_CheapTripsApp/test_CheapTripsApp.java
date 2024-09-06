package com.orengolan.cheaptrips.Test_CheapTripsApp;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orengolan.cheaptrips.cheaptripsapp.CheapTripsRequest;
import com.orengolan.cheaptrips.userinformation.UserInfo;
import com.orengolan.cheaptrips.userinformation.UserInfoService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.MockMvc;
import java.util.stream.Stream;
import static org.mockito.Mockito.when;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class test_CheapTripsApp {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserInfoService userInfoService;

    @Autowired
    private ObjectMapper objectMapper;
    private String jwtToken;
    private String userIdentifier;


    @BeforeEach
    public void setup() throws Exception {
        // Perform authentication and store the token
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\",\"password\":\"Password1\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        String responseBody = response.getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        jwtToken = "Bearer " + jsonNode.get("token").asText();
        userIdentifier = "user@example.com";
    }



    // =================== START TEST SEARCH CITIES ===================
    @ParameterizedTest
    @ValueSource(strings = {
            "New York", "Tokyo", "frankfurt"
    })
    @Order(1)
    public void positive_test_SearchCities(String city) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/cheap-trip/city-search")
                        .header("Authorization", jwtToken)
                        .param("cityName", city)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Elvenwood","Starfall"
    })
    @Order(2)
    public void negative_test_SearchCities(String city) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/cheap-trip/city-search")
                        .header("Authorization", jwtToken)
                        .param("cityName", city)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("City with name "+city.toLowerCase()+" not found."));

    }
    // =================== END TEST SEARCH CITIES ===================

    // =================== START TEST SEARCH CHEAP TRIPS ===================

    // Method to provide test data for monthly trips
    static Stream<Arguments> provideMonthlyTripData() {
        return Stream.of(
                // TLV to common destinations
                Arguments.of("TLV", "NYC", "New York", 9000, 2),
                Arguments.of("TLV", "LON", "London", 4000, 4),
                Arguments.of("TLV", "PAR", "Paris", 3300, 3),
                Arguments.of("TLV", "BKK", "Bangkok", 7000, 5),
                Arguments.of("TLV", "BER", "Berlin", 2800, 3),
                Arguments.of("TLV", "ROM", "Rome", 2300, 4),
                Arguments.of("TLV", "BOM", "Mumbai", 4000, 3),
                Arguments.of("TLV", "ATH", "Athens", 1200, 5),
                Arguments.of("TLV", "IST", "Istanbul", 1100, 3),
                Arguments.of("TLV", "BCN", "Barcelona", 3200, 4),

                // Random origins and destinations
                Arguments.of("NYC", "LON", "London", 5600, 2),
                Arguments.of("PAR", "TYO", "Tokyo", 9700, 4),
                Arguments.of("LAX", "HKG", "Hong Kong", 11400, 5),
                Arguments.of("DXB", "MAD", "Madrid", 6000, 2),
                Arguments.of("SYD", "SIN", "Singapore", 6300, 3),
                Arguments.of("SEL", "BKK", "Bangkok", 4500, 4)
        );
    }

    // Method to provide test data for trips by dates
    static Stream<Arguments> provideTripByDatesData() {
        return Stream.of(
                // TLV to common destinations
                Arguments.of("TLV", "NYC", "New York", 9000, 2, "2024-09-07", "2024-09-22"),
                Arguments.of("TLV", "LON", "London", 4000, 4, "2024-10-07", "2024-10-22"),
                Arguments.of("TLV", "PAR", "Paris", 3300, 3, "2024-11-06", "2024-11-21"),
                Arguments.of("TLV", "BKK", "Bangkok", 7000, 5, "2024-12-06", "2024-12-21"),
                Arguments.of("TLV", "BER", "Berlin", 2800, 3, "2025-01-05", "2025-01-20"),
                Arguments.of("TLV", "ROM", "Rome", 2300, 4, "2025-02-04", "2025-02-19"),
                Arguments.of("TLV", "BOM", "Mumbai", 4000, 3, "2025-03-06", "2025-03-21"),
                Arguments.of("TLV", "ATH", "Athens", 1200, 5, "2025-04-05", "2025-04-20"),
                Arguments.of("TLV", "IST", "Istanbul", 1100, 3, "2025-05-05", "2025-05-20"),
                Arguments.of("TLV", "BCN", "Barcelona", 3200, 4, "2025-06-04", "2025-06-19"),

                // Random origins and destinations
                Arguments.of("NYC", "LON", "London", 5600, 2, "2025-07-04", "2025-07-19"),
                Arguments.of("PAR", "TYO", "Tokyo", 9700, 4, "2025-08-03", "2025-08-18"),
                Arguments.of("LAX", "HKG", "Hong Kong", 11400, 5, "2025-10-02", "2025-10-17"),
                Arguments.of("DXB", "MAD", "Madrid", 6000, 2, "2025-11-01", "2025-11-16"),
                Arguments.of("SYD", "SIN", "Singapore", 6300, 3, "2025-12-01", "2025-12-16"),
                Arguments.of("SEL", "BKK", "Bangkok", 4500, 4, "2026-01-30", "2026-02-14")

        );
    }



    @ParameterizedTest
    @MethodSource("provideMonthlyTripData")
    @Order(3)
    public void positive_test_monthlyTrip(String origin, String destination, String cityName, int radius, int limitPlaces) throws Exception {
        // Mock the behavior of the service
        when(userInfoService.getUserByIdentifier(userIdentifier)).thenReturn(new UserInfo("John", "Dho", "john@example.com", "1234567890", "JohnD"));

        // Create a CheapTripsRequest object with necessary data
        CheapTripsRequest request = new CheapTripsRequest(origin, destination, cityName, radius, limitPlaces);

        // Perform the request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.post("/cheap-trip/generate-monthly-trip")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flight.origin.city.cityIATACode").value(origin))
                .andExpect(MockMvcResultMatchers.jsonPath("$.flight.destination.city.cityIATACode").value(destination))
                .andExpect(MockMvcResultMatchers.jsonPath("$.news.newsList").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.placesData.radius").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.placesData.lat").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.placesData.lon").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.placesData.limitPlaces").isNotEmpty());
    }

    @ParameterizedTest
    @MethodSource("provideTripByDatesData")
    @Order(4)
    public void positive_test_generateTripByDates(String origin, String destination, String cityName, int radius, int limitPlaces, String departDate, String returnDate) throws Exception {
        // Mock the behavior of the service
        when(userInfoService.getUserByIdentifier(userIdentifier)).thenReturn(new UserInfo("John", "Dho", "john@example.com", "1234567890", "JohnD"));

        // Create a CheapTripsRequest object with necessary data
        CheapTripsRequest request = new CheapTripsRequest(origin, destination, cityName, radius, limitPlaces);

        // Perform the request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.post("/cheap-trip/generate-trip-by-dates")
                        .header("Authorization", jwtToken)
                        .param("depart_date", departDate)
                        .param("return_date", returnDate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flight.origin.city.cityIATACode").value(origin))
                .andExpect(MockMvcResultMatchers.jsonPath("$.flight.destination.city.cityIATACode").value(destination))
                .andExpect(MockMvcResultMatchers.jsonPath("$.news.newsList").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.placesData.radius").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.placesData.lat").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.placesData.lon").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.placesData.limitPlaces").isNotEmpty());
    }

}
