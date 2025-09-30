package com.govtech.scrabble.service;

import com.govtech.scrabble.dto.LetterScoringResponse;

public interface LetterScoringService {
    LetterScoringResponse getLetterScores();
    int getLetterScore(char letter);
    boolean isLetterScoringEnabled();
}