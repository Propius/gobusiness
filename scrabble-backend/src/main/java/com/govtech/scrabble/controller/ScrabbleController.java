package com.govtech.scrabble.controller;

import com.govtech.scrabble.dto.CalculateScoreRequest;
import com.govtech.scrabble.dto.CalculateScoreResponse;
import com.govtech.scrabble.dto.ScoreRequest;
import com.govtech.scrabble.dto.ScoreResponse;
import com.govtech.scrabble.dto.WordFinderRequest;
import com.govtech.scrabble.dto.WordFinderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Scrabble", description = "Scrabble Points Calculator API")
public interface ScrabbleController {
    
    @GetMapping("/calculate")
    @Operation(summary = "Calculate score for a word", description = "Calculate Scrabble points for a given word (basic calculation without special tiles)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Score calculated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    ResponseEntity<CalculateScoreResponse> calculateScore(
            @Parameter(description = "Word to calculate score for", required = true)
            @RequestParam String word,
            @Parameter(description = "Optional positions for special tiles calculation")
            @RequestParam(required = false) List<Integer> positions,
            @Parameter(description = "Optional special tiles types for calculation")
            @RequestParam(required = false) List<String> specialTiles);
    
    @PostMapping("/calculate")
    @Operation(summary = "Calculate score with special tiles", description = "Calculate Scrabble points for a word with optional special tile bonuses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Score calculated successfully with special tiles"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "503", description = "Special tiles feature is disabled")
    })
    ResponseEntity<CalculateScoreResponse> calculateScoreWithSpecialTiles(@Valid @RequestBody CalculateScoreRequest request);
    
    @PostMapping("/scores")
    @Operation(summary = "Save a score", description = "Save a Scrabble score to persistent storage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Score saved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    ResponseEntity<ScoreResponse> saveScore(@Valid @RequestBody ScoreRequest request);
    
    @GetMapping("/scores/top")
    @Operation(summary = "Get top scores", description = "Retrieve top 10 Scrabble scores")
    @ApiResponse(responseCode = "200", description = "Top scores retrieved successfully")
    ResponseEntity<List<ScoreResponse>> getTopScores();
    
    @PostMapping("/word-finder")
    @Operation(summary = "Find possible words", description = "Find possible words that can be formed using board tiles and hand tiles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Possible words found successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Feature not enabled")
    })
    ResponseEntity<WordFinderResponse> findPossibleWords(@Valid @RequestBody WordFinderRequest request);

    @PostMapping("/validate")
    @Operation(summary = "Validate a word", description = "Validate whether a word exists in the English dictionary")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Word validated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    ResponseEntity<CalculateScoreResponse> validateWord(@Valid @RequestBody CalculateScoreRequest request);
}