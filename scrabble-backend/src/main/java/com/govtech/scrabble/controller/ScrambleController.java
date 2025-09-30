package com.govtech.scrabble.controller;

import com.govtech.scrabble.dto.ScrambleCheckResponse;
import com.govtech.scrabble.dto.ScrambleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Word Scramble", description = "Word scramble game API")
public interface ScrambleController {
    
    @GetMapping("/new")
    @Operation(summary = "Generate new scrambled word", 
               description = "Generate a new word scramble puzzle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Scramble generated successfully"),
            @ApiResponse(responseCode = "503", description = "Scramble feature is disabled")
    })
    ResponseEntity<ScrambleResponse> generateScramble(
            @Parameter(description = "Difficulty level (easy, medium, hard)", required = false)
            @RequestParam(required = false) String difficulty);
    
    @PostMapping("/{sessionId}/reshuffle")
    @Operation(summary = "Reshuffle scrambled letters", 
               description = "Reshuffle the letters of an existing scramble")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Letters reshuffled successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid session ID"),
            @ApiResponse(responseCode = "503", description = "Scramble feature is disabled")
    })
    ResponseEntity<ScrambleResponse> reshuffleScramble(
            @Parameter(description = "Scramble session ID", required = true)
            @PathVariable String sessionId);
    
    @PostMapping("/{sessionId}/check")
    @Operation(summary = "Check scramble answer", 
               description = "Check if the user's answer is correct")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Answer checked successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "503", description = "Scramble feature is disabled")
    })
    ResponseEntity<ScrambleCheckResponse> checkAnswer(
            @Parameter(description = "Scramble session ID", required = true)
            @PathVariable String sessionId,
            @Parameter(description = "User's answer", required = true)
            @RequestBody Map<String, String> request);
    
    @DeleteMapping("/{sessionId}")
    @Operation(summary = "Clear scramble session", 
               description = "Clear an active scramble session")
    @ApiResponse(responseCode = "200", description = "Session cleared successfully")
    ResponseEntity<Void> clearSession(
            @Parameter(description = "Scramble session ID", required = true)
            @PathVariable String sessionId);
    
    @GetMapping("/stats")
    @Operation(summary = "Get scramble statistics", 
               description = "Get statistics about active scramble sessions")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    ResponseEntity<Map<String, Object>> getStats();
}