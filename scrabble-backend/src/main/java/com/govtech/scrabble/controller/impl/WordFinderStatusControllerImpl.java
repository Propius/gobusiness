package com.govtech.scrabble.controller.impl;

import com.govtech.scrabble.config.ScrabbleProperties;
import com.govtech.scrabble.controller.WordFinderStatusController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/word-finder")
public class WordFinderStatusControllerImpl implements WordFinderStatusController {

    private final ScrabbleProperties scrabbleProperties;
    
    public WordFinderStatusControllerImpl(ScrabbleProperties scrabbleProperties) {
        this.scrabbleProperties = scrabbleProperties;
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        return ResponseEntity.ok(Map.of(
                "featureEnabled", scrabbleProperties.getWordFinder().isEnabled(),
                "feature", "word-finder"
        ));
    }
}