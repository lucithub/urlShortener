package com.example.urlshortener;

import com.example.urlshortener.url.dto.CreateUrlRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void fullUrlShorteningFlow() throws Exception {
        // 1. Create a short URL with auto-generated code
        CreateUrlRequest createRequest = new CreateUrlRequest("https://example.com", null, null);

        String response = mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.originalUrl").value("https://example.com"))
                .andExpect(jsonPath("$.shortUrl").exists())
                .andExpect(jsonPath("$.hitCount").value(0))
                .andExpect(jsonPath("$.expired").value(false))
                .andReturn().getResponse().getContentAsString();

        // 2. List all URLs - should have at least 1
        mockMvc.perform(get("/api/v1/urls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        // 3. The created URL is in the list (at least 1 URL exists now)
        mockMvc.perform(get("/api/v1/urls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    public void redirectFlow_withCustomAlias() throws Exception {
        // 1. Create URL with custom alias
        CreateUrlRequest request = new CreateUrlRequest("https://google.com", "e2etest", null);

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("e2etest"))
                .andExpect(jsonPath("$.originalUrl").value("https://google.com"));

        // 2. Test redirect - should return 302 with Location header
        mockMvc.perform(get("/e2etest"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://google.com"));

        // 3. Verify hit count increased after redirect
        mockMvc.perform(get("/api/v1/urls/e2etest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hitCount").value(1));

        // 4. Delete the URL - cleanup
        mockMvc.perform(delete("/api/v1/urls/e2etest"))
                .andExpect(status().isNoContent());

        // 5. Verify deletion - should return 404
        mockMvc.perform(get("/api/v1/urls/e2etest"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createUrl_withExpiration() throws Exception {
        // 1. Create URL with 1 day expiration
        CreateUrlRequest request = new CreateUrlRequest("https://expired-test.com", "expiry1", 1);

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("expiry1"))
                .andExpect(jsonPath("$.expiresAt").exists());

        // 2. Cleanup
        mockMvc.perform(delete("/api/v1/urls/expiry1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void updateUrl_hitCount() throws Exception {
        // 1. Create URL
        CreateUrlRequest request = new CreateUrlRequest("https://hitcount-test.com", null, null);

        String response = mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Extract code from response
        ObjectMapper mapper = new ObjectMapper();
        String code = mapper.readTree(response).get("code").asText();

        // 2. Verify initial hit count is 0
        mockMvc.perform(get("/api/v1/urls/" + code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hitCount").value(0));

        // 3. Trigger redirect multiple times (each increments hit count)
        mockMvc.perform(get("/" + code))
                .andExpect(status().isFound());

        mockMvc.perform(get("/" + code))
                .andExpect(status().isFound());

        // 4. Verify hit count increased
        mockMvc.perform(get("/api/v1/urls/" + code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hitCount").value(2));

        // 5. Cleanup
        mockMvc.perform(delete("/api/v1/urls/" + code))
                .andExpect(status().isNoContent());
    }
}