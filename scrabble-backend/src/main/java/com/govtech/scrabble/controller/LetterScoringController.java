package com.govtech.scrabble.controller;

import com.govtech.scrabble.dto.LetterScoringResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "Letter Scoring", description = "API for Scrabble letter scoring display")
public interface LetterScoringController {
    
    @Operation(summary = "Get letter scoring legend",
               description = "Retrieves the standard Scrabble letter-to-score mapping for display")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Letter scores retrieved successfully"),
        @ApiResponse(responseCode = "503", description = "Letter scoring display is disabled")
    })
    @GetMapping("/letter-scores")
    ResponseEntity<LetterScoringResponse> getLetterScores();
}