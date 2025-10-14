package com.govtech.scrabble.service;

import com.govtech.scrabble.config.ScrabbleDictionaryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Feature-aware dictionary adapter that routes validation requests to appropriate dictionary.
 *
 * Strategy:
 * - Score Calculator (ScrabbleService): Always uses LanguageTool (comprehensive validation)
 * - Word Scramble (WordScrambleService): Uses custom dictionary when enabled (performance optimized)
 * - Other features: Use LanguageTool by default
 *
 * This allows optimal dictionary selection per feature without global configuration.
 */
@Service
@Primary
public class FeatureAwareDictionaryAdapter implements EnglishDictionaryService {

    private static final Logger logger = LoggerFactory.getLogger(FeatureAwareDictionaryAdapter.class);

    // Thread-local to track which feature is calling
    private static final ThreadLocal<String> FEATURE_CONTEXT = new ThreadLocal<>();

    private final ScrabbleDictionary customDictionary;
    private final EnglishDictionaryService languageToolDictionary;
    private final ScrabbleDictionaryConfig config;

    public FeatureAwareDictionaryAdapter(ScrabbleDictionary customDictionary,
                                        @org.springframework.beans.factory.annotation.Qualifier("englishDictionaryServiceImpl") EnglishDictionaryService languageToolDictionary,
                                        ScrabbleDictionaryConfig config) {
        this.customDictionary = customDictionary;
        this.languageToolDictionary = languageToolDictionary;
        this.config = config;

        logger.info("Feature-aware dictionary adapter initialized");
        logger.info("  - Custom dictionary enabled: {}", config.isEnabled());
        logger.info("  - Custom dictionary loaded: {}", customDictionary.isLoaded());
        logger.info("  - Strategy: Score Calculator=LanguageTool, Word Scramble=Custom (if enabled)");
    }

    /**
     * Set the feature context for current thread.
     * Call this before dictionary operations to route correctly.
     */
    public static void setFeatureContext(String feature) {
        FEATURE_CONTEXT.set(feature);
        Logger log = LoggerFactory.getLogger(FeatureAwareDictionaryAdapter.class);
        log.debug("Dictionary feature context set to: {}", feature);
    }

    /**
     * Clear the feature context for current thread.
     */
    public static void clearFeatureContext() {
        FEATURE_CONTEXT.remove();
    }

    /**
     * Get current feature context.
     */
    private String getFeatureContext() {
        return FEATURE_CONTEXT.get();
    }

    /**
     * Determine which dictionary to use based on feature context.
     */
    private boolean shouldUseCustomDictionary() {
        if (!config.isEnabled() || !customDictionary.isLoaded()) {
            return false;
        }

        String feature = getFeatureContext();

        // Score Calculator always uses LanguageTool for comprehensive validation
        if ("SCORE_CALCULATOR".equals(feature)) {
            logger.debug("Score Calculator detected - using LanguageTool dictionary");
            return false;
        }

        // Word Scramble uses custom dictionary for performance
        if ("WORD_SCRAMBLE".equals(feature)) {
            logger.debug("Word Scramble detected - using custom dictionary");
            return true;
        }

        // Default: use LanguageTool for unknown features
        logger.debug("Unknown/default feature context '{}' - using LanguageTool", feature);
        return false;
    }

    @Override
    public boolean isValidWord(String word) {
        if (shouldUseCustomDictionary()) {
            return customDictionary.isValidWord(word);
        }
        return languageToolDictionary.isValidWord(word);
    }

    @Override
    public List<String> findPossibleWords(List<String> availableLetters, int minLength, int maxLength) {
        // Word generation always uses LanguageTool (more comprehensive)
        return languageToolDictionary.findPossibleWords(availableLetters, minLength, maxLength);
    }

    @Override
    public String getRandomWord(int minLength, int maxLength) {
        // Word Scramble uses custom dictionary for random words
        if (shouldUseCustomDictionary()) {
            // Collect all words within the length range
            java.util.List<String> words = new java.util.ArrayList<>();
            for (int length = minLength; length <= maxLength; length++) {
                words.addAll(customDictionary.getWordsByLength(length));
            }
            if (!words.isEmpty()) {
                int randomIndex = (int) (Math.random() * words.size());
                return words.get(randomIndex);
            }
        }
        // Fallback to LanguageTool
        return languageToolDictionary.getRandomWord(minLength, maxLength);
    }

    @Override
    public int getWordCount() {
        if (shouldUseCustomDictionary()) {
            return customDictionary.getWordCount();
        }
        return languageToolDictionary.getWordCount();
    }

    @Override
    public List<String> getWordsByLength(int length) {
        if (shouldUseCustomDictionary()) {
            return customDictionary.getWordsByLength(length).stream().toList();
        }
        return languageToolDictionary.getWordsByLength(length);
    }
}
