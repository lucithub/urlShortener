package com.example.urlshortener.url.dto;

import jakarta.validation.constraints.*;

public record CreateUrlRequest(

        @NotBlank(message = "URL is required")
        @Size(max = 2048, message = "URL must not exceed 2048 characters")
        String url,

        @Pattern(
                regexp = "^[a-zA-Z0-9_-]{3,32}$",
                message = "Invalid alias: must be 3-32 alphanumeric characters, underscores, or hyphens"
        )
        String customAlias,

        @Min(value = 1, message = "Invalid expiration: must be between 1 and 365 days")
        @Max(value = 365, message = "Invalid expiration: must be between 1 and 365 days")
        Integer expirationDays
) {
}
