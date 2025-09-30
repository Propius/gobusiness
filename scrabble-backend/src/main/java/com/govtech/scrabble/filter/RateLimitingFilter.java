package com.govtech.scrabble.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.govtech.scrabble.config.RateLimitingConfig;
import com.govtech.scrabble.exception.GlobalExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Rate limiting filter using token bucket algorithm.
 * Limits the number of requests per IP address within a time window.
 *
 * Rate limiting can be disabled via configuration for development/testing:
 * - Set app.rate-limit.enabled=false in application.yml, or
 * - Set environment variable APP_RATE_LIMIT_ENABLED=false
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

    // Cache for storing rate limit data per IP
    private final ConcurrentHashMap<String, RateLimitData> rateLimitCache = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private final RateLimitingConfig config;

    public RateLimitingFilter(ObjectMapper objectMapper, RateLimitingConfig config) {
        this.objectMapper = objectMapper;
        this.config = config;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Skip rate limiting if disabled or for health checks
        if (!config.isEnabled() || isExcludedPath(request.getRequestURI())) {
            if (!config.isEnabled()) {
                logger.debug("Rate limiting is DISABLED - allowing all requests");
            }
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIpAddress(request);

        if (!isAllowed(clientIp)) {
            handleRateLimitExceeded(response, clientIp);
            return;
        }

        // Add rate limiting headers
        addRateLimitHeaders(response, clientIp);

        filterChain.doFilter(request, response);
    }

    private boolean isAllowed(String clientIp) {
        RateLimitData rateLimitData = rateLimitCache.computeIfAbsent(clientIp,
            k -> new RateLimitData(config.getRequestsPerMinute(), config.getBurstCapacity()));

        return rateLimitData.tryConsume();
    }

    private void addRateLimitHeaders(HttpServletResponse response, String clientIp) {
        RateLimitData rateLimitData = rateLimitCache.get(clientIp);
        if (rateLimitData != null) {
            response.setHeader("X-RateLimit-Limit", String.valueOf(config.getRequestsPerMinute()));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(rateLimitData.getAvailableTokens()));
            response.setHeader("X-RateLimit-Reset", String.valueOf(rateLimitData.getResetTime()));
        }
    }

    private void handleRateLimitExceeded(HttpServletResponse response, String clientIp) throws IOException {
        logger.warn("Rate limit exceeded for IP: {}", sanitizeIp(clientIp));
        
        GlobalExceptionHandler.ErrorResponse errorResponse = new GlobalExceptionHandler.ErrorResponse(
            "RATE_LIMIT_EXCEEDED",
            "Too many requests. Please try again later.",
            null,
            HttpStatus.TOO_MANY_REQUESTS.value()
        );

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Retry-After", "60");
        
        addRateLimitHeaders(response, clientIp);
        
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    private String getClientIpAddress(HttpServletRequest request) {
        // Check various headers for real IP (in case of proxy/load balancer)
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    private boolean isExcludedPath(String path) {
        return path.startsWith("/actuator/health") || 
               path.startsWith("/actuator/info") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs");
    }

    /**
     * Sanitizes IP address to prevent log injection attacks.
     * Removes all characters except digits, dots, and colons (for IPv6).
     * Limits length to 45 characters (max IPv6 length).
     *
     * @param ip The IP address to sanitize
     * @return Sanitized IP address string, or "unknown" if null
     */
    private String sanitizeIp(String ip) {
        if (ip == null) return "unknown";
        // Sanitize IP by removing non-IP characters and limit length
        String sanitized = ip.replaceAll("[^0-9.:]", "");
        return sanitized.substring(0, Math.min(sanitized.length(), 45));
    }

    /**
     * Token bucket implementation for rate limiting
     */
    private static class RateLimitData {
        private final int capacity;
        private final int refillRate;
        private final AtomicInteger tokens;
        private final AtomicLong lastRefill;

        public RateLimitData(int refillRate, int capacity) {
            this.capacity = capacity;
            this.refillRate = refillRate;
            this.tokens = new AtomicInteger(capacity);
            this.lastRefill = new AtomicLong(System.currentTimeMillis());
        }

        public synchronized boolean tryConsume() {
            refillTokens();
            
            if (tokens.get() > 0) {
                tokens.decrementAndGet();
                return true;
            }
            
            return false;
        }

        private void refillTokens() {
            long now = System.currentTimeMillis();
            long timePassed = now - lastRefill.get();
            
            if (timePassed >= 60000) { // 1 minute
                int tokensToAdd = (int) (timePassed / 60000) * refillRate;
                int newTokenCount = Math.min(capacity, tokens.get() + tokensToAdd);
                tokens.set(newTokenCount);
                lastRefill.set(now);
            }
        }

        public int getAvailableTokens() {
            refillTokens();
            return Math.max(0, tokens.get());
        }

        public long getResetTime() {
            return (lastRefill.get() + 60000) / 1000; // Next refill time in seconds
        }
    }
}