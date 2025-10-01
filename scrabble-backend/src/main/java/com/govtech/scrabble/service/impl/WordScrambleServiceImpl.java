package com.govtech.scrabble.service.impl;

import com.govtech.scrabble.config.ScrabbleProperties;
import com.govtech.scrabble.dto.ScrambleCheckResponse;
import com.govtech.scrabble.dto.ScrambleResponse;
import com.govtech.scrabble.service.EnglishDictionaryService;
import com.govtech.scrabble.service.ScrabbleDictionary;
import com.govtech.scrabble.service.ScrabbleService;
import com.govtech.scrabble.service.WordScrambleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WordScrambleServiceImpl implements WordScrambleService {

    private static final Logger logger = LoggerFactory.getLogger(WordScrambleServiceImpl.class);
    private static final Map<String, String> CATEGORY_HINTS = Map.of(
        "BUSINESS", "Related to work and commerce",
        "COMPUTER", "Electronic device for processing data",
        "SCIENCE", "Study of the natural world",
        "FAMILY", "People related by blood or marriage"
    );

    private final ScrabbleProperties scrabbleProperties;
    private final ScrabbleDictionary scrabbleDictionary;
    private final EnglishDictionaryService englishDictionaryService;
    private final ScrabbleService scrabbleService;

    public WordScrambleServiceImpl(ScrabbleProperties scrabbleProperties,
                                 ScrabbleDictionary scrabbleDictionary,
                                 EnglishDictionaryService englishDictionaryService,
                                 ScrabbleService scrabbleService) {
        this.scrabbleProperties = scrabbleProperties;
        this.scrabbleDictionary = scrabbleDictionary;
        this.englishDictionaryService = englishDictionaryService;
        this.scrabbleService = scrabbleService;
    }
    
    private final Random random = new Random();
    private final Map<String, String> activeScrambles = new ConcurrentHashMap<>();
    
    public boolean isScrambleEnabled() {
        return scrabbleProperties.getScramble().isEnabled();
    }
    
    public ScrambleResponse generateScramble() {
        return generateScramble(null);
    }
    
    public ScrambleResponse generateScramble(String difficulty) {
        if (!isScrambleEnabled()) {
            throw new IllegalStateException("Word scramble feature is disabled");
        }
        
        int minLength = scrabbleProperties.getScramble().getWordLength().getMin();
        int maxLength = scrabbleProperties.getScramble().getWordLength().getMax();
        
        if (difficulty != null) {
            ScrabbleProperties.DifficultyLevel difficultyLevel = 
                scrabbleProperties.getScramble().getDifficultyLevels().get(difficulty.toLowerCase());
            
            if (difficultyLevel != null) {
                minLength = Math.max(difficultyLevel.getMin(), scrabbleProperties.getScramble().getWordLength().getMin());
                maxLength = Math.min(difficultyLevel.getMax(), scrabbleProperties.getScramble().getWordLength().getMax());
            }
        }
        
        // Use custom ScrabbleDictionary if loaded, otherwise fall back to EnglishDictionaryService
        String originalWord;
        try {
            com.govtech.scrabble.service.FeatureAwareDictionaryAdapter.setFeatureContext("WORD_SCRAMBLE");

            if (scrabbleDictionary.isLoaded()) {
                logger.debug("Using ScrabbleDictionary for word scramble");
                originalWord = getRandomWordFromScrabbleDictionary(minLength, maxLength);
            } else {
                logger.debug("Falling back to EnglishDictionaryService for word scramble");
                originalWord = englishDictionaryService.getRandomWord(minLength, maxLength);
            }
        } finally {
            com.govtech.scrabble.service.FeatureAwareDictionaryAdapter.clearFeatureContext();
        }

        String scrambledWord = scrambleWord(originalWord);

        while (scrambledWord.equals(originalWord)) {
            scrambledWord = scrambleWord(originalWord);
        }
        
        String sessionId = generateSessionId();
        activeScrambles.put(sessionId, originalWord);
        
        List<Character> availableLetters = new ArrayList<>();
        for (char c : originalWord.toCharArray()) {
            availableLetters.add(c);
        }
        
        String hint = generateHint(originalWord);
        String difficultyLevel = determineDifficulty(originalWord.length());
        
        logger.debug("Generated scramble for word '{}': '{}'", originalWord, scrambledWord);
        
        ScrambleResponse response = new ScrambleResponse(
            scrambledWord,
            null, // Don't expose the original word to client
            availableLetters,
            originalWord.length(),
            difficultyLevel,
            hint
        );
        
        response.setOriginalWord(sessionId); // Use session ID instead of actual word
        
        return response;
    }
    
    public ScrambleResponse reshuffleScramble(String sessionId) {
        if (!isScrambleEnabled()) {
            throw new IllegalStateException("Word scramble feature is disabled");
        }
        
        String originalWord = activeScrambles.get(sessionId);
        if (originalWord == null) {
            throw new IllegalArgumentException("Invalid or expired scramble session");
        }
        
        String newScrambledWord = scrambleWord(originalWord);
        while (newScrambledWord.equals(originalWord)) {
            newScrambledWord = scrambleWord(originalWord);
        }
        
        List<Character> availableLetters = new ArrayList<>();
        for (char c : originalWord.toCharArray()) {
            availableLetters.add(c);
        }
        
        String hint = generateHint(originalWord);
        String difficultyLevel = determineDifficulty(originalWord.length());
        
        logger.debug("Reshuffled scramble for session '{}': '{}'", sessionId, newScrambledWord);
        
        return new ScrambleResponse(
            newScrambledWord,
            sessionId,
            availableLetters,
            originalWord.length(),
            difficultyLevel,
            hint
        );
    }
    
    public ScrambleCheckResponse checkAnswer(String sessionId, String userAnswer) {
        if (!isScrambleEnabled()) {
            throw new IllegalStateException("Word scramble feature is disabled");
        }
        
        String originalWord = activeScrambles.get(sessionId);
        if (originalWord == null) {
            return new ScrambleCheckResponse(false, userAnswer, null, 0, "Invalid or expired scramble session");
        }
        
        String normalizedAnswer = userAnswer != null ? userAnswer.toUpperCase().trim() : "";
        boolean isCorrect = originalWord.equals(normalizedAnswer);
        
        int score = 0;
        String message;
        
        if (isCorrect) {
            score = scrabbleService.calculateScore(originalWord).getTotalScore();
            message = "Congratulations! You unscrambled the word correctly!";
            activeScrambles.remove(sessionId);
            logger.debug("User correctly unscrambled word '{}' with score {}", originalWord, score);
        } else {
            message = "Incorrect! Try again or get a new scramble.";
            logger.debug("User provided incorrect answer '{}' for word '{}'", normalizedAnswer, originalWord);
        }
        
        return new ScrambleCheckResponse(isCorrect, normalizedAnswer, originalWord, score, message);
    }
    
    public void clearSession(String sessionId) {
        activeScrambles.remove(sessionId);
    }
    
    public int getActiveSessionsCount() {
        return activeScrambles.size();
    }
    
    private String scrambleWord(String word) {
        List<Character> chars = new ArrayList<>();
        for (char c : word.toCharArray()) {
            chars.add(c);
        }
        Collections.shuffle(chars, random);
        
        StringBuilder scrambled = new StringBuilder();
        for (char c : chars) {
            scrambled.append(c);
        }
        
        return scrambled.toString();
    }
    
    private String generateHint(String word) {
        if (CATEGORY_HINTS.containsKey(word)) {
            return CATEGORY_HINTS.get(word);
        }
        
        return switch (word.length()) {
            case 4 -> "A common 4-letter word";
            case 5 -> "A common 5-letter word";
            case 6 -> "A common 6-letter word";
            case 7 -> "A common 7-letter word";
            case 8 -> "A common 8-letter word";
            default -> "A common English word";
        };
    }
    
    private String determineDifficulty(int length) {
        // Check configured difficulty levels to determine which one this word length fits
        for (Map.Entry<String, ScrabbleProperties.DifficultyLevel> entry : 
             scrabbleProperties.getScramble().getDifficultyLevels().entrySet()) {
            
            ScrabbleProperties.DifficultyLevel level = entry.getValue();
            if (length >= level.getMin() && length <= level.getMax()) {
                return entry.getKey();
            }
        }
        
        // Fallback to medium if no configured difficulty matches
        return "medium";
    }
    
    private String generateSessionId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Get a random word from the custom Scrabble dictionary within the specified length range.
     * @param minLength minimum word length
     * @param maxLength maximum word length
     * @return random word from the dictionary
     */
    private String getRandomWordFromScrabbleDictionary(int minLength, int maxLength) {
        List<String> eligibleWords = new ArrayList<>();

        // Collect all words within the length range
        for (int len = minLength; len <= maxLength; len++) {
            Set<String> wordsOfLength = scrabbleDictionary.getWordsByLength(len);
            eligibleWords.addAll(wordsOfLength);
        }

        if (eligibleWords.isEmpty()) {
            logger.warn("No words found in custom dictionary for length range {}-{}, falling back to EnglishDictionaryService",
                minLength, maxLength);
            return englishDictionaryService.getRandomWord(minLength, maxLength);
        }

        // Select a random word
        int randomIndex = random.nextInt(eligibleWords.size());
        String word = eligibleWords.get(randomIndex).toUpperCase();

        logger.debug("Selected random word '{}' from custom dictionary (length: {})", word, word.length());
        return word;
    }
}