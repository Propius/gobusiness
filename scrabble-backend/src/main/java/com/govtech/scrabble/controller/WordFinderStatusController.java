package com.govtech.scrabble.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Word Finder", description = "Word Finder Feature Status API")
public interface WordFinderStatusController {

    @GetMapping("/status")
    @Operation(summary = "Get word finder feature status", description = "Check if the word finder feature is enabled")
    @ApiResponse(responseCode = "200", description = "Feature status retrieved successfully")
    ResponseEntity<Map<String, Object>> getStatus();
}