package com.govtech.scrabble.controller.impl;

import com.govtech.scrabble.controller.BoardAnalyzerController;
import com.govtech.scrabble.dto.BoardAnalyzerRequest;
import com.govtech.scrabble.dto.BoardAnalyzerResponse;
import com.govtech.scrabble.service.BoardAnalyzerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/board-analyzer")
public class BoardAnalyzerControllerImpl implements BoardAnalyzerController {
    
    private final BoardAnalyzerService boardAnalyzerService;
    
    public BoardAnalyzerControllerImpl(BoardAnalyzerService boardAnalyzerService) {
        this.boardAnalyzerService = boardAnalyzerService;
    }
    
    @PostMapping("/analyze")
    public ResponseEntity<BoardAnalyzerResponse> analyzeBoard(@RequestBody BoardAnalyzerRequest request) {
        try {
            BoardAnalyzerResponse response = boardAnalyzerService.analyzeBoardForTopCombinations(request);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(503).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = Map.of(
            "enabled", boardAnalyzerService.isBoardAnalyzerEnabled(),
            "feature", "board-analyzer",
            "description", "Full board analysis for optimal word placement"
        );
        return ResponseEntity.ok(status);
    }
}