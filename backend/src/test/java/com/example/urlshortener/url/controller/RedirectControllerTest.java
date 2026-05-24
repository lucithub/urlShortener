package com.example.urlshortener.url.controller;

import com.example.urlshortener.exception.UrlExpiredException;
import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.url.model.ShortUrl;
import com.example.urlshortener.url.service.UrlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RedirectController.class)
class RedirectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlService urlService;

    @Test
    @DisplayName("GET /{code} with valid code returns 302 redirect")
    void redirect_validCode_returns302WithLocation() throws Exception {
        ShortUrl shortUrl = ShortUrl.builder()
                .code("abc1234")
                .targetUrl("https://example.com")
                .hits(0L)
                .createdAt(Instant.now())
                .build();

        when(urlService.findByCodeAndActive("abc1234")).thenReturn(shortUrl);
        doNothing().when(urlService).registerHit(shortUrl);

        mockMvc.perform(get("/abc1234"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com"));

        verify(urlService).registerHit(shortUrl);
    }

    @Test
    @DisplayName("GET /{code} with invalid code returns 404")
    void redirect_invalidCode_returns404() throws Exception {
        when(urlService.findByCodeAndActive("invalid"))
                .thenThrow(new UrlNotFoundException("invalid"));

        mockMvc.perform(get("/invalid"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /{code} with expired code returns 410 Gone")
    void redirect_expiredCode_returns410() throws Exception {
        when(urlService.findByCodeAndActive("expired"))
                .thenThrow(new UrlExpiredException("expired"));

        mockMvc.perform(get("/expired"))
                .andExpect(status().isGone());
    }
}
