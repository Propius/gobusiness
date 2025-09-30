package com.govtech.scrabble.controller.impl;

import com.govtech.scrabble.controller.LetterScoringController;
import com.govtech.scrabble.dto.LetterScoringResponse;
import com.govtech.scrabble.service.LetterScoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scrabble")
public class LetterScoringControllerImpl implements LetterScoringController {
    
    private static final Logger logger = LoggerFactory.getLogger(LetterScoringControllerImpl.class);
    
    private final LetterScoringService letterScoringService;
    
    public LetterScoringControllerImpl(LetterScoringService letterScoringService) {
        this.letterScoringService = letterScoringService;
    }
    
    @Override
    public ResponseEntity<LetterScoringResponse> getLetterScores() {
        try {
            logger.info("Request received for letter scoring legend");

            if (!letterScoringService.isLetterScoringEnabled()) {
                logger.info("Letter scoring display is disabled - returning graceful response");
                LetterScoringResponse response = new LetterScoringResponse();
                response.setEnabled(false);
                response.setLetterScores(java.util.Collections.emptyMap());
                response.setMessage("Letter scoring display is currently disabled");
                return ResponseEntity.ok(response);
            }

            LetterScoringResponse response = letterScoringService.getLetterScores();
            if (response != null) {
                response.setEnabled(true);
                logger.info("Letter scoring legend provided successfully with {} letters",
                           response.getLetterScores() != null ? response.getLetterScores().size() : 0);
                return ResponseEntity.ok(response);
            } else {
                throw new RuntimeException("Service returned null response");
            }

        } catch (Exception e) {
            logger.error("Error retrieving letter scores", e);
            LetterScoringResponse errorResponse = new LetterScoringResponse();
            errorResponse.setEnabled(false);
            errorResponse.setMessage("Failed to retrieve letter scores: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}