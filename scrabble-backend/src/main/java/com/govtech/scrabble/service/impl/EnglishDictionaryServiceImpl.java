package com.govtech.scrabble.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.govtech.scrabble.config.ScrabbleProperties;
import com.govtech.scrabble.service.EnglishDictionaryService;
import com.govtech.scrabble.util.ScrabbleScoreUtil;
import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Optimized implementation of English dictionary service using LanguageTool with caching.
 * This provides high performance while maintaining accuracy and avoiding hardcoded word lists.
 * Note: This is now a fallback service. DictionaryAdapter (marked @Primary) will use
 * ScrabbleDictionary when enabled, falling back to this service when disabled.
 */
@Service
public class EnglishDictionaryServiceImpl implements EnglishDictionaryService {
    
    private static final Logger logger = LoggerFactory.getLogger(EnglishDictionaryServiceImpl.class);
    
    private final JLanguageTool langTool;
    private final Cache<String, Boolean> validationCache;
    private final Cache<String, List<String>> wordGenerationCache;
    private final Random random = new Random();
    private final ScrabbleProperties scrabbleProperties;
    
    
    public EnglishDictionaryServiceImpl(ScrabbleProperties scrabbleProperties) {
        this.scrabbleProperties = scrabbleProperties;
        try {
            this.langTool = new JLanguageTool(new AmericanEnglish());
            
            // Disable expensive rules we don't need for simple word validation
            langTool.disableRules(Arrays.asList(
                "WHITESPACE_RULE", 
                "EN_QUOTES",
                "UPPERCASE_SENTENCE_START",
                "SENTENCE_WHITESPACE",
                "EN_UNPAIRED_BRACKETS"
            ));
            
            logger.info("LanguageTool initialized with {} rules disabled for performance", 5);
        } catch (Exception e) {
            logger.error("Failed to initialize LanguageTool", e);
            throw new RuntimeException("Dictionary service initialization failed", e);
        }
        
        // High-performance cache for word validation
        this.validationCache = Caffeine.newBuilder()
                .maximumSize(50000) // Cache up to 50k words
                .expireAfterWrite(2, TimeUnit.HOURS) // Cache for 2 hours
                .recordStats()
                .build();
                
        // Smaller cache for word generation results
        this.wordGenerationCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build();
        
        logger.info("Optimized LanguageTool dictionary service initialized with caching");
    }
    
    @Override
    public boolean isValidWord(String word) {
        if (word == null || word.trim().isEmpty()) {
            return false;
        }
        
        String normalizedWord = word.trim().toUpperCase();
        
        // Use cache only if caching is enabled
        if (scrabbleProperties.getDictionary().getCaching().isEnabled()) {
            return validationCache.get(normalizedWord, this::validateWithLanguageTool);
        } else {
            return validateWithLanguageTool(normalizedWord);
        }
    }
    
    private Boolean validateWithLanguageTool(String word) {
        try {
            String lowerWord = word.toLowerCase();
            List<RuleMatch> matches = langTool.check(lowerWord);
            
            // Check for spelling errors specifically
            boolean hasSpellingErrors = matches.stream()
                    .anyMatch(match -> 
                        match.getRule().isDictionaryBasedSpellingRule() || 
                        match.getRule().getCategory().getId().toString().equals("TYPOS") ||
                        match.getMessage().toLowerCase().contains("spelling"));
            
            boolean isValid = !hasSpellingErrors;
            
            if (logger.isDebugEnabled()) {
                logger.debug("Word '{}' validation: {} (matches: {})", word, isValid, matches.size());
            }
            
            return isValid;
            
        } catch (IOException e) {
            logger.warn("LanguageTool validation failed for word '{}': {}", word, e.getMessage());
            // Graceful degradation: assume valid if validation fails
            return true;
        }
    }
    
    @Override
    public List<String> findPossibleWords(List<String> availableLetters, int minLength, int maxLength) {
        if (availableLetters == null || availableLetters.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Create cache key
        String cacheKey = String.format("%s_%d_%d", 
                String.join(",", availableLetters), minLength, maxLength);
        
        // Use cache only if caching is enabled
        if (scrabbleProperties.getDictionary().getCaching().isEnabled()) {
            return wordGenerationCache.get(cacheKey, key -> 
                    generateWordsEfficiently(availableLetters, minLength, maxLength));
        } else {
            return generateWordsEfficiently(availableLetters, minLength, maxLength);
        }
    }
    
    private List<String> generateWordsEfficiently(List<String> availableLetters, int minLength, int maxLength) {
        // Count available letters
        Map<Character, Integer> letterCounts = new HashMap<>();
        for (String letter : availableLetters) {
            if (letter != null && !letter.trim().isEmpty()) {
                char c = letter.trim().toUpperCase().charAt(0);
                if (Character.isLetter(c)) {
                    letterCounts.put(c, letterCounts.getOrDefault(c, 0) + 1);
                }
            }
        }
        
        if (letterCounts.isEmpty()) {
            return Collections.emptyList();
        }
        
        Set<String> possibleWords = new HashSet<>();
        
        // Choose approach based on configuration - use exhaustive only if explicitly enabled
        if (scrabbleProperties.getDictionary().getWordGeneration().getExhaustive().isEnabled()) {
            logger.debug("Using exhaustive word generation approach");
            generateExhaustiveWords(possibleWords, letterCounts, minLength, maxLength);
        } else {
            logger.debug("Using sampling word generation approach (default)");
            generateSamplingWords(possibleWords, letterCounts, minLength, maxLength);
        }
        
        // Convert to list and sort by scoring potential
        List<String> result = new ArrayList<>(possibleWords);
        result.sort((a, b) -> {
            int scoreA = calculateWordScoringPotential(a);
            int scoreB = calculateWordScoringPotential(b);
            return Integer.compare(scoreB, scoreA); // Highest scoring potential first
        });
        
        // Apply configured result limit
        int maxResults = scrabbleProperties.getDictionary().getWordGeneration().getExhaustive().isEnabled() ? 
            scrabbleProperties.getDictionary().getWordGeneration().getExhaustive().getMaxTotalResults() :
            scrabbleProperties.getDictionary().getWordGeneration().getSampling().getMaxTotalResults();
            
        return result.stream().limit(maxResults).collect(Collectors.toList());
    }
    
    private void generateSamplingWords(Set<String> results, Map<Character, Integer> letterCounts, int minLength, int maxLength) {
        // Generate combinations of available letters using sampling approach
        List<Character> availableChars = new ArrayList<>();
        for (Map.Entry<Character, Integer> entry : letterCounts.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                availableChars.add(entry.getKey());
            }
        }
        
        if (availableChars.isEmpty()) return;
        
        int maxAttempts = scrabbleProperties.getDictionary().getWordGeneration().getSampling().getMaxAttemptsPerLength();
        logger.debug("Using sampling approach with {} attempts per length", maxAttempts);
        
        // Try different length combinations - start with longest first for higher scoring words
        for (int length = Math.min(maxLength, availableChars.size()); length >= minLength; length--) {
            generateSamplingCombinationsOfLength(results, availableChars, length, maxAttempts);
        }
    }
    
    private void generateSamplingCombinationsOfLength(Set<String> results, List<Character> availableChars, int length, int maxAttempts) {
        Random rand = new Random();
        Set<String> attempted = new HashSet<>();
        
        for (int attempt = 0; attempt < maxAttempts && attempted.size() < maxAttempts / 2; attempt++) {
            List<Character> shuffled = new ArrayList<>(availableChars);
            Collections.shuffle(shuffled, rand);
            
            if (shuffled.size() >= length) {
                StringBuilder candidate = new StringBuilder();
                for (int i = 0; i < length; i++) {
                    candidate.append(shuffled.get(i));
                }
                
                String word = candidate.toString().toUpperCase();
                if (!attempted.contains(word)) {
                    attempted.add(word);
                    if (isValidWord(word)) {
                        results.add(word);
                    }
                }
            }
        }
    }
    
    private void generateExhaustiveWords(Set<String> results, Map<Character, Integer> letterCounts, int minLength, int maxLength) {
        logger.debug("Using exhaustive approach");
        // Convert letter counts to character array
        List<Character> availableChars = new ArrayList<>();
        for (Map.Entry<Character, Integer> entry : letterCounts.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                availableChars.add(entry.getKey());
            }
        }
        
        if (availableChars.isEmpty()) return;
        
        // Generate all possible permutations systematically for each length
        for (int length = minLength; length <= Math.min(maxLength, availableChars.size()); length++) {
            generateExhaustivePermutationsOfLength(results, availableChars, length);
            
            // Safety check to prevent infinite execution
            if (results.size() > scrabbleProperties.getDictionary().getWordGeneration().getExhaustive().getMaxTotalResults()) {
                logger.warn("Exhaustive approach hit safety limit, stopping generation");
                break;
            }
        }
    }
    
    private void generateExhaustivePermutationsOfLength(Set<String> results, List<Character> availableChars, int length) {
        Set<String> allPermutations = new HashSet<>();
        boolean[] used = new boolean[availableChars.size()];
        generatePermutations(availableChars, length, new ArrayList<>(), used, allPermutations);
        
        // Validate each permutation
        for (String permutation : allPermutations) {
            if (isValidWord(permutation)) {
                results.add(permutation);
            }
        }
    }
    
    private void generatePermutations(List<Character> availableChars, int length, 
                                    List<Character> current, boolean[] used, Set<String> results) {
        if (current.size() == length) {
            StringBuilder word = new StringBuilder();
            for (char c : current) {
                word.append(c);
            }
            results.add(word.toString().toUpperCase());
            return;
        }
        
        for (int i = 0; i < availableChars.size(); i++) {
            if (used[i]) continue;
            
            char ch = availableChars.get(i);
            current.add(ch);
            used[i] = true;
            
            generatePermutations(availableChars, length, current, used, results);
            
            current.remove(current.size() - 1);
            used[i] = false;
        }
    }
    
    private int calculateWordScoringPotential(String word) {
        // Calculate base Scrabble score using centralized utility
        int baseScore = ScrabbleScoreUtil.calculateWordScore(word);
        // Bonus for longer words to prioritize high-value candidates
        int lengthBonus = word.length() * 2;
        return baseScore + lengthBonus;
    }
    
    @Override
    public String getRandomWord(int minLength, int maxLength) {
        // Use different strategies based on word length for efficiency
        if (maxLength <= 5) {
            // For shorter words, try random generation first
            for (int attempts = 0; attempts < 500; attempts++) {
                String word = generateSmartRandomWord(minLength, maxLength);
                if (isValidWord(word)) {
                    return word.toUpperCase();
                }
            }
        }
        
        // For longer words or if short word generation failed, use pattern-based approach
        for (int attempts = 0; attempts < 300; attempts++) {
            String word = generateWordWithCommonPatterns(minLength, maxLength);
            if (isValidWord(word)) {
                return word.toUpperCase();
            }
        }
        
        // Try syllable-based generation for better efficiency
        for (int attempts = 0; attempts < 200; attempts++) {
            String word = generateSyllableBasedWord(minLength, maxLength);
            if (isValidWord(word)) {
                return word.toUpperCase();
            }
        }
        
        // If still no valid word found, throw exception rather than use fallback
        logger.error("Unable to generate any valid word of length {}-{} using LanguageTool after extensive attempts", minLength, maxLength);
        throw new RuntimeException("Dictionary service unable to generate valid word within specified length range");
    }
    
    private String generateSmartRandomWord(int minLength, int maxLength) {
        String vowels = "AEIOU";
        String consonants = "BCDFGHJKLMNPQRSTVWXYZ";
        StringBuilder word = new StringBuilder();
        
        int targetLength = minLength + random.nextInt(maxLength - minLength + 1);
        
        for (int i = 0; i < targetLength; i++) {
            if (i % 2 == 0 || random.nextBoolean()) {
                // Add consonant
                word.append(consonants.charAt(random.nextInt(consonants.length())));
            } else {
                // Add vowel
                word.append(vowels.charAt(random.nextInt(vowels.length())));
            }
        }
        
        return word.toString();
    }
    
    private String generateWordWithCommonPatterns(int minLength, int maxLength) {
        String vowels = "AEIOU";
        String consonants = "BCDFGHJKLMNPQRSTVWXYZ";
        StringBuilder word = new StringBuilder();
        
        int targetLength = minLength + random.nextInt(maxLength - minLength + 1);
        
        // Use more realistic English patterns (consonant-vowel alternation)
        for (int i = 0; i < targetLength; i++) {
            if (i % 2 == 0) {
                // Add consonant at even positions (start with consonant)
                word.append(consonants.charAt(random.nextInt(consonants.length())));
            } else {
                // Add vowel at odd positions
                word.append(vowels.charAt(random.nextInt(vowels.length())));
            }
        }
        
        return word.toString();
    }
    
    private String generateSyllableBasedWord(int minLength, int maxLength) {
        // Common English syllable patterns for more realistic word generation
        String[] onsets = {"B", "C", "D", "F", "G", "H", "J", "K", "L", "M", "N", "P", "R", "S", "T", "V", "W", "Y", "Z", 
                          "BL", "BR", "CL", "CR", "DR", "FL", "FR", "GL", "GR", "PL", "PR", "SL", "SM", "SN", "SP", "ST", "SW", "TR", "TW"};
        String[] nuclei = {"A", "E", "I", "O", "U", "AE", "AI", "AU", "EA", "EE", "EI", "IE", "OA", "OO", "OU"};
        String[] codas = {"", "B", "D", "F", "G", "K", "L", "M", "N", "P", "R", "S", "T", "V", "W", "X", "Z", 
                         "CH", "CK", "LL", "NG", "NK", "NT", "RD", "RM", "RN", "RT", "ST"};
        
        StringBuilder word = new StringBuilder();
        int targetLength = minLength + random.nextInt(maxLength - minLength + 1);
        
        while (word.length() < targetLength) {
            // Generate a syllable
            String onset = onsets[random.nextInt(onsets.length)];
            String nucleus = nuclei[random.nextInt(nuclei.length)];
            String coda = codas[random.nextInt(codas.length)];
            
            String syllable = onset + nucleus + coda;
            
            // Only add syllable if it doesn't make word too long
            if (word.length() + syllable.length() <= targetLength) {
                word.append(syllable);
            } else {
                // Try to complete with a shorter ending
                int remaining = targetLength - word.length();
                if (remaining > 0) {
                    word.append(nucleus.substring(0, Math.min(remaining, nucleus.length())));
                }
                break;
            }
        }
        
        // Trim to exact length if needed
        String result = word.toString();
        if (result.length() > targetLength) {
            result = result.substring(0, targetLength);
        }
        
        return result;
    }
    
    @Override
    public int getWordCount() {
        // Return a more realistic estimate of English words
        // The English language has approximately 170,000+ words in current use
        return 170000; // Conservative estimate of English words available via LanguageTool
    }
    
    @Override
    public List<String> getWordsByLength(int length) {
        Set<String> words = new HashSet<>();
        Map<Character, Integer> allLetters = new HashMap<>();
        
        // Provide all letters with reasonable frequency
        for (char c = 'A'; c <= 'Z'; c++) {
            allLetters.put(c, 5); // Assume 5 of each letter available
        }
        
        // Generate words of specific length using configured approach
        if (scrabbleProperties.getDictionary().getWordGeneration().getExhaustive().isEnabled()) {
            generateExhaustiveWords(words, allLetters, length, length);
        } else {
            generateSamplingWords(words, allLetters, length, length);
        }
        
        // Also try pure random generation for more variety
        for (int attempts = 0; attempts < 100; attempts++) {
            String randomWord = generateSmartRandomWord(length, length);
            if (isValidWord(randomWord)) {
                words.add(randomWord.toUpperCase());
            }
        }
        
        return words.stream()
                .filter(word -> word.length() == length)
                .limit(20)
                .collect(Collectors.toList());
    }
    
    private boolean canFormWord(String word, Map<Character, Integer> availableLetters) {
        Map<Character, Integer> needed = new HashMap<>();
        
        // Count letters needed for the word
        for (char c : word.toCharArray()) {
            needed.put(c, needed.getOrDefault(c, 0) + 1);
        }
        
        // Check if we have enough of each letter
        for (Map.Entry<Character, Integer> entry : needed.entrySet()) {
            char letter = entry.getKey();
            int neededCount = entry.getValue();
            int availableCount = availableLetters.getOrDefault(letter, 0);
            
            if (availableCount < neededCount) {
                return false;
            }
        }
        
        return true;
    }
    
    // Method to get cache statistics for monitoring
    public void logCacheStats() {
        var stats = validationCache.stats();
        logger.info("Validation cache stats - Size: {}, Hit rate: {:.2f}%, Evictions: {}", 
                validationCache.estimatedSize(),
                stats.hitRate() * 100,
                stats.evictionCount());
    }
}