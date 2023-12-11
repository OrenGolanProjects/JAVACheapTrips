package com.orengolan.cheaptrips.cheaptripsapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orengolan.cheaptrips.userinformation.UserInfo;
import com.orengolan.cheaptrips.userinformation.UserInfoService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class CheapTripControllerTest {

    @Autowired
    private static MockMvc mockMvc;

    @MockBean
    private UserInfoService userInfoService;

    // Global variables for authentication
    private static String userIdentifier;
    private static String jwtToken;

    @BeforeAll
    static void setUp(@Autowired MockMvc mockMvc) throws Exception {

        // Assign the mockMvc instance to the static field
        CheapTripControllerTest.mockMvc = mockMvc;

        // Perform authentication and store the token
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"john@example.com\",\"password\":\"123\"}")) // Add your authentication details
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        String responseBody = response.getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        jwtToken = "Bearer " + jsonNode.get("token").asText();

        userIdentifier = "john@example.com";
    }

    @Test
    @Order(1)
    void generateMonthlyTrip() throws Exception {
        // Mock the behavior of the service
        when(userInfoService.getUserByIdentifier(userIdentifier)).thenReturn(new UserInfo("John", "Dho", "john@example.com", "1234567890", "JohnD"));

        // Create a CheapTripsRequest object with necessary data
        CheapTripsRequest request = new CheapTripsRequest("TLV","AMS","amsterdam",10000,2);

        // Perform the request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.post("/cheap-trip/generate-monthly-trip")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flight.origin.city.cityName").value("tel aviv-yafo"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.flight.destination.city.cityName").value("amsterdam"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.news.newsList").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.placesData.radius").value(10000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.placesData.lat").value(52.373055))
                .andExpect(MockMvcResultMatchers.jsonPath("$.placesData.lon").value(4.8922224))
                .andExpect(MockMvcResultMatchers.jsonPath("$.placesData.limitPlaces").value(2));
    }

    @Test
    @Order(2)
    void generateTripByDates() throws Exception {
        // Mock the behavior of the service
        when(userInfoService.getUserByIdentifier(userIdentifier)).thenReturn(new UserInfo("John", "Dho", "john@example.com", "1234567890", "JohnD"));

        // Create a CheapTripsRequest object with necessary data
        CheapTripsRequest request = new CheapTripsRequest("TLV","AMS","amsterdam",10000,2);

        // Perform the request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.post("/cheap-trip/generate-trip-by-dates")
                        .param("depart_date", "2024-01-01")
                        .param("return_date", "2024-01-10")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flight.origin.city.cityName").value("tel aviv-yafo"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.flight.destination.city.cityName").value("amsterdam"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.news.newsList").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.placesData.radius").value(10000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.placesData.lat").value(52.373055))
                .andExpect(MockMvcResultMatchers.jsonPath("$.placesData.lon").value(4.8922224))
                .andExpect(MockMvcResultMatchers.jsonPath("$.placesData.limitPlaces").value(2));
    }


    @Test
    @Order(3)
    void citySearchResult() throws Exception {
        // Mock the behavior of the service
        when(userInfoService.getUserByIdentifier(userIdentifier)).thenReturn(new UserInfo("John", "Dho", "john@example.com", "1234567890", "JohnD"));

        // Perform the request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.get("/cheap-trip/city-search")
                        .param("cityName", "Berlin")
                        .header("Authorization", jwtToken))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
