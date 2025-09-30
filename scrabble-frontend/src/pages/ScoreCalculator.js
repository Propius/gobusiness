import React, { useState, useEffect, useCallback, useRef } from 'react';
import TileGrid from '../components/TileGrid';
import EnhancedTileGrid from '../components/EnhancedTileGrid';
import ScoreDisplay from '../components/ScoreDisplay';
import ActionButtons from '../components/ActionButtons';
import TopScoresModal from '../components/TopScoresModal';
import LetterScoringLegend from '../components/LetterScoring/LetterScoringLegend';
import { scrabbleService } from '../services/scrabbleService';
import { configService } from '../services/configService';
// import EnhancedTileInput from '../components/SpecialTiles/EnhancedTileInput';

function ScoreCalculator() {
  const [tileCount, setTileCount] = useState(10);
  const [tiles, setTiles] = useState(Array(10).fill(''));
  const [score, setScore] = useState(0);
  const [isValidWord, setIsValidWord] = useState(true);
  const [validationMessage, setValidationMessage] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [topScores, setTopScores] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [specialTilesEnabled, setSpecialTilesEnabled] = useState(false);
  const [letterScoringVisible, setLetterScoringVisible] = useState(false);
  const [letterScoringEnabled, setLetterScoringEnabled] = useState(false);
  const [specialTiles, setSpecialTiles] = useState(Array(10).fill('normal'));

  // Add state for managing API requests
  const [lastRequestTime, setLastRequestTime] = useState(0);
  const abortControllerRef = useRef(null);

  // Track previous state to detect actual changes
  const previousTilesRef = useRef('');
  const previousSpecialTilesRef = useRef('');

  // Load configuration on component mount
  useEffect(() => {
    const loadConfig = async () => {
      try {
        const [tileConfig, specialTilesConfig, letterScoringConfig] = await Promise.all([
          configService.getTileConfig(),
          configService.getSpecialTilesConfig(),
          configService.getLetterScoringConfig()
        ]);
        
        const count = tileConfig.scoreCalculator.tileCount;
        setTileCount(count);
        setTiles(Array(count).fill(''));
        setSpecialTiles(Array(count).fill('normal'));
        setSpecialTilesEnabled(specialTilesConfig.scoreCalculator.enabled);
        setLetterScoringEnabled(letterScoringConfig.scoreCalculator.enabled);
      } catch (error) {
        console.error('Error loading config:', error);
        // Fallback to defaults - tiles already initialized, just ensure other states are set
        setSpecialTilesEnabled(false);
        setLetterScoringEnabled(false);
      }
    };

    loadConfig();
  }, []);

  const calculateScore = useCallback(async (word, specialTilesData) => {
    // Cancel any pending request
    if (abortControllerRef.current) {
      abortControllerRef.current.abort();
    }

    // Create new abort controller for this request
    const controller = new AbortController();
    abortControllerRef.current = controller;
    
    // Set timestamp for this request
    const requestTime = Date.now();
    setLastRequestTime(requestTime);

    try {
      const response = await scrabbleService.calculateScore(word, specialTilesData, controller.signal);
      
      // Only update state if this is still the most recent request
      if (requestTime >= lastRequestTime && !controller.signal.aborted) {
        setScore(response.totalScore);
        setIsValidWord(response.isValidWord !== false);
        setValidationMessage(response.validationMessage || '');
      }
    } catch (error) {
      // Only handle error if this request wasn't cancelled and is still current
      if (!controller.signal.aborted && requestTime >= lastRequestTime) {
        console.error('Error calculating score:', error);
        setScore(0);
        setIsValidWord(true);
        setValidationMessage('');
      }
    }
  }, [lastRequestTime]);

  // Debounced effect for calculating score
  useEffect(() => {
    const word = tiles.join('');
    const currentTilesState = word;
    const currentSpecialTilesState = JSON.stringify(specialTiles);

    // Only trigger validation if:
    // 1. There is at least one letter on the board
    // 2. The board state has actually changed
    const hasLetters = word.trim().length > 0;
    const tilesChanged = currentTilesState !== previousTilesRef.current;
    const specialTilesChanged = currentSpecialTilesState !== previousSpecialTilesRef.current;
    const hasChanges = tilesChanged || specialTilesChanged;

    // If no letters or no changes, skip validation
    if (!hasLetters || !hasChanges) {
      // If no letters at all, reset score to 0
      if (!hasLetters) {
        setScore(0);
        setIsValidWord(true);
        setValidationMessage('');
      }
      return;
    }

    // Update previous state refs
    previousTilesRef.current = currentTilesState;
    previousSpecialTilesRef.current = currentSpecialTilesState;

    // Prepare special tiles data if enabled
    let specialTilesData = null;
    if (specialTilesEnabled && specialTiles.length > 0) {
      const positions = [];
      const specialTileTypes = [];

      // Map frontend tile types to backend expected formats
      const tileTypeMap = {
        'dl': 'double_letter',
        'tl': 'triple_letter',
        'dw': 'double_word',
        'tw': 'triple_word'
      };

      specialTiles.forEach((tileType, index) => {
        if (tileType !== 'normal') {
          positions.push(index);
          specialTileTypes.push(tileTypeMap[tileType] || tileType);
        }
      });

      if (positions.length > 0) {
        specialTilesData = {
          positions,
          specialTiles: specialTileTypes
        };
      }
    }

    // Debounce API calls - wait 500ms after user stops typing
    const timeoutId = setTimeout(() => {
      calculateScore(word, specialTilesData);
    }, 500);

    // Cleanup timeout if tiles change before timeout completes
    return () => clearTimeout(timeoutId);
  }, [tiles, specialTiles, specialTilesEnabled, calculateScore]);

  // Cleanup abort controller on unmount
  useEffect(() => {
    return () => {
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }
    };
  }, []);

  const handleTileChange = (index, value) => {
    if (value && !/^[A-Za-z]$/.test(value)) {
      return;
    }
    
    const newTiles = [...tiles];
    newTiles[index] = value;
    setTiles(newTiles);

    if (value && index < tiles.length - 1) {
      setTimeout(() => {
        const nextInput = document.querySelector(`input[data-index="${index + 1}"]`);
        if (nextInput) {
          nextInput.focus();
        }
      }, 50);
    }
  };

  const handleSpecialTileChange = (index, specialTileType) => {
    const newSpecialTiles = [...specialTiles];
    newSpecialTiles[index] = specialTileType;
    setSpecialTiles(newSpecialTiles);
  };

  const handleResetTiles = () => {
    setTiles(Array(tileCount).fill(''));
    setSpecialTiles(Array(tileCount).fill('normal'));
    setScore(0);
    setIsValidWord(true);
    setValidationMessage('');

    // Reset previous state tracking
    previousTilesRef.current = '';
    previousSpecialTilesRef.current = JSON.stringify(Array(tileCount).fill('normal'));

    const firstInput = document.querySelector('input[data-index="0"]');
    if (firstInput) {
      firstInput.focus();
    }
  };

  const handleSaveScore = async () => {
    const word = tiles.join('').trim();
    if (!word || score === 0) {
      alert('Please enter a valid word before saving the score.');
      return;
    }

    if (isValidWord === false) {
      alert(`Cannot save invalid word: ${validationMessage}`);
      return;
    }

    try {
      // Prepare special tiles data if enabled
      let positions = null;
      let specialTilesList = null;

      if (specialTilesEnabled && specialTiles.some(tile => tile !== 'normal')) {
        positions = [];
        specialTilesList = [];

        // Map frontend tile types to backend expected formats
        const tileTypeMap = {
          'dl': 'double_letter',
          'tl': 'triple_letter',
          'dw': 'double_word',
          'tw': 'triple_word'
        };

        specialTiles.forEach((tileType, index) => {
          if (tileType !== 'normal') {
            positions.push(index);
            specialTilesList.push(tileTypeMap[tileType] || tileType);
          }
        });

        // If no special tiles were actually used, set to null
        if (positions.length === 0) {
          positions = null;
          specialTilesList = null;
        }
      }

      // Save score with enhanced score and special tiles data
      await scrabbleService.saveScore(word, score, positions, specialTilesList);
      alert(`Score saved! Word: ${word}, Score: ${score}`);
    } catch (error) {
      console.error('Error saving score:', error);
      if (error.response && error.response.status === 400) {
        alert(`Cannot save score: ${error.response.data.message || 'Invalid word'}`);
      } else {
        alert('Error saving score. Please try again.');
      }
    }
  };

  const handleViewTopScores = async () => {
    setIsModalOpen(true);
    setLoading(true);
    setError('');

    try {
      const scores = await scrabbleService.getTopScores();
      setTopScores(scores);
    } catch (error) {
      console.error('Error fetching top scores:', error);
      setError('Failed to load top scores');
    } finally {
      setLoading(false);
    }
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setTopScores([]);
    setError('');
  };

  const toggleLetterScoring = () => {
    setLetterScoringVisible(!letterScoringVisible);
  };

  const currentWord = tiles.join('').trim();
  const canSaveScore = currentWord.length > 0 && score > 0 && isValidWord !== false;

  return (
    <div className="page-content">
      <header className="page-header">
        <h1>Scrabble Points Calculator</h1>
        <p>Enter letters in the tiles below to calculate your Scrabble score!</p>
      </header>
      
      <main className="calculator-main">
        {specialTilesEnabled ? (
          <EnhancedTileGrid 
            tiles={tiles}
            specialTiles={specialTiles}
            onTileChange={handleTileChange}
            onSpecialTileChange={handleSpecialTileChange}
            specialTilesEnabled={true}
          />
        ) : (
          <TileGrid 
            tiles={tiles}
            onTileChange={handleTileChange}
          />
        )}
        
        <ScoreDisplay 
          score={score} 
          isValidWord={isValidWord}
          validationMessage={validationMessage}
        />
        
        <ActionButtons
          onResetTiles={handleResetTiles}
          onSaveScore={handleSaveScore}
          onViewTopScores={handleViewTopScores}
          disabled={!canSaveScore}
        />
      </main>
      
      {letterScoringEnabled && (
        <LetterScoringLegend
          isVisible={letterScoringVisible}
          onToggle={toggleLetterScoring}
        />
      )}
      
      <TopScoresModal
        isOpen={isModalOpen}
        onClose={closeModal}
        scores={topScores}
        loading={loading}
        error={error}
      />
    </div>
  );
}

export default ScoreCalculator;