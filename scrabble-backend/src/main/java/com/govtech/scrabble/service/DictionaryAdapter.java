package com.govtech.scrabble.service;

import com.govtech.scrabble.config.ScrabbleDictionaryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Adapter service that seamlessly switches between custom dictionary and LanguageTool.
 * When custom dictionary is enabled, it uses the lightweight ScrabbleDictionary.
 * When disabled, it falls back to the existing EnglishDictionaryService (LanguageTool).
 *
 * This allows for zero-downtime migration and A/B testing.
 */
@Service
@Primary
public class DictionaryAdapter implements EnglishDictionaryService {

    private static final Logger logger = LoggerFactory.getLogger(DictionaryAdapter.class);

    private final ScrabbleDictionary customDictionary;
    private final EnglishDictionaryService languageToolDictionary;
    private final ScrabbleDictionaryConfig config;

    public DictionaryAdapter(ScrabbleDictionary customDictionary,
                           EnglishDictionaryService languageToolDictionary,
                           ScrabbleDictionaryConfig config) {
        this.customDictionary = customDictionary;
        this.languageToolDictionary = languageToolDictionary;
        this.config = config;

        logger.info("Dictionary adapter initialized - Custom dictionary enabled: {}", config.isEnabled());
    }

    /**
     * Get the active dictionary service based on configuration.
     */
    private EnglishDictionaryService getActiveDictionary() {
        if (config.isEnabled() && customDictionary.isLoaded()) {
            return new CustomDictionaryWrapper();
        }
        logger.debug("Using fallback LanguageTool dictionary");
        return languageToolDictionary;
    }

    @Override
    public boolean isValidWord(String word) {
        if (config.isEnabled() && customDictionary.isLoaded()) {
            return customDictionary.isValidWord(word);
        }
        return languageToolDictionary.isValidWord(word);
    }

    @Override
    public List<String> findPossibleWords(List<String> availableLetters, int minLength, int maxLength) {
        return getActiveDictionary().findPossibleWords(availableLetters, minLength, maxLength);
    }

    @Override
    public String getRandomWord(int minLength, int maxLength) {
        return getActiveDictionary().getRandomWord(minLength, maxLength);
    }

    @Override
    public int getWordCount() {
        if (config.isEnabled() && customDictionary.isLoaded()) {
            return customDictionary.getWordCount();
        }
        return languageToolDictionary.getWordCount();
    }

    @Override
    public List<String> getWordsByLength(int length) {
        return getActiveDictionary().getWordsByLength(length);
    }

    /**
     * Inner wrapper class to adapt ScrabbleDictionary to EnglishDictionaryService interface.
     * This is only used when custom dictionary is active.
     */
    private class CustomDictionaryWrapper implements EnglishDictionaryService {

        @Override
        public boolean isValidWord(String word) {
            return customDictionary.isValidWord(word);
        }

        @Override
        public List<String> findPossibleWords(List<String> availableLetters, int minLength, int maxLength) {
            // Delegate to LanguageTool for complex word generation
            // Custom dictionary is optimized for validation, not generation
            logger.debug("Delegating findPossibleWords to LanguageTool (complex operation)");
            return languageToolDictionary.findPossibleWords(availableLetters, minLength, maxLength);
        }

        @Override
        public String getRandomWord(int minLength, int maxLength) {
            // Delegate to LanguageTool for random word generation
            logger.debug("Delegating getRandomWord to LanguageTool");
            return languageToolDictionary.getRandomWord(minLength, maxLength);
        }

        @Override
        public int getWordCount() {
            return customDictionary.getWordCount();
        }

        @Override
        public List<String> getWordsByLength(int length) {
            // Return words from custom dictionary
            return customDictionary.getWordsByLength(length).stream().toList();
        }
    }
}