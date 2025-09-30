package com.govtech.scrabble.service;

import com.govtech.scrabble.dto.WordFinderRequest;
import com.govtech.scrabble.dto.WordFinderResponse;

/**
 * Interface for word finding operations
 * Provides functionality to find possible words from board and hand tiles
 */
public interface WordFinderService {
    
    /**
     * Find possible words that can be formed from board and hand tiles
     * @param request WordFinderRequest containing board tiles and hand tiles
     * @return WordFinderResponse containing possible words with scores and positions
     */
    WordFinderResponse findPossibleWords(WordFinderRequest request);
}