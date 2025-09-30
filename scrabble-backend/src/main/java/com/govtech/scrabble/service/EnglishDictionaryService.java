package com.govtech.scrabble.service;

import java.util.List;

/**
 * Interface for English dictionary operations
 * Provides word validation and word finding capabilities using a comprehensive English word list
 */
public interface EnglishDictionaryService {
    
    /**
     * Check if a word exists in the English dictionary
     * @param word The word to validate (case-insensitive)
     * @return true if the word is valid, false otherwise
     */
    boolean isValidWord(String word);
    
    /**
     * Find all possible words that can be formed from the given letters
     * @param availableLetters List of available letters
     * @param minLength Minimum word length to consider
     * @param maxLength Maximum word length to consider
     * @return List of valid words that can be formed, sorted by length (descending) then alphabetically
     */
    List<String> findPossibleWords(List<String> availableLetters, int minLength, int maxLength);
    
    /**
     * Get a random word from the dictionary within the specified length range
     * @param minLength Minimum word length
     * @param maxLength Maximum word length
     * @return A random valid English word
     */
    String getRandomWord(int minLength, int maxLength);
    
    /**
     * Get the total number of words in the dictionary
     * @return Total word count
     */
    int getWordCount();
    
    /**
     * Get all words with a specific length
     * @param length The desired word length
     * @return List of words with the specified length
     */
    List<String> getWordsByLength(int length);
}