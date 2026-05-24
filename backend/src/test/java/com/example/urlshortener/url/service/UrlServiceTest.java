package com.example.urlshortener.url.service;

import com.example.urlshortener.exception.DuplicateAliasException;
import com.example.urlshortener.exception.InvalidUrlException;
import com.example.urlshortener.exception.UrlExpiredException;
import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.url.dto.CreateUrlRequest;
import com.example.urlshortener.url.dto.UrlResponse;
import com.example.urlshortener.url.model.ShortUrl;
import com.example.urlshortener.url.repository.ShortUrlRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private ShortUrlRepository repository;

    @InjectMocks
    private UrlService urlService;

    @Test
    @DisplayName("createUrl with valid request returns UrlResponse")
    void createUrl_validRequest_returnsUrlResponse() {
        // Arrange: CreateUrlRequest with valid URL
        CreateUrlRequest request = new CreateUrlRequest("https://example.com", null, null);

        ShortUrl savedEntity = ShortUrl.builder()
                .id(1L)
                .code("abc1234")
                .targetUrl("https://example.com")
                .hits(0)
                .createdAt(Instant.now())
                .build();

        when(repository.existsByCode(any())).thenReturn(false);
        when(repository.save(any(ShortUrl.class))).thenReturn(savedEntity);

        // Act: call urlService.createUrl(request)
        UrlResponse response = urlService.createUrl(request);

        // Assert: response has correct fields (code, targetUrl, shortUrl)
        assertNotNull(response);
        assertEquals("abc1234", response.code());
        assertEquals("https://example.com", response.originalUrl());
        assertNotNull(response.shortUrl());
        assertTrue(response.shortUrl().contains("abc1234"));
    }

    @Test
    @DisplayName("createUrl with invalid URL throws InvalidUrlException")
    void createUrl_invalidUrl_throwsInvalidUrlException() {
        // Arrange: CreateUrlRequest with URL not starting in http:// or https://
        CreateUrlRequest request = new CreateUrlRequest("not-a-valid-url", null, null);

        // Act & Assert: assertThrows(InvalidUrlException.class, () -> urlService.createUrl(request))
        assertThrows(InvalidUrlException.class, () -> urlService.createUrl(request));
    }

    @Test
    @DisplayName("createUrl with duplicate alias throws DuplicateAliasException")
    void createUrl_duplicateAlias_throwsDuplicateAliasException() {
        // Arrange: CreateUrlRequest with customAlias + mock existsByCustomAlias=true
        CreateUrlRequest request = new CreateUrlRequest("https://example.com", "myalias", null);

        when(repository.existsByCode("myalias")).thenReturn(true);

        // Act & Assert: assertThrows(DuplicateAliasException.class, () -> urlService.createUrl(request))
        assertThrows(DuplicateAliasException.class, () -> urlService.createUrl(request));
    }

    @Test
    @DisplayName("getAllUrls returns list sorted by createdAt desc")
    void getAllUrls_returnsListOfUrlResponse() {
        // Arrange: mock repository.findAllByOrderByCreatedAtDesc() to return list
        List<ShortUrl> entities = List.of(
                ShortUrl.builder()
                        .id(1L)
                        .code("abc1234")
                        .targetUrl("https://example.com")
                        .hits(5)
                        .createdAt(Instant.now())
                        .build()
        );

        when(repository.findAllByOrderByCreatedAtDesc()).thenReturn(entities);

        // Act: call urlService.getAllUrls()
        List<UrlResponse> responses = urlService.getAllUrls();

        // Assert: list size matches, response list not empty
        assertEquals(1, responses.size());
        assertFalse(responses.isEmpty());
        assertEquals("abc1234", responses.get(0).code());
    }

    @Test
    @DisplayName("getUrlByCode when not found throws UrlNotFoundException")
    void getUrlByCode_notFound_throwsUrlNotFoundException() {
        // Arrange: mock repository.findByCode() to return empty
        when(repository.findByCode("invalid")).thenReturn(Optional.empty());

        // Act & Assert: assertThrows(UrlNotFoundException.class, () -> urlService.getUrlByCode("invalid"))
        assertThrows(UrlNotFoundException.class, () -> urlService.getUrlByCode("invalid"));
    }

    @Test
    @DisplayName("deleteUrl when not found throws UrlNotFoundException")
    void deleteUrl_notFound_throwsUrlNotFoundException() {
        // Arrange: mock repository.findByCode() to return empty
        when(repository.findByCode("invalid")).thenReturn(Optional.empty());

        // Act & Assert: assertThrows(UrlNotFoundException.class, () -> urlService.deleteUrl("invalid"))
        assertThrows(UrlNotFoundException.class, () -> urlService.deleteUrl("invalid"));
    }

    @Test
    @DisplayName("findByCodeAndActive with expired URL throws UrlExpiredException")
    void findByCodeAndActive_expired_throwsUrlExpiredException() {
        // Arrange: mock repository.findByCode() to return entity with expired expiresAt
        ShortUrl expiredEntity = ShortUrl.builder()
                .id(1L)
                .code("expired")
                .targetUrl("https://example.com")
                .hits(0)
                .expiresAt(Instant.now().minusSeconds(3600)) // expired 1 hour ago
                .createdAt(Instant.now().minusSeconds(7200))
                .build();

        when(repository.findByCode("expired")).thenReturn(Optional.of(expiredEntity));

        // Act & Assert: assertThrows(UrlExpiredException.class, () -> urlService.findByCodeAndActive("expired"))
        assertThrows(UrlExpiredException.class, () -> urlService.findByCodeAndActive("expired"));
    }

    @Test
    @DisplayName("registerHit increments hits and updates lastAccessedAt")
    void registerHit_incrementsHitsCounter() {
        // Arrange: ShortUrl entity with hits=0
        ShortUrl shortUrl = ShortUrl.builder()
                .id(1L)
                .code("abc1234")
                .targetUrl("https://example.com")
                .hits(0)
                .createdAt(Instant.now())
                .build();

        when(repository.save(any(ShortUrl.class))).thenReturn(shortUrl);

        // Act: call urlService.registerHit(shortUrl)
        urlService.registerHit(shortUrl);

        // Assert: verify repository.save() was called with hits incremented
        verify(repository, times(1)).save(argThat(entity ->
                entity.getHits() == 1 &&
                entity.getLastAccessedAt() != null
        ));
    }
}
