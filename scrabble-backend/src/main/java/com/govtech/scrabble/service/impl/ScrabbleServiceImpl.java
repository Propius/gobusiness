package com.govtech.scrabble.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.govtech.scrabble.config.ScrabbleProperties;
import com.govtech.scrabble.dto.CalculateScoreRequest;
import com.govtech.scrabble.dto.CalculateScoreResponse;
import com.govtech.scrabble.dto.ScoreRequest;
import com.govtech.scrabble.dto.ScoreResponse;
import com.govtech.scrabble.entity.Score;
import com.govtech.scrabble.repository.ScoreRepository;
import com.govtech.scrabble.service.EnglishDictionaryService;
import com.govtech.scrabble.service.ScrabbleService;
import com.govtech.scrabble.util.ScrabbleScoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScrabbleServiceImpl implements ScrabbleService {

    private static final Logger logger = LoggerFactory.getLogger(ScrabbleServiceImpl.class);

    private final ScoreRepository scoreRepository;
    private final EnglishDictionaryService englishDictionaryService;
    private final ScrabbleProperties scrabbleProperties;
    private final ObjectMapper objectMapper;

    public ScrabbleServiceImpl(ScoreRepository scoreRepository, EnglishDictionaryService englishDictionaryService,
                              ScrabbleProperties scrabbleProperties, ObjectMapper objectMapper) {
        this.scoreRepository = scoreRepository;
        this.englishDictionaryService = englishDictionaryService;
        this.scrabbleProperties = scrabbleProperties;
        this.objectMapper = objectMapper;
    }
    
    public CalculateScoreResponse calculateScore(String word) {
        if (word == null || word.trim().isEmpty()) {
            return new CalculateScoreResponse("", 0, true, null);
        }

        String normalizedWord = word.toUpperCase().trim();
        int totalScore = ScrabbleScoreUtil.calculateWordScore(normalizedWord);

        // Set context for feature-aware dictionary selection (Score Calculator uses LanguageTool)
        try {
            com.govtech.scrabble.service.FeatureAwareDictionaryAdapter.setFeatureContext("SCORE_CALCULATOR");
            boolean isValidWord = englishDictionaryService.isValidWord(normalizedWord);
            String validationMessage = isValidWord ? null : "Word not found in dictionary";

            return new CalculateScoreResponse(normalizedWord, totalScore, isValidWord, validationMessage);
        } finally {
            com.govtech.scrabble.service.FeatureAwareDictionaryAdapter.clearFeatureContext();
        }
    }
    
    @Override
    public CalculateScoreResponse calculateScoreWithSpecialTiles(CalculateScoreRequest request) {
        if (request == null || request.getWord() == null || request.getWord().trim().isEmpty()) {
            return new CalculateScoreResponse("", 0, 0, true, null, new ArrayList<>());
        }
        
        String normalizedWord = request.getWord().toUpperCase().trim();

        // Validate word with dictionary (set context for Score Calculator)
        boolean isValidWord;
        String validationMessage;
        try {
            com.govtech.scrabble.service.FeatureAwareDictionaryAdapter.setFeatureContext("SCORE_CALCULATOR");
            isValidWord = englishDictionaryService.isValidWord(normalizedWord);
            validationMessage = isValidWord ? null : "Word not found in dictionary";
        } finally {
            com.govtech.scrabble.service.FeatureAwareDictionaryAdapter.clearFeatureContext();
        }
        
        // Calculate base score (without special tiles)
        int baseScore = ScrabbleScoreUtil.calculateWordScore(normalizedWord);
        
        // Check if special tiles are enabled
        if (!scrabbleProperties.getSpecialTiles().getScoreCalculator().isEnabled() ||
            request.getPositions() == null || request.getSpecialTiles() == null) {
            // Return basic score if special tiles not enabled or not provided
            return new CalculateScoreResponse(normalizedWord, baseScore, baseScore, isValidWord, validationMessage, new ArrayList<>());
        }
        
        // Calculate enhanced score with special tiles
        int enhancedScore = ScrabbleScoreUtil.calculateWordScoreWithSpecialTiles(
            normalizedWord, request.getPositions(), request.getSpecialTiles());
        
        // Track bonuses applied
        List<String> bonusesApplied = calculateBonusesApplied(normalizedWord, request.getPositions(), request.getSpecialTiles());
        
        return new CalculateScoreResponse(normalizedWord, baseScore, enhancedScore, isValidWord, validationMessage, bonusesApplied);
    }
    
    private List<String> calculateBonusesApplied(String word, List<Integer> positions, List<String> specialTiles) {
        List<String> bonuses = new ArrayList<>();
        
        for (int i = 0; i < word.length(); i++) {
            if (i < positions.size()) {
                int position = positions.get(i);
                if (position >= 0 && position < specialTiles.size()) {
                    String specialTileType = specialTiles.get(position);
                    
                    switch (specialTileType) {
                        case "dl" -> bonuses.add("Double Letter at position " + position + " ('" + word.charAt(i) + "')");
                        case "tl" -> bonuses.add("Triple Letter at position " + position + " ('" + word.charAt(i) + "')");
                        case "dw" -> bonuses.add("Double Word at position " + position);
                        case "tw" -> bonuses.add("Triple Word at position " + position);
                        // "normal" case doesn't add any bonus message
                    }
                }
            }
        }
        
        return bonuses;
    }
    
    @Transactional
    public ScoreResponse saveScore(String word) {
        CalculateScoreResponse calculatedScore = calculateScore(word);

        if (calculatedScore.getTotalScore() == 0) {
            throw new IllegalArgumentException("Cannot save score for empty or invalid word");
        }

        if (calculatedScore.getIsValidWord() != null && !calculatedScore.getIsValidWord()) {
            throw new IllegalArgumentException("Cannot save score for invalid word: " + calculatedScore.getValidationMessage());
        }

        Score score = new Score(calculatedScore.getWord(), calculatedScore.getTotalScore());
        Score savedScore = scoreRepository.save(score);

        return new ScoreResponse(savedScore);
    }

    @Override
    @Transactional
    public ScoreResponse saveScoreWithEnhancement(ScoreRequest request) {
        String word = request.getWord();
        Integer enhancedScore = request.getEnhancedScore();

        if (word == null || word.trim().isEmpty()) {
            throw new IllegalArgumentException("Cannot save score for empty word");
        }

        if (enhancedScore == null || enhancedScore <= 0) {
            throw new IllegalArgumentException("Enhanced score must be positive");
        }

        String normalizedWord = word.toUpperCase().trim();

        // Validate word with dictionary
        boolean isValidWord = englishDictionaryService.isValidWord(normalizedWord);
        if (!isValidWord) {
            throw new IllegalArgumentException("Cannot save score for invalid word: Word not found in dictionary");
        }

        // Calculate base score for validation
        int baseScore = ScrabbleScoreUtil.calculateWordScore(normalizedWord);

        // Validate that enhanced score is not less than base score
        if (enhancedScore < baseScore) {
            throw new IllegalArgumentException("Enhanced score cannot be less than base score");
        }

        // Serialize positions and special tiles to JSON
        String positionsJson = null;
        String specialTilesJson = null;

        try {
            if (request.getPositions() != null && !request.getPositions().isEmpty()) {
                positionsJson = objectMapper.writeValueAsString(request.getPositions());
            }
            if (request.getSpecialTiles() != null && !request.getSpecialTiles().isEmpty()) {
                specialTilesJson = objectMapper.writeValueAsString(request.getSpecialTiles());
            }
        } catch (JsonProcessingException e) {
            logger.error("Error serializing special tiles data", e);
            throw new IllegalArgumentException("Error processing special tiles data");
        }

        // Create and save score entity with enhanced score
        Score score = new Score(normalizedWord, enhancedScore, enhancedScore, positionsJson, specialTilesJson);
        Score savedScore = scoreRepository.save(score);

        logger.info("Saved score with enhancement: word={}, baseScore={}, enhancedScore={}",
            normalizedWord, baseScore, enhancedScore);

        return new ScoreResponse(savedScore);
    }

    public List<ScoreResponse> getTopScores() {
        List<Score> topScores = scoreRepository.findTopScores(PageRequest.of(0, 10));
        return topScores.stream()
                .map(ScoreResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public CalculateScoreResponse validateWordOnly(String word) {
        if (word == null || word.trim().isEmpty()) {
            return new CalculateScoreResponse("", 0, false, "Word cannot be empty");
        }

        String normalizedWord = word.toUpperCase().trim();

        // Set context for feature-aware dictionary selection (Score Calculator uses LanguageTool)
        try {
            com.govtech.scrabble.service.FeatureAwareDictionaryAdapter.setFeatureContext("SCORE_CALCULATOR");
            boolean isValidWord = englishDictionaryService.isValidWord(normalizedWord);
            String validationMessage = isValidWord ? null : "Word not found in dictionary";

            // Return response with score 0, only validation matters here
            return new CalculateScoreResponse(normalizedWord, 0, isValidWord, validationMessage);
        } finally {
            com.govtech.scrabble.service.FeatureAwareDictionaryAdapter.clearFeatureContext();
        }
    }
}