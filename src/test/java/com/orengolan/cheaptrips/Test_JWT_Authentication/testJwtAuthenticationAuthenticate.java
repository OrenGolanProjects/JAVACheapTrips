package com.orengolan.cheaptrips.Test_JWT_Authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orengolan.cheaptrips.jwt.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class testJwtAuthenticationAuthenticate {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUserDetailsService userDetailsService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private JwtAuthenticationController jwtAuthenticationController;

    @Test
    @Order(1)
    public void positive_test_ExistsUserAuthenticate() throws Exception {
        JwtRequest jwtRequest = new JwtRequest("user@example.com", "Password1");

        UserDetails userDetails = new User(jwtRequest.getEmail(), jwtRequest.getPassword(), new ArrayList<>());


        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(jwtRequest.getEmail(), jwtRequest.getPassword()));
        Mockito.when(userDetailsService.loadUserByUsername(jwtRequest.getEmail())).thenReturn(userDetails);
        String token = "";
        Mockito.when(jwtTokenUtil.generateToken(userDetails)).thenReturn(token);

        mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jwtRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").isNotEmpty());
    }


    @Test
    @Order(2)
    public void negative_test_NoneExistsUserAuthenticate() throws Exception {
        JwtRequest jwtRequest = new JwtRequest("NonExistsUser@example.com", "Password1");

        UserDetails userDetails = new User(jwtRequest.getEmail(), jwtRequest.getPassword(), new ArrayList<>());

        Mockito.when(userDetailsService.loadUserByUsername(jwtRequest.getEmail())).thenReturn(userDetails);

        String token = "";
        Mockito.when(jwtTokenUtil.generateToken(userDetails)).thenReturn(token);

        mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jwtRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("A user with this email and password does not exist"));

    }
}