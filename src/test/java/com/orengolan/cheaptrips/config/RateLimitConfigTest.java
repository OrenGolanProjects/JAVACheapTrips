package com.orengolan.cheaptrips.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@SpringBootTest
@AutoConfigureMockMvc
public class RateLimitConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser // Add this annotation to simulate an authenticated user
    public void testRateLimit() throws Exception {
        // Simulate 100 requests within a short time period
        for (int i = 0; i < 100; i++) {
            mockMvc.perform(MockMvcRequestBuilders.get("/cheap-trip/city-search?cityName=London")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }

        // The 11th request should be rate-limited
        mockMvc.perform(MockMvcRequestBuilders.get("/cheap-trip/city-search?cityName=Paris")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isTooManyRequests());
    }
}