package com.govtech.scrabble.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "scrabble")
public class ScrabbleProperties {
    
    private Dictionary dictionary = new Dictionary();
    private Scramble scramble = new Scramble();
    private Tiles tiles = new Tiles();
    private SpecialTiles specialTiles = new SpecialTiles();
    private BoardAnalyzer boardAnalyzer = new BoardAnalyzer();
    private LetterScoring letterScoring = new LetterScoring();
    private WordFinder wordFinder = new WordFinder();
    
    public Dictionary getDictionary() {
        return dictionary;
    }
    
    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }
    
    public Scramble getScramble() {
        return scramble;
    }
    
    public void setScramble(Scramble scramble) {
        this.scramble = scramble;
    }
    
    public Tiles getTiles() {
        return tiles;
    }
    
    public void setTiles(Tiles tiles) {
        this.tiles = tiles;
    }
    
    public SpecialTiles getSpecialTiles() {
        return specialTiles;
    }
    
    public void setSpecialTiles(SpecialTiles specialTiles) {
        this.specialTiles = specialTiles;
    }
    
    public BoardAnalyzer getBoardAnalyzer() {
        return boardAnalyzer;
    }
    
    public void setBoardAnalyzer(BoardAnalyzer boardAnalyzer) {
        this.boardAnalyzer = boardAnalyzer;
    }
    
    public LetterScoring getLetterScoring() {
        return letterScoring;
    }
    
    public void setLetterScoring(LetterScoring letterScoring) {
        this.letterScoring = letterScoring;
    }
    
    public WordFinder getWordFinder() {
        return wordFinder;
    }
    
    public void setWordFinder(WordFinder wordFinder) {
        this.wordFinder = wordFinder;
    }
    
    public static class Dictionary {
        private Validation validation = new Validation();
        private WordGeneration wordGeneration = new WordGeneration();
        private Caching caching = new Caching();
        
        public Validation getValidation() {
            return validation;
        }
        
        public void setValidation(Validation validation) {
            this.validation = validation;
        }
        
        public WordGeneration getWordGeneration() {
            return wordGeneration;
        }
        
        public void setWordGeneration(WordGeneration wordGeneration) {
            this.wordGeneration = wordGeneration;
        }
        
        public Caching getCaching() {
            return caching;
        }
        
        public void setCaching(Caching caching) {
            this.caching = caching;
        }
    }
    
    public static class Validation {
        private boolean enabled = false;
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
    
    public static class WordGeneration {
        private SamplingConfig sampling = new SamplingConfig();
        private ExhaustiveConfig exhaustive = new ExhaustiveConfig();
        
        public SamplingConfig getSampling() {
            return sampling;
        }
        
        public void setSampling(SamplingConfig sampling) {
            this.sampling = sampling;
        }
        
        public ExhaustiveConfig getExhaustive() {
            return exhaustive;
        }
        
        public void setExhaustive(ExhaustiveConfig exhaustive) {
            this.exhaustive = exhaustive;
        }
        
        public static class SamplingConfig {
            private int maxAttemptsPerLength = 50;
            private int maxTotalResults = 100;
            
            public int getMaxAttemptsPerLength() {
                return maxAttemptsPerLength;
            }
            
            public void setMaxAttemptsPerLength(int maxAttemptsPerLength) {
                this.maxAttemptsPerLength = maxAttemptsPerLength;
            }
            
            public int getMaxTotalResults() {
                return maxTotalResults;
            }
            
            public void setMaxTotalResults(int maxTotalResults) {
                this.maxTotalResults = maxTotalResults;
            }
        }
        
        public static class ExhaustiveConfig {
            private boolean enabled = false;
            private int maxTotalResults = 1000;
            
            public boolean isEnabled() {
                return enabled;
            }
            
            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }
            
            public int getMaxTotalResults() {
                return maxTotalResults;
            }
            
            public void setMaxTotalResults(int maxTotalResults) {
                this.maxTotalResults = maxTotalResults;
            }
        }
    }
    
    public static class Scramble {
        private boolean enabled = true;
        private WordLength wordLength = new WordLength();
        private Map<String, DifficultyLevel> difficultyLevels = new HashMap<>();
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public WordLength getWordLength() {
            return wordLength;
        }
        
        public void setWordLength(WordLength wordLength) {
            this.wordLength = wordLength;
        }
        
        public Map<String, DifficultyLevel> getDifficultyLevels() {
            return difficultyLevels;
        }
        
        public void setDifficultyLevels(Map<String, DifficultyLevel> difficultyLevels) {
            this.difficultyLevels = difficultyLevels;
        }
    }
    
    public static class WordLength {
        private int min = 4;
        private int max = 8;
        
        public int getMin() {
            return min;
        }
        
        public void setMin(int min) {
            this.min = min;
        }
        
        public int getMax() {
            return max;
        }
        
        public void setMax(int max) {
            this.max = max;
        }
    }
    
    public static class DifficultyLevel {
        private int min;
        private int max;
        
        public DifficultyLevel() {}
        
        public DifficultyLevel(int min, int max) {
            this.min = min;
            this.max = max;
        }
        
        public int getMin() {
            return min;
        }
        
        public void setMin(int min) {
            this.min = min;
        }
        
        public int getMax() {
            return max;
        }
        
        public void setMax(int max) {
            this.max = max;
        }
    }
    
    public static class Tiles {
        private ScoreCalculator scoreCalculator = new ScoreCalculator();
        private WordFinder wordFinder = new WordFinder();
        private BoardAnalyzerTiles boardAnalyzer = new BoardAnalyzerTiles();
        
        public ScoreCalculator getScoreCalculator() {
            return scoreCalculator;
        }
        
        public void setScoreCalculator(ScoreCalculator scoreCalculator) {
            this.scoreCalculator = scoreCalculator;
        }
        
        public WordFinder getWordFinder() {
            return wordFinder;
        }
        
        public void setWordFinder(WordFinder wordFinder) {
            this.wordFinder = wordFinder;
        }
        
        public BoardAnalyzerTiles getBoardAnalyzer() {
            return boardAnalyzer;
        }
        
        public void setBoardAnalyzer(BoardAnalyzerTiles boardAnalyzer) {
            this.boardAnalyzer = boardAnalyzer;
        }
        
        public static class ScoreCalculator {
            private int tileCount = 10;
            
            public int getTileCount() {
                return tileCount;
            }
            
            public void setTileCount(int tileCount) {
                this.tileCount = tileCount;
            }
        }
        
        public static class WordFinder {
            private int boardTileCount = 15;
            private int handTileCount = 7;
            
            public int getBoardTileCount() {
                return boardTileCount;
            }
            
            public void setBoardTileCount(int boardTileCount) {
                this.boardTileCount = boardTileCount;
            }
            
            public int getHandTileCount() {
                return handTileCount;
            }
            
            public void setHandTileCount(int handTileCount) {
                this.handTileCount = handTileCount;
            }
        }
        
        public static class BoardAnalyzerTiles {
            private int boardSize = 15;
            
            public int getBoardSize() {
                return boardSize;
            }
            
            public void setBoardSize(int boardSize) {
                this.boardSize = boardSize;
            }
        }
    }
    
    public static class SpecialTiles {
        private FeatureFlag scoreCalculator = new FeatureFlag();
        private FeatureFlag wordFinder = new FeatureFlag();
        private FeatureFlag boardAnalyzer = new FeatureFlag();
        
        public FeatureFlag getScoreCalculator() {
            return scoreCalculator;
        }
        
        public void setScoreCalculator(FeatureFlag scoreCalculator) {
            this.scoreCalculator = scoreCalculator;
        }
        
        public FeatureFlag getWordFinder() {
            return wordFinder;
        }
        
        public void setWordFinder(FeatureFlag wordFinder) {
            this.wordFinder = wordFinder;
        }
        
        public FeatureFlag getBoardAnalyzer() {
            return boardAnalyzer;
        }
        
        public void setBoardAnalyzer(FeatureFlag boardAnalyzer) {
            this.boardAnalyzer = boardAnalyzer;
        }
        
        public static class FeatureFlag {
            private boolean enabled = false;
            
            public boolean isEnabled() {
                return enabled;
            }
            
            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }
        }
    }
    
    public static class BoardAnalyzer {
        private boolean enabled = false;
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
    
    public static class LetterScoring {
        private FeatureFlag scoreCalculator = new FeatureFlag();
        private FeatureFlag wordFinder = new FeatureFlag();
        private FeatureFlag boardAnalyzer = new FeatureFlag();
        
        public FeatureFlag getScoreCalculator() {
            return scoreCalculator;
        }
        
        public void setScoreCalculator(FeatureFlag scoreCalculator) {
            this.scoreCalculator = scoreCalculator;
        }
        
        public FeatureFlag getWordFinder() {
            return wordFinder;
        }
        
        public void setWordFinder(FeatureFlag wordFinder) {
            this.wordFinder = wordFinder;
        }
        
        public FeatureFlag getBoardAnalyzer() {
            return boardAnalyzer;
        }
        
        public void setBoardAnalyzer(FeatureFlag boardAnalyzer) {
            this.boardAnalyzer = boardAnalyzer;
        }
        
        public static class FeatureFlag {
            private boolean enabled = false;
            
            public boolean isEnabled() {
                return enabled;
            }
            
            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }
        }
    }
    
    public static class WordFinder {
        private boolean enabled = false;
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
    
    public static class Caching {
        private boolean enabled = true;
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}