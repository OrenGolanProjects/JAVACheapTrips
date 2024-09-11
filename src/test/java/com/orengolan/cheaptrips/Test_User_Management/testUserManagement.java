package com.orengolan.cheaptrips.Test_User_Management;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orengolan.cheaptrips.jwt.JwtRequest;
import com.orengolan.cheaptrips.jwt.JwtRequestNewUser;
import com.orengolan.cheaptrips.userinformation.UserInfo;
import com.orengolan.cheaptrips.userinformation.UserInfoRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class testUserManagement {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    public void setup(TestInfo testInfo) throws Exception {
        // Perform authentication and store the token
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\",\"password\":\"Password1\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse();
        String responseBody = response.getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        jwtToken = "Bearer " + jsonNode.get("token").asText();

        // Check if the current test method has the @Order annotation with value 11
        Optional<Order> orderAnnotation = testInfo.getTestMethod().flatMap(method -> Optional.ofNullable(method.getAnnotation(Order.class)));

        if (orderAnnotation.isPresent() && orderAnnotation.get().value() == 11) {
            return;
        }
        UserInfo userInfo = new UserInfo("John", "Do", "user@example.com", "123-4567890", "user123");

        mockMvc.perform(MockMvcRequestBuilders.put("/app/userinfo/update-specific-user-info")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInfo)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userName").value("user123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surName").value("Do"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("123-4567890"));

    }

    @Test
    @Order(0)
    public void positive_test_CreateUserSuccessfully_first() throws Exception {


        JwtRequest jwtRequest = new JwtRequest("user@example.com", "Password1");
        UserInfoRequest userInfoRequest = new UserInfoRequest("John", "Do", "123-4567890", "user123");

        JwtRequestNewUser jwtRequestNewUser = new JwtRequestNewUser(jwtRequest, userInfoRequest);
        jwtRequestNewUser.setJwtRequest(jwtRequest);
        jwtRequestNewUser.setUserInfoRequest(userInfoRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jwtRequestNewUser)));
    }

    // =================== START GET USER INFO ===================
    @Test
    @Order(1)
    public void positive_test_GetUserInfo() throws Exception {
        String userIdentifier = "user@example.com";

        mockMvc.perform(MockMvcRequestBuilders.get("/app/userinfo/get-specific-user-info")
                        .param("userIdentifier", userIdentifier)
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userName").value("user123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surName").value("Do"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("123-4567890"));
    }

    @Test
    @Order(2)
    public void negative_test_GetNoneExistIdentifier() throws Exception {
        String userIdentifier = "NoneExistIdentifier";

        mockMvc.perform(MockMvcRequestBuilders.get("/app/userinfo/get-specific-user-info")
                        .param("userIdentifier", userIdentifier)
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("User identifier not found, pleas try again."));
    }
    // =================== END GET USER INFO ===================

    // =================== START UPDATE USER INFO ===================
    @Test
    @Order(3)
    public void positive_test_UpdateUserInfo() throws Exception {
        UserInfo userInfo = new UserInfo("JohnUpdated", "DoUpdated", "user@example.com", "123-9876541", "userUpdated123");

        mockMvc.perform(MockMvcRequestBuilders.put("/app/userinfo/update-specific-user-info")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInfo)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userName").value("userUpdated123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("JohnUpdated"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surName").value("DoUpdated"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("123-9876541"));
    }

    UserInfo userInfo = new UserInfo("John", "Do", "user@example.com", "123-4567890", "user123");


    @Test
    @Order(4)
    public void positive_test_ReturnUserInfoToOrigin() throws Exception {
        UserInfo userInfo = new UserInfo("John", "Do", "user@example.com", "123-4567890", "user123");

        mockMvc.perform(MockMvcRequestBuilders.put("/app/userinfo/update-specific-user-info")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInfo)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userName").value("user123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surName").value("Do"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("123-4567890"));
    }

    @Test
    @Order(5)
    public void positive_test_UpdatedNoChange() throws Exception {
        UserInfo userInfo = new UserInfo("", "", "@example.com", "", "");

        mockMvc.perform(MockMvcRequestBuilders.put("/app/userinfo/update-specific-user-info")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInfo)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userName").value("user123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surName").value("Do"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("123-4567890"));
    }

    @Test
    @Order(6)
    public void positive_test_UpdateUserName() throws Exception {
        UserInfo userInfo = new UserInfo(null, null, "user@example.com", null, "newUserName");

        mockMvc.perform(MockMvcRequestBuilders.put("/app/userinfo/update-specific-user-info")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInfo)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userName").value("newUserName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surName").value("Do"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("123-4567890"));
    }

    @Test
    @Order(7)
    public void positive_test_UpdateFirstName() throws Exception {
        UserInfo userInfo = new UserInfo("newFirstName", null, "user@example.com", null, null);

        mockMvc.perform(MockMvcRequestBuilders.put("/app/userinfo/update-specific-user-info")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInfo)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userName").value("user123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("newFirstName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surName").value("Do"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("123-4567890"));
    }

    @Test
    @Order(8)
    public void positive_test_UpdateSurName() throws Exception {
        UserInfo userInfo = new UserInfo(null, "newSurName", "user@example.com", null, null);

        mockMvc.perform(MockMvcRequestBuilders.put("/app/userinfo/update-specific-user-info")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInfo)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userName").value("user123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surName").value("newSurName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("123-4567890"));
    }

    @Test
    @Order(9)
    public void positive_test_UpdatePhone() throws Exception {
        UserInfo userInfo = new UserInfo(null, null, "user@example.com", "987-6543210", null);

        mockMvc.perform(MockMvcRequestBuilders.put("/app/userinfo/update-specific-user-info")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInfo)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userName").value("user123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surName").value("Do"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("987-6543210"));
    }


    // =================== END UPDATE USER INFO ===================

    // =================== START DELETE USER INFO ===================
    @Test
    @Order(10)
    public void positive_test_DeleteUser() throws Exception {
        UserInfo userInfo = new UserInfo(null, null, "user@example.com", "987-6543210", null);

        mockMvc.perform(MockMvcRequestBuilders.delete("/app/userinfo/delete-specific-user-info")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userInfo)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty());
    }

    @Test
    @Order(11)
    public void negative_test_DeleteNoneExistsUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/app/userinfo/delete-specific-user-info")
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("User not found."));

    }

    @Test
    @Order(12)
    public void positive_test_CreateUserSuccessfully_last() throws Exception {


        JwtRequest jwtRequest = new JwtRequest("user@example.com", "Password1");
        UserInfoRequest userInfoRequest = new UserInfoRequest("John", "Do", "123-4567890", "user123");

        JwtRequestNewUser jwtRequestNewUser = new JwtRequestNewUser(jwtRequest, userInfoRequest);
        jwtRequestNewUser.setJwtRequest(jwtRequest);
        jwtRequestNewUser.setUserInfoRequest(userInfoRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jwtRequestNewUser)));
    }
    // =================== END DELETE USER INFO ===================

}
