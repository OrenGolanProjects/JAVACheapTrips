package com.orengolan.cheaptrips.config;


import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * The {@code RateLimitConfig} class provides configuration for rate limiting in the application.
 * It defines static constants for the maximum number of allowed requests per specified time intervals
 * (30 minutes and 1 hour) and offers a method to create a Bucket using Bucket4j library to enforce rate limits.
 *
 * Key Features:
 * - Configures rate limits for the application based on the specified time intervals.
 * - Utilizes Bucket4j library to create a token-bucket-based rate limiter.
 * - Allows customization of the number of requests allowed per time interval.
 * - Provides a central location for rate limit configuration, promoting consistency across the application.
 *
 * Example Usage:
 * The class is used to create a Bucket instance, which can be injected into controllers to perform rate limiting
 * on specific endpoints. It ensures that the application adheres to defined rate limits, preventing abuse and ensuring
 * fair usage of resources.
 */

@Configuration
public class RateLimitConfig {

    // Constants for rate limits
    public static final long REQUESTS_PER_30_MINUTES = 100;
    public static final long REQUESTS_PER_HOUR = 300;

    /**
     * Creates and returns a Bucket instance configured with rate limits for 30 minutes and 1 hour.
     *
     * @return Bucket instance with configured rate limits.
     */
    @Bean
    public Bucket createBucket() {
        Refill refill = Refill.intervally(REQUESTS_PER_30_MINUTES, Duration.ofMinutes(30));
        Bandwidth limit30Minutes = Bandwidth.classic(REQUESTS_PER_30_MINUTES, refill).withInitialTokens(REQUESTS_PER_30_MINUTES);

        refill = Refill.intervally(REQUESTS_PER_HOUR, Duration.ofHours(1));
        Bandwidth limit1Hour = Bandwidth.classic(REQUESTS_PER_HOUR, refill).withInitialTokens(REQUESTS_PER_HOUR);

        return Bucket.builder()
                .addLimit(limit30Minutes)
                .addLimit(limit1Hour)
                .build();
    }
}

