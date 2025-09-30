package com.govtech.scrabble.controller;

import com.govtech.scrabble.dto.BoardAnalyzerRequest;
import com.govtech.scrabble.dto.BoardAnalyzerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Board Analyzer", description = "Full board analysis for optimal word placement")
public interface BoardAnalyzerController {
    
    @PostMapping("/analyze")
    @Operation(summary = "Analyze board for top scoring combinations", 
               description = "Find the top 10 highest scoring word combinations that can be played on the current board")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Board analysis completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "503", description = "Board analyzer feature is disabled")
    })
    ResponseEntity<BoardAnalyzerResponse> analyzeBoard(
            @Parameter(description = "Board analysis request containing board state and hand tiles", required = true)
            @RequestBody BoardAnalyzerRequest request);
    
    @GetMapping("/status")
    @Operation(summary = "Get board analyzer status", 
               description = "Check if board analyzer feature is enabled and get configuration")
    @ApiResponse(responseCode = "200", description = "Status retrieved successfully")
    ResponseEntity<Map<String, Object>> getStatus();
}