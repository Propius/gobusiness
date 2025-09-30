package com.govtech.scrabble.service.impl;

import com.govtech.scrabble.config.ScrabbleProperties;
import com.govtech.scrabble.dto.LetterScoringResponse;
import com.govtech.scrabble.service.LetterScoringService;
import com.govtech.scrabble.util.ScrabbleScoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class LetterScoringServiceImpl implements LetterScoringService {

    private static final Logger logger = LoggerFactory.getLogger(LetterScoringServiceImpl.class);

    private final ScrabbleProperties scrabbleProperties;

    public LetterScoringServiceImpl(ScrabbleProperties scrabbleProperties) {
        this.scrabbleProperties = scrabbleProperties;
    }
    
    @Override
    public boolean isLetterScoringEnabled() {
        // Letter scoring legend should be available if ANY feature has it enabled
        return scrabbleProperties.getLetterScoring().getScoreCalculator().isEnabled() ||
               scrabbleProperties.getLetterScoring().getWordFinder().isEnabled() ||
               scrabbleProperties.getLetterScoring().getBoardAnalyzer().isEnabled();
    }
    
    @Override
    public LetterScoringResponse getLetterScores() {
        if (!isLetterScoringEnabled()) {
            return new LetterScoringResponse(Map.of(), "Letter scoring display is disabled");
        }

        logger.info("Providing letter scoring legend");

        // Get letter scores from centralized utility
        Map<Character, Integer> allScores = ScrabbleScoreUtil.getAllLetterScores();

        // Convert to String keys for JSON response
        Map<String, Integer> letterScoresMap = new LinkedHashMap<>();
        for (Map.Entry<Character, Integer> entry : allScores.entrySet()) {
            letterScoresMap.put(entry.getKey().toString(), entry.getValue());
        }

        return new LetterScoringResponse(letterScoresMap, "Standard Scrabble letter scores");
    }

    @Override
    public int getLetterScore(char letter) {
        // Delegate to centralized utility for single source of truth
        return ScrabbleScoreUtil.getLetterScore(letter);
    }
}