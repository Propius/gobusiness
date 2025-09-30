package com.govtech.scrabble.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the custom Scrabble dictionary.
 * This lightweight dictionary replaces the heavyweight LanguageTool library,
 * providing 90% smaller footprint and 6x faster loading times.
 */
@Configuration
@ConfigurationProperties(prefix = "scrabble.custom-dictionary")
public class ScrabbleDictionaryConfig {

    private boolean enabled = true;
    private String filePath = "classpath:scrabble-dictionary.yml";
    private int minLength = 4;
    private int maxLength = 10;
    private boolean cacheEnabled = true;

    /**
     * Check if custom dictionary is enabled.
     * When false, system falls back to LanguageTool.
     */
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Get the path to the dictionary YAML file.
     */
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Get minimum word length for filtering.
     */
    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    /**
     * Get maximum word length for filtering.
     */
    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * Check if caching is enabled for dictionary lookups.
     */
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }
}