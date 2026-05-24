package com.example.urlshortener.url.repository;

import com.example.urlshortener.url.model.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for ShortUrl entities.
 * Provides query methods for looking up, checking existence, and listing shortened URLs.
 */
public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {

    /**
     * Find a ShortUrl by its short code.
     */
    Optional<ShortUrl> findByCode(String code);

    /**
     * Check if a short code already exists (collision check).
     */
    boolean existsByCode(String code);

    /**
     * Check if a target URL has already been shortened.
     */
    boolean existsByTargetUrl(String targetUrl);

    /**
     * List all URLs sorted by creation date, newest first.
     */
    List<ShortUrl> findAllByOrderByCreatedAtDesc();
}
