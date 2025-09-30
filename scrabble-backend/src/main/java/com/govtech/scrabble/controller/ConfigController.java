package com.govtech.scrabble.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Configuration", description = "Application Configuration API")
public interface ConfigController {

    @GetMapping("/tiles")
    @Operation(summary = "Get tile configuration", description = "Get tile count configuration for different features")
    @ApiResponse(responseCode = "200", description = "Configuration retrieved successfully")
    ResponseEntity<Map<String, Object>> getTileConfig();

    @GetMapping("/special-tiles")
    @Operation(summary = "Get special tiles feature status", description = "Check if special tiles feature is enabled for different components")
    @ApiResponse(responseCode = "200", description = "Feature status retrieved successfully")
    ResponseEntity<Map<String, Object>> getSpecialTilesConfig();

    @GetMapping("/scramble")
    @Operation(summary = "Get scramble configuration", description = "Get word scramble difficulty levels and settings")
    @ApiResponse(responseCode = "200", description = "Configuration retrieved successfully")
    ResponseEntity<Map<String, Object>> getScrambleConfig();

    @GetMapping("/letter-scoring")
    @Operation(summary = "Get letter scoring display configuration", description = "Check if letter scoring display is enabled for different components")
    @ApiResponse(responseCode = "200", description = "Configuration retrieved successfully")
    ResponseEntity<Map<String, Object>> getLetterScoringConfig();
}