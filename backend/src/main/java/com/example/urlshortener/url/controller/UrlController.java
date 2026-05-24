package com.example.urlshortener.url.controller;

import com.example.urlshortener.url.dto.CreateUrlRequest;
import com.example.urlshortener.url.dto.UrlResponse;
import com.example.urlshortener.url.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for URL shortening CRUD operations.
 * Base path: /api/v1/urls
 */
@RestController
@RequestMapping("/api/v1/urls")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    /**
     * Creates a new shortened URL.
     *
     * @param request the URL creation request with validation
     * @return the created URL response with generated code
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UrlResponse createUrl(@Valid @RequestBody CreateUrlRequest request) {
        return urlService.createUrl(request);
    }

    /**
     * Returns all shortened URLs sorted by creation date (newest first).
     *
     * @return list of all URL responses
     */
    @GetMapping
    public List<UrlResponse> getAllUrls() {
        return urlService.getAllUrls();
    }

    /**
     * Retrieves details of a shortened URL by its code.
     *
     * @param code the short code or custom alias
     * @return the URL response
     */
    @GetMapping("/{code}")
    public UrlResponse getUrlByCode(@PathVariable String code) {
        return urlService.getUrlByCode(code);
    }

    /**
     * Deletes a shortened URL by its code.
     *
     * @param code the short code or custom alias to delete
     */
    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUrl(@PathVariable String code) {
        urlService.deleteUrl(code);
    }
}
