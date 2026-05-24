package com.example.urlshortener.url.controller;

import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.url.dto.CreateUrlRequest;
import com.example.urlshortener.url.dto.UrlResponse;
import com.example.urlshortener.url.service.UrlService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlService urlService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/urls with valid request returns 201")
    void createUrl_validRequest_returns201() throws Exception {
        CreateUrlRequest request = new CreateUrlRequest("https://example.com", null, null);
        UrlResponse response = new UrlResponse(
                "abc1234",
                "https://example.com",
                "http://localhost:8080/abc1234",
                null,
                0L,
                Instant.now(),
                null,
                false
        );

        when(urlService.createUrl(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("abc1234"))
                .andExpect(jsonPath("$.originalUrl").value("https://example.com"));
    }

    @Test
    @DisplayName("POST /api/v1/urls with blank URL returns 400")
    void createUrl_invalidRequest_returns400() throws Exception {
        CreateUrlRequest request = new CreateUrlRequest("", null, null);

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/urls returns 200 with list")
    void getAllUrls_returns200WithList() throws Exception {
        List<UrlResponse> urls = List.of(
                new UrlResponse(
                        "abc1234",
                        "https://example.com",
                        "http://localhost:8080/abc1234",
                        null,
                        0L,
                        Instant.now(),
                        null,
                        false
                )
        );
        when(urlService.getAllUrls()).thenReturn(urls);

        mockMvc.perform(get("/api/v1/urls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("abc1234"));
    }

    @Test
    @DisplayName("GET /api/v1/urls/{code} not found returns 404")
    void getUrlByCode_notFound_returns404() throws Exception {
        when(urlService.getUrlByCode("invalid")).thenThrow(new UrlNotFoundException("invalid"));

        mockMvc.perform(get("/api/v1/urls/invalid"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/urls/{code} returns 204")
    void deleteUrl_success_returns204() throws Exception {
        doNothing().when(urlService).deleteUrl("abc1234");

        mockMvc.perform(delete("/api/v1/urls/abc1234"))
                .andExpect(status().isNoContent());
    }
}
