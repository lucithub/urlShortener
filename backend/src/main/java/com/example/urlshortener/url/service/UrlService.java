package com.example.urlshortener.url.service;

import com.example.urlshortener.exception.DuplicateAliasException;
import com.example.urlshortener.exception.InvalidUrlException;
import com.example.urlshortener.exception.UrlExpiredException;
import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.url.dto.CreateUrlRequest;
import com.example.urlshortener.url.dto.UrlResponse;
import com.example.urlshortener.url.model.ShortUrl;
import com.example.urlshortener.url.repository.ShortUrlRepository;
import com.example.urlshortener.util.Base62;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Business logic service for URL shortening operations.
 * Handles URL creation, retrieval, deletion, and redirect tracking.
 */
@Service
public class UrlService {

    private static final String BASE_URL = "http://localhost:8080";
    private static final int MAX_COLLISION_RETRIES = 10;

    private final ShortUrlRepository repository;

    public UrlService(ShortUrlRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates a new shortened URL.
     * Validates the URL format, handles custom aliases, and generates a unique code.
     */
    public UrlResponse createUrl(CreateUrlRequest request) {
        validateUrl(request.url());

        if (request.customAlias() != null && !request.customAlias().isBlank()) {
            if (repository.existsByCode(request.customAlias())) {
                throw new DuplicateAliasException(request.customAlias());
            }
        }

        String code = generateUniqueCode(request.customAlias());

        Instant expiresAt = null;
        if (request.expirationDays() != null) {
            expiresAt = Instant.now().plusSeconds(request.expirationDays() * 86400L);
        }

        ShortUrl entity = ShortUrl.builder()
                .code(code)
                .targetUrl(request.url())
                .expiresAt(expiresAt)
                .build();

        ShortUrl saved = repository.save(entity);
        return toResponse(saved);
    }

    /**
     * Returns all shortened URLs sorted by creation date (newest first).
     */
    public List<UrlResponse> getAllUrls() {
        return repository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Finds a URL by its code.
     *
     * @throws UrlNotFoundException if the code does not exist
     */
    public UrlResponse getUrlByCode(String code) {
        ShortUrl entity = repository.findByCode(code)
                .orElseThrow(() -> new UrlNotFoundException(code));
        return toResponse(entity);
    }

    /**
     * Deletes a URL by its code.
     *
     * @throws UrlNotFoundException if the code does not exist
     */
    public void deleteUrl(String code) {
        ShortUrl entity = repository.findByCode(code)
                .orElseThrow(() -> new UrlNotFoundException(code));
        repository.delete(entity);
    }

    /**
     * Finds an active URL by code, checking for expiration.
     * Used by the redirect endpoint.
     *
     * @throws UrlNotFoundException  if the code does not exist
     * @throws UrlExpiredException   if the URL has expired
     */
    public ShortUrl findByCodeAndActive(String code) {
        ShortUrl entity = repository.findByCode(code)
                .orElseThrow(() -> new UrlNotFoundException(code));

        if (entity.getExpiresAt() != null && entity.getExpiresAt().isBefore(Instant.now())) {
            throw new UrlExpiredException("Short URL has expired on " + entity.getExpiresAt());
        }

        return entity;
    }

    /**
     * Registers a hit for a shortened URL.
     * Increments the hit counter and updates the last accessed timestamp.
     */
    public void registerHit(ShortUrl shortUrl) {
        shortUrl.setHits(shortUrl.getHits() + 1);
        shortUrl.setLastAccessedAt(Instant.now());
        repository.save(shortUrl);
    }

    // ── Private helpers ──────────────────────────────────────────────

    /**
     * Validates that the URL starts with http:// or https://.
     */
    private void validateUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new InvalidUrlException("URL must start with http:// or https://");
        }
    }

    /**
     * Generates a unique code, retrying on collision up to MAX_COLLISION_RETRIES.
     * If a custom alias is provided, it is used directly.
     */
    private String generateUniqueCode(String customAlias) {
        if (customAlias != null && !customAlias.isBlank()) {
            return customAlias;
        }

        for (int i = 0; i < MAX_COLLISION_RETRIES; i++) {
            String code = Base62.generateCode();
            if (!repository.existsByCode(code)) {
                return code;
            }
        }

        throw new com.example.urlshortener.exception.ShortCodeCollisionException(
                "Unable to generate unique code after " + MAX_COLLISION_RETRIES + " attempts");
    }

    /**
     * Maps a ShortUrl entity to a UrlResponse DTO.
     */
    private UrlResponse toResponse(ShortUrl entity) {
        boolean expired = entity.getExpiresAt() != null
                && entity.getExpiresAt().isBefore(Instant.now());

        return new UrlResponse(
                entity.getCode(),
                entity.getTargetUrl(),
                BASE_URL + "/" + entity.getCode(),
                null, // customAlias - not stored as separate field in current entity
                entity.getHits(),
                entity.getCreatedAt(),
                entity.getExpiresAt(),
                expired
        );
    }
}
