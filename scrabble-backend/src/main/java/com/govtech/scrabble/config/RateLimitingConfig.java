package com.govtech.scrabble.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for rate limiting feature.
 *
 * This configuration allows rate limiting to be toggled on/off via properties or environment variables.
 * Useful for development, testing, and production environments.
 *
 * Example usage in application.yml:
 * <pre>
 * app:
 *   rate-limit:
 *     enabled: true
 *     requests-per-minute: 60
 *     burst-capacity: 10
 * </pre>
 *
 * Example usage with environment variables:
 * <pre>
 * APP_RATE_LIMIT_ENABLED=false
 * APP_RATE_LIMIT_REQUESTS_PER_MINUTE=120
 * APP_RATE_LIMIT_BURST_CAPACITY=20
 * </pre>
 */
@Configuration
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitingConfig {

    /**
     * Enable or disable rate limiting globally.
     * When disabled, all requests pass through without rate limiting.
     * Default: true
     */
    private boolean enabled = true;

    /**
     * Maximum number of requests allowed per minute per IP address.
     * Default: 60
     */
    private int requestsPerMinute = 60;

    /**
     * Maximum burst capacity for token bucket algorithm.
     * Allows short bursts of requests above the steady rate.
     * Default: 10
     */
    private int burstCapacity = 10;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public void setRequestsPerMinute(int requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
    }

    public int getBurstCapacity() {
        return burstCapacity;
    }

    public void setBurstCapacity(int burstCapacity) {
        this.burstCapacity = burstCapacity;
    }
}