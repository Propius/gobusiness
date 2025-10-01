package com.govtech.scrabble.controller.impl;

import com.govtech.scrabble.controller.ScrabbleController;
import com.govtech.scrabble.dto.CalculateScoreRequest;
import com.govtech.scrabble.dto.CalculateScoreResponse;
import com.govtech.scrabble.dto.ScoreRequest;
import com.govtech.scrabble.dto.ScoreResponse;
import com.govtech.scrabble.dto.WordFinderRequest;
import com.govtech.scrabble.dto.WordFinderResponse;
import com.govtech.scrabble.service.ScrabbleService;
import com.govtech.scrabble.service.WordFinderService;
import com.govtech.scrabble.service.InputValidationService;
import com.govtech.scrabble.config.ScrabbleProperties;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scrabble")
public class ScrabbleControllerImpl implements ScrabbleController {
    
    private final ScrabbleService scrabbleService;
    private final WordFinderService wordFinderService;
    private final InputValidationService inputValidationService;
    private final ScrabbleProperties scrabbleProperties;
    
    public ScrabbleControllerImpl(ScrabbleService scrabbleService, WordFinderService wordFinderService, 
                                 InputValidationService inputValidationService, ScrabbleProperties scrabbleProperties) {
        this.scrabbleService = scrabbleService;
        this.wordFinderService = wordFinderService;
        this.inputValidationService = inputValidationService;
        this.scrabbleProperties = scrabbleProperties;
    }
    
    public ResponseEntity<CalculateScoreResponse> calculateScore(@RequestParam String word,
                                                                @RequestParam(required = false) List<Integer> positions,
                                                                @RequestParam(required = false) List<String> specialTiles) {
        // Validate input parameters
        inputValidationService.validateWord(word);
        inputValidationService.validatePositions(positions);
        inputValidationService.validateSpecialTiles(specialTiles);
        inputValidationService.validatePositionsAndTilesMatch(positions, specialTiles);
        
        // If special tiles data is provided and feature is enabled, use enhanced calculation
        if (positions != null && specialTiles != null && 
            scrabbleProperties.getSpecialTiles().getScoreCalculator().isEnabled()) {
            
            CalculateScoreRequest request = new CalculateScoreRequest();
            request.setWord(word.trim().toUpperCase());
            request.setPositions(positions);
            request.setSpecialTiles(specialTiles);
            
            CalculateScoreResponse response = scrabbleService.calculateScoreWithSpecialTiles(request);
            return ResponseEntity.ok(response);
        }
        
        // Otherwise use basic calculation
        CalculateScoreResponse response = scrabbleService.calculateScore(word.trim().toUpperCase());
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<CalculateScoreResponse> calculateScoreWithSpecialTiles(@Valid @RequestBody CalculateScoreRequest request) {
        if (!scrabbleProperties.getSpecialTiles().getScoreCalculator().isEnabled()) {
            throw new IllegalStateException("Special tiles feature is disabled");
        }
        
        CalculateScoreResponse response = scrabbleService.calculateScoreWithSpecialTiles(request);
        return ResponseEntity.ok(response);
    }
    
    public ResponseEntity<ScoreResponse> saveScore(@Valid @RequestBody ScoreRequest request) {
        // Check if enhanced score is provided (new flow with special tiles)
        if (request.getEnhancedScore() != null) {
            ScoreResponse response = scrabbleService.saveScoreWithEnhancement(request);
            return ResponseEntity.ok(response);
        }

        // Fallback to legacy flow (basic score without special tiles)
        ScoreResponse response = scrabbleService.saveScore(request.getWord());
        return ResponseEntity.ok(response);
    }
    
    public ResponseEntity<List<ScoreResponse>> getTopScores() {
        List<ScoreResponse> topScores = scrabbleService.getTopScores();
        return ResponseEntity.ok(topScores);
    }
    
    public ResponseEntity<WordFinderResponse> findPossibleWords(@Valid @RequestBody WordFinderRequest request) {
        if (!scrabbleProperties.getWordFinder().isEnabled()) {
            throw new IllegalStateException("Word finder feature is disabled");
        }

        WordFinderResponse response = wordFinderService.findPossibleWords(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CalculateScoreResponse> validateWord(@Valid @RequestBody CalculateScoreRequest request) {
        inputValidationService.validateWord(request.getWord());

        String normalizedWord = request.getWord().trim().toUpperCase();
        CalculateScoreResponse response = scrabbleService.validateWordOnly(normalizedWord);
        return ResponseEntity.ok(response);
    }
}