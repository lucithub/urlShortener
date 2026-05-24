package com.example.urlshortener;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void contextLoads() {
        // Verifies Spring Boot context loads successfully
    }

    @Test
    public void getAllUrls_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/urls"))
            .andExpect(status().isOk());
    }

    @Test
    public void invalidCode_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/urls/invalid-code-123"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void redirectEndpoint_returnsFoundOrNotFound() throws Exception {
        // Should return 302 (Found) for valid code or 404 for invalid
        mockMvc.perform(get("/xyz999"))
            .andExpect(status().isNotFound());
    }
}
