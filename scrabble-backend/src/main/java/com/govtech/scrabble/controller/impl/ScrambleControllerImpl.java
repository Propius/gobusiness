package com.govtech.scrabble.controller.impl;

import com.govtech.scrabble.controller.ScrambleController;
import com.govtech.scrabble.dto.ScrambleCheckResponse;
import com.govtech.scrabble.dto.ScrambleResponse;
import com.govtech.scrabble.service.WordScrambleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/scramble")
public class ScrambleControllerImpl implements ScrambleController {
    
    private final WordScrambleService wordScrambleService;
    
    public ScrambleControllerImpl(WordScrambleService wordScrambleService) {
        this.wordScrambleService = wordScrambleService;
    }
    
    @GetMapping("/new")
    public ResponseEntity<ScrambleResponse> generateScramble(@RequestParam(required = false) String difficulty) {
        try {
            ScrambleResponse response = wordScrambleService.generateScramble(difficulty);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(503).build();
        }
    }
    
    @PostMapping("/{sessionId}/reshuffle")
    public ResponseEntity<ScrambleResponse> reshuffleScramble(@PathVariable String sessionId) {
        try {
            ScrambleResponse response = wordScrambleService.reshuffleScramble(sessionId);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(503).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{sessionId}/check")
    public ResponseEntity<ScrambleCheckResponse> checkAnswer(@PathVariable String sessionId, @RequestBody Map<String, String> request) {
        try {
            String userAnswer = request.get("answer");
            if (userAnswer == null) {
                return ResponseEntity.badRequest().build();
            }
            
            ScrambleCheckResponse response = wordScrambleService.checkAnswer(sessionId, userAnswer);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(503).build();
        }
    }
    
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> clearSession(@PathVariable String sessionId) {
        wordScrambleService.clearSession(sessionId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = Map.of(
            "activeSessionsCount", wordScrambleService.getActiveSessionsCount(),
            "featureEnabled", wordScrambleService.isScrambleEnabled()
        );
        return ResponseEntity.ok(stats);
    }
}