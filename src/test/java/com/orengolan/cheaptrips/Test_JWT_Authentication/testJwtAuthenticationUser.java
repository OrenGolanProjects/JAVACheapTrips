package com.orengolan.cheaptrips.Test_JWT_Authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orengolan.cheaptrips.jwt.JwtRequest;
import com.orengolan.cheaptrips.jwt.JwtRequestNewUser;
import com.orengolan.cheaptrips.userinformation.UserInfoRequest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Order;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class testJwtAuthenticationUser {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    public void positive_test_CreateUserSuccessfully() throws Exception {
        JwtRequestNewUser jwtRequestNewUser = new JwtRequestNewUser();

        JwtRequest jwtRequest = new JwtRequest("user@example.com", "Password1");
        UserInfoRequest userInfoRequest = new UserInfoRequest("John", "Do", "123-4567890", "user123");

        jwtRequestNewUser.setJwtRequest(jwtRequest);
        jwtRequestNewUser.setUserInfoRequest(userInfoRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jwtRequestNewUser)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").isNotEmpty());
    }

    @Test
    @Order(2)
    public void negative_test_duplicateUser() throws Exception {
        JwtRequestNewUser jwtRequestNewUser = new JwtRequestNewUser();

        JwtRequest jwtRequest = new JwtRequest("user@example.com", "Password1");
        UserInfoRequest userInfoRequest = new UserInfoRequest("John", "Do", "123-4567890", "user123");

        jwtRequestNewUser.setJwtRequest(jwtRequest);
        jwtRequestNewUser.setUserInfoRequest(userInfoRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jwtRequestNewUser)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("User with email user@example.com already exists."));
    }

    @Test
    @Order(3)
    public void negative_test_EmailError() throws Exception {
        JwtRequestNewUser jwtRequestNewUser = new JwtRequestNewUser();

        JwtRequest jwtRequest = new JwtRequest("@example.com", "Password1");
        UserInfoRequest userInfoRequest = new UserInfoRequest("John", "Do", "123-4567890", "user123");

        jwtRequestNewUser.setJwtRequest(jwtRequest);
        jwtRequestNewUser.setUserInfoRequest(userInfoRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jwtRequestNewUser)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("Invalid email format, must be a valid email address"));
    }

    @Test
    @Order(4)
    public void negative_test_PhoneError() throws Exception {
        JwtRequestNewUser jwtRequestNewUser = new JwtRequestNewUser();

        JwtRequest jwtRequest = new JwtRequest("user@example.com", "Password1");
        UserInfoRequest userInfoRequest = new UserInfoRequest("John", "Do", "1234567890", "user123");

        jwtRequestNewUser.setJwtRequest(jwtRequest);
        jwtRequestNewUser.setUserInfoRequest(userInfoRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jwtRequestNewUser)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("Phone number must be in the format XXX-XXXXXXX"));
    }

    @Test
    @Order(5)
    public void negative_test_PhoneAlphabeticChasError() throws Exception {
        JwtRequestNewUser jwtRequestNewUser = new JwtRequestNewUser();

        JwtRequest jwtRequest = new JwtRequest("user@example.com", "Password1");
        UserInfoRequest userInfoRequest = new UserInfoRequest("John", "Do", "@#456asd", "user123");

        jwtRequestNewUser.setJwtRequest(jwtRequest);
        jwtRequestNewUser.setUserInfoRequest(userInfoRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jwtRequestNewUser)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("Phone number must be in the format XXX-XXXXXXX"));
    }

    @Test
    @Order(6)
    public void negative_test_InvalidPassword() throws Exception {
        JwtRequestNewUser jwtRequestNewUser = new JwtRequestNewUser();

        JwtRequest jwtRequest = new JwtRequest("user@example.com", "pass");
        UserInfoRequest userInfoRequest = new UserInfoRequest("John", "Do", "123-4567890", "user123");

        jwtRequestNewUser.setJwtRequest(jwtRequest);
        jwtRequestNewUser.setUserInfoRequest(userInfoRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jwtRequestNewUser)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").value("Password must be at least 8 characters long and contain at least one letter and one number"));
    }

    @Test
    @Order(7)
    public void negative_test_BlankUserName() throws Exception {
        JwtRequestNewUser jwtRequestNewUser = new JwtRequestNewUser();

        JwtRequest jwtRequest = new JwtRequest("user@example.com", "Password1");
        UserInfoRequest userInfoRequest = new UserInfoRequest("John", "Do", "123-4567890", "");

        jwtRequestNewUser.setJwtRequest(jwtRequest);
        jwtRequestNewUser.setUserInfoRequest(userInfoRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jwtRequestNewUser)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userName").value("size must be between 2 and 10"));
    }

    @Test
    @Order(8)
    public void negative_test_InvalidFirstName() throws Exception {
        JwtRequestNewUser jwtRequestNewUser = new JwtRequestNewUser();

        JwtRequest jwtRequest = new JwtRequest("user@example.com", "Password1");
        UserInfoRequest userInfoRequest = new UserInfoRequest("", "Do", "123-4567890", "user123");

        jwtRequestNewUser.setJwtRequest(jwtRequest);
        jwtRequestNewUser.setUserInfoRequest(userInfoRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jwtRequestNewUser)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("size must be between 2 and 10"));
    }

    @Test
    @Order(9)
    public void negative_test_InvalidSurName() throws Exception {
        JwtRequestNewUser jwtRequestNewUser = new JwtRequestNewUser();

        JwtRequest jwtRequest = new JwtRequest("user@example.com", "Password1");
        UserInfoRequest userInfoRequest = new UserInfoRequest("John", "", "123-4567890", "user123");

        jwtRequestNewUser.setJwtRequest(jwtRequest);
        jwtRequestNewUser.setUserInfoRequest(userInfoRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jwtRequestNewUser)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.surName").value("size must be between 2 and 10"));
    }

    @Test
    @Order(10)
    public void negative_test_BlackValues() throws Exception {
        JwtRequestNewUser jwtRequestNewUser = new JwtRequestNewUser();

        JwtRequest jwtRequest = new JwtRequest("", "");
        UserInfoRequest userInfoRequest = new UserInfoRequest("", "", "", "");

        jwtRequestNewUser.setJwtRequest(jwtRequest);
        jwtRequestNewUser.setUserInfoRequest(userInfoRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jwtRequestNewUser)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("Invalid email format, must be a valid email address"));
    }
}
