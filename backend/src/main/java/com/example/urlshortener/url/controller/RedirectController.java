package com.example.urlshortener.url.controller;

import com.example.urlshortener.url.model.ShortUrl;
import com.example.urlshortener.url.service.UrlService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for public URL redirects.
 * No class-level @RequestMapping — the redirect endpoint lives at ROOT level (/{code}).
 * Uses 302 FOUND (not 301) to preserve analytics — browsers don't cache 302,
 * so every redirect hits the server for hit tracking.
 */
@RestController
public class RedirectController {

    private final UrlService urlService;

    public RedirectController(UrlService urlService) {
        this.urlService = urlService;
    }

    /**
     * Redirects to the original URL for a given short code.
     *
     * @param code the short code or custom alias
     * @return 302 Found with Location header pointing to the original URL
     * @throws com.example.urlshortener.exception.UrlNotFoundException if code not found (404)
     * @throws com.example.urlshortener.exception.UrlExpiredException if URL has expired (410)
     */
    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        ShortUrl shortUrl = urlService.findByCodeAndActive(code);
        urlService.registerHit(shortUrl);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, shortUrl.getTargetUrl())
                .build();
    }
}
