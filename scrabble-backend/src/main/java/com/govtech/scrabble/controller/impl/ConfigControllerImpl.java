package com.govtech.scrabble.controller.impl;

import com.govtech.scrabble.config.ScrabbleProperties;
import com.govtech.scrabble.controller.ConfigController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/config")
public class ConfigControllerImpl implements ConfigController {

    private final ScrabbleProperties scrabbleProperties;
    
    public ConfigControllerImpl(ScrabbleProperties scrabbleProperties) {
        this.scrabbleProperties = scrabbleProperties;
    }

    @GetMapping("/tiles")
    public ResponseEntity<Map<String, Object>> getTileConfig() {
        Map<String, Object> config = new HashMap<>();
        
        config.put("scoreCalculator", Map.of(
            "tileCount", scrabbleProperties.getTiles().getScoreCalculator().getTileCount()
        ));
        
        config.put("wordFinder", Map.of(
            "boardTileCount", scrabbleProperties.getTiles().getWordFinder().getBoardTileCount(),
            "handTileCount", scrabbleProperties.getTiles().getWordFinder().getHandTileCount()
        ));
        
        config.put("boardAnalyzer", Map.of(
            "boardSize", scrabbleProperties.getTiles().getBoardAnalyzer().getBoardSize()
        ));
        
        return ResponseEntity.ok(config);
    }

    @GetMapping("/special-tiles")
    public ResponseEntity<Map<String, Object>> getSpecialTilesConfig() {
        Map<String, Object> config = new HashMap<>();
        
        config.put("scoreCalculator", Map.of(
            "enabled", scrabbleProperties.getSpecialTiles().getScoreCalculator().isEnabled()
        ));
        
        config.put("wordFinder", Map.of(
            "enabled", scrabbleProperties.getSpecialTiles().getWordFinder().isEnabled()
        ));
        
        config.put("boardAnalyzer", Map.of(
            "enabled", scrabbleProperties.getSpecialTiles().getBoardAnalyzer().isEnabled()
        ));
        
        return ResponseEntity.ok(config);
    }

    @GetMapping("/scramble")
    public ResponseEntity<Map<String, Object>> getScrambleConfig() {
        Map<String, Object> config = new HashMap<>();
        
        config.put("enabled", scrabbleProperties.getScramble().isEnabled());
        config.put("wordLength", Map.of(
            "min", scrabbleProperties.getScramble().getWordLength().getMin(),
            "max", scrabbleProperties.getScramble().getWordLength().getMax()
        ));
        
        Map<String, Map<String, Integer>> difficultyLevels = new HashMap<>();
        for (Map.Entry<String, ScrabbleProperties.DifficultyLevel> entry : 
             scrabbleProperties.getScramble().getDifficultyLevels().entrySet()) {
            
            ScrabbleProperties.DifficultyLevel level = entry.getValue();
            difficultyLevels.put(entry.getKey(), Map.of(
                "min", level.getMin(),
                "max", level.getMax()
            ));
        }
        config.put("difficultyLevels", difficultyLevels);
        
        return ResponseEntity.ok(config);
    }

    @GetMapping("/letter-scoring")
    public ResponseEntity<Map<String, Object>> getLetterScoringConfig() {
        Map<String, Object> config = new HashMap<>();
        
        config.put("scoreCalculator", Map.of(
            "enabled", scrabbleProperties.getLetterScoring().getScoreCalculator().isEnabled()
        ));
        
        config.put("wordFinder", Map.of(
            "enabled", scrabbleProperties.getLetterScoring().getWordFinder().isEnabled()
        ));
        
        config.put("boardAnalyzer", Map.of(
            "enabled", scrabbleProperties.getLetterScoring().getBoardAnalyzer().isEnabled()
        ));
        
        return ResponseEntity.ok(config);
    }
}