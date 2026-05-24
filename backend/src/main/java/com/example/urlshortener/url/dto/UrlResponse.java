package com.example.urlshortener.url.dto;

import java.time.Instant;

public record UrlResponse(
        String code,
        String originalUrl,
        String shortUrl,
        String customAlias,
        Long hitCount,
        Instant createdAt,
        Instant expiresAt,
        boolean expired
) {
}
