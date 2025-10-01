import React, { useState, useEffect } from 'react';
import TileGrid from '../components/TileGrid';
import EnhancedTileGrid from '../components/EnhancedTileGrid';
import ScoreDisplay from '../components/ScoreDisplay';
import ActionButtons from '../components/ActionButtons';
import TopScoresModal from '../components/TopScoresModal';
import LetterScoringLegend from '../components/LetterScoring/LetterScoringLegend';
import { scrabbleService } from '../services/scrabbleService';
import { configService } from '../services/configService';
import { calculateWordScore, calculateScoreWithSpecialTiles } from '../utils/scrabbleScore';
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
  const [isSaving, setIsSaving] = useState(false);

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

  // Instant score calculation (frontend-only)
  useEffect(() => {
    const word = tiles.join('');

    if (!word.trim()) {
      setScore(0);
      setIsValidWord(true);
      setValidationMessage('');
      return;
    }

    if (specialTilesEnabled) {
      // Map frontend tile types to backend format expected by utility
      const mappedSpecialTiles = specialTiles.map(tileType => {
        const tileTypeMap = {
          'dl': 'dl',
          'tl': 'tl',
          'dw': 'dw',
          'tw': 'tw',
          'normal': 'normal'
        };
        return tileTypeMap[tileType] || 'normal';
      });

      const enhancedScore = calculateScoreWithSpecialTiles(word, mappedSpecialTiles);
      setScore(enhancedScore);
    } else {
      const basicScore = calculateWordScore(word);
      setScore(basicScore);
    }

    // Clear validation state - validation now happens only on save
    setIsValidWord(true);
    setValidationMessage('');
  }, [tiles, specialTiles, specialTilesEnabled]);

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

  const handleKeyDown = (index, event) => {
    if (event.key === 'Backspace') {
      event.preventDefault();

      // Find the last filled tile from right to left for smooth deletion
      let lastFilledIndex = -1;
      for (let i = tiles.length - 1; i >= 0; i--) {
        if (tiles[i]) {
          lastFilledIndex = i;
          break;
        }
      }

      if (lastFilledIndex >= 0) {
        handleTileChange(lastFilledIndex, '');
        setTimeout(() => {
          const targetInput = document.querySelector(`input[data-index="${lastFilledIndex}"]`);
          if (targetInput) {
            targetInput.focus();
          }
        }, 10);
      }
    } else if (event.key === 'ArrowLeft' && index > 0) {
      const prevInput = document.querySelector(`input[data-index="${index - 1}"]`);
      if (prevInput) prevInput.focus();
    } else if (event.key === 'ArrowRight' && index < tiles.length - 1) {
      const nextInput = document.querySelector(`input[data-index="${index + 1}"]`);
      if (nextInput) nextInput.focus();
    }
  };

  const handleResetTiles = () => {
    setTiles(Array(tileCount).fill(''));
    setSpecialTiles(Array(tileCount).fill('normal'));
    setScore(0);
    setIsValidWord(true);
    setValidationMessage('');

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

    setIsSaving(true);
    try {
      // Validate word before saving
      const validationResponse = await scrabbleService.validateWord(word);

      if (!validationResponse.isValidWord) {
        setIsValidWord(false);
        setValidationMessage(validationResponse.validationMessage || 'Invalid word');
        alert(`Cannot save invalid word: ${validationResponse.validationMessage || 'Invalid word'}`);
        return;
      }

      // Word is valid, proceed with saving
      setIsValidWord(true);
      setValidationMessage('');

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
    } finally {
      setIsSaving(false);
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
            onKeyDown={handleKeyDown}
            specialTilesEnabled={true}
          />
        ) : (
          <TileGrid
            tiles={tiles}
            onTileChange={handleTileChange}
            onKeyDown={handleKeyDown}
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
          disabled={!canSaveScore || isSaving}
          isSaving={isSaving}
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