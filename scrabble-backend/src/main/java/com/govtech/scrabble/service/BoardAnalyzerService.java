package com.govtech.scrabble.service;

import com.govtech.scrabble.dto.BoardAnalyzerRequest;
import com.govtech.scrabble.dto.BoardAnalyzerResponse;

public interface BoardAnalyzerService {
    
    boolean isBoardAnalyzerEnabled();
    
    BoardAnalyzerResponse analyzeBoardForTopCombinations(BoardAnalyzerRequest request);
}