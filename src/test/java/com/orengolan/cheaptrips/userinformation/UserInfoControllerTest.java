package com.orengolan.cheaptrips.userinformation;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@SpringBootTest
@AutoConfigureMockMvc
public class UserInfoControllerTest {

    @Autowired
    public static MockMvc mockMvc;

    @MockBean
    private UserInfoService userInfoService;

    // Global variables for authentication
    private static String userIdentifier;
    private static String jwtToken;

    @BeforeAll
    static void setUp(@Autowired MockMvc mockMvc) throws Exception {
        // Assign the mockMvc instance to the static field
        UserInfoControllerTest.mockMvc = mockMvc;

        // Perform authentication and store the token
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"john@example.com\",\"password\":\"123\"}")) // Add your authentication details
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();

        String responseBody = response.getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        jwtToken = "Bearer "+jsonNode.get("token").asText();

        userIdentifier = "john@example.com";
    }

    @Test
    @Order(1)
    void createSpecificUser() throws Exception {
        // Mock the behavior of the service
        UserInfoRequest userInfoRequest = new UserInfoRequest("John", "Dho", "1234567890", "JohnD");
        when(userInfoService.createNewUser(userInfoRequest.toUserInto("testUser")))
                .thenReturn(new UserInfo("John", "Dho", "john@example.com", "1234567890", "JohnD"));

        // Perform the POST request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.post("/app/userinfo/create-specific-user-info")
                        .param("userIdentifier", userIdentifier)
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userInfoRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @Order(2)
    void getSpecificUser() throws Exception {
        // Mock the behavior of the service
        when(userInfoService.getUserByIdentifier(userIdentifier)).thenReturn(new UserInfo("John", "Dho","john@example.com","1234567890","orenG"));

        // Perform the GET request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.get("/app/userinfo/get-specific-user-info")
                        .param("userIdentifier", userIdentifier)
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userName").value("orenG"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surName").value("Dho"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("1234567890"));
    }



    @Test
    @Order(3)
    void updateSpecificUser() throws Exception {
        // Mock the behavior of the service
        UserInfoRequest updatedUserInfoRequest = new UserInfoRequest("John", "Dho", "1234567890", "JohnD");
        when(userInfoService.updateUserInfo(userIdentifier, updatedUserInfoRequest.toUserInto(userIdentifier)))
                .thenReturn(new UserInfo("OH", "Yea",userIdentifier,"1234567891","JohnK"));

        // Perform the PUT request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.put("/app/userinfo/update-specific-user-info")
                        .param("userIdentifier", userIdentifier)
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedUserInfoRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Test
    @Order(4)
    void deleteSpecificUser() throws Exception {
        // Mock the behavior of the service
        when(userInfoService.deleteSpecificUser("testUser")).thenReturn(new UserInfo("OH", "Yea",userIdentifier,"1234567891","JohnK"));

        // Perform the DELETE request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.delete("/app/userinfo/delete-specific-user-info")
                .param("userIdentifier", userIdentifier)
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}