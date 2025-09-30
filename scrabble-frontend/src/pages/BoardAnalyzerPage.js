import React, { useState, useEffect } from 'react';
import axios from 'axios';
import SpecialTileSelector, { SPECIAL_TILE_TYPES } from '../components/SpecialTiles/SpecialTileSelector';
import LetterScoringLegend from '../components/LetterScoring/LetterScoringLegend';
import PredefinedBoardToggle from '../components/BoardAnalyzer/PredefinedBoardToggle';
import { configService } from '../services/configService';
import { getApiUrl } from '../config/api.config';
import './BoardAnalyzerPage.css';

const BoardAnalyzerPage = () => {
  const [boardLetters, setBoardLetters] = useState(Array(225).fill('')); // 15x15 board
  const [handLetters, setHandLetters] = useState(Array(7).fill(''));
  const [specialTiles, setSpecialTiles] = useState(Array(225).fill('normal')); // 15x15 special tiles
  const [results, setResults] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [isEnabled, setIsEnabled] = useState(false);
  const [boardSize, setBoardSize] = useState(15);
  const [specialTilesEnabled, setSpecialTilesEnabled] = useState(false);
  const [showSpecialTilesMode, setShowSpecialTilesMode] = useState(false);
  const [letterScoringVisible, setLetterScoringVisible] = useState(false);
  const [letterScoringEnabled, setLetterScoringEnabled] = useState(false);
  const [predefinedBoardEnabled, setPredefinedBoardEnabled] = useState(false);

  useEffect(() => {
    checkFeatureStatus();
  }, []);

  const checkFeatureStatus = async () => {
    try {
      const [statusResponse, specialTilesResponse, letterScoringResponse] = await Promise.all([
        axios.get(getApiUrl('boardAnalyzer', 'status')),
        axios.get(getApiUrl('config', 'specialTiles')),
        configService.getLetterScoringConfig()
      ]);
      setIsEnabled(statusResponse.data.enabled);
      setSpecialTilesEnabled(specialTilesResponse.data.boardAnalyzer.enabled);
      setLetterScoringEnabled(letterScoringResponse.boardAnalyzer.enabled);
    } catch (error) {
      console.error('Error checking feature status:', error);
      setLetterScoringEnabled(false);
    }
  };

  const handleBoardLetterChange = (index, value) => {
    const newBoardLetters = [...boardLetters];
    newBoardLetters[index] = value.toUpperCase();
    setBoardLetters(newBoardLetters);
  };

  const handleHandLetterChange = (index, value) => {
    const newHandLetters = [...handLetters];
    newHandLetters[index] = value.toUpperCase();
    setHandLetters(newHandLetters);
  };

  const handleSpecialTileChange = (index, tileType) => {
    const newSpecialTiles = [...specialTiles];
    newSpecialTiles[index] = tileType;
    setSpecialTiles(newSpecialTiles);
  };

  const clearBoard = () => {
    setBoardLetters(Array(225).fill(''));
    setHandLetters(Array(7).fill(''));
    // Preserve special tiles - do not reset them
    // setSpecialTiles(Array(225).fill('normal')); // REMOVED: This was clearing special tiles
    setResults(null);
    setError('');
  };

  const analyzeBoard = async () => {
    if (!isEnabled) {
      setError('Board analyzer feature is not enabled');
      return;
    }

    // Check if we have at least one hand letter
    const hasHandLetters = handLetters.some(letter => letter.trim() !== '');
    if (!hasHandLetters) {
      setError('Please enter at least one hand letter');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const response = await axios.post('http://localhost:8080/api/board-analyzer/analyze', {
        boardLetters: boardLetters,
        handLetters: handLetters.filter(letter => letter.trim() !== ''),
        specialTiles: specialTiles
      });

      setResults(response.data);
    } catch (error) {
      console.error('Error analyzing board:', error);
      if (error.response?.status === 503) {
        setError('Board analyzer feature is not enabled on the server');
      } else {
        setError('Failed to analyze board. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  const renderBoard = () => {
    try {
      const elements = [];
    
    // Add column headers (A-O for 15x15)
    for (let col = 0; col < boardSize; col++) {
      elements.push(
        <div
          key={`col-header-${col}`}
          className="board-header col-header"
          style={{
            gridRow: 1,
            gridColumn: col + 2
          }}
        >
          {String.fromCharCode(65 + col)}
        </div>
      );
    }
    
    // Add row headers (1-15)
    for (let row = 0; row < boardSize; row++) {
      elements.push(
        <div
          key={`row-header-${row}`}
          className="board-header row-header"
          style={{
            gridRow: row + 2,
            gridColumn: 1
          }}
        >
          {row + 1}
        </div>
      );
    }
    
    // Add board tiles
    for (let row = 0; row < boardSize; row++) {
      for (let col = 0; col < boardSize; col++) {
        const index = row * boardSize + col;
        const specialTileType = specialTiles[index] || 'normal';
        const tileStyle = SPECIAL_TILE_TYPES[specialTileType] || SPECIAL_TILE_TYPES.normal;
        
        if (showSpecialTilesMode && specialTilesEnabled) {
          // Show special tile selector in edit mode
          elements.push(
            <div
              key={`special-${index}`}
              className="special-tile-cell"
              style={{
                gridRow: row + 2,
                gridColumn: col + 2,
                backgroundColor: tileStyle?.color || '#f5f5dc'
              }}
              title={`${String.fromCharCode(65 + col)}${row + 1} - ${tileStyle?.name || 'Normal'}`}
            >
              <SpecialTileSelector
                selectedType={specialTileType}
                onTypeChange={(_, tileType) => handleSpecialTileChange(index, tileType)}
                position={index}
              />
            </div>
          );
        } else {
          // Normal letter input mode
          elements.push(
            <input
              key={`board-${index}`}
              type="text"
              maxLength="1"
              value={boardLetters[index]}
              onChange={(e) => handleBoardLetterChange(index, e.target.value)}
              className="board-tile"
              style={{
                gridRow: row + 2,
                gridColumn: col + 2,
                backgroundColor: specialTilesEnabled ? (tileStyle?.color || '#f5f5dc') : '#F5DEB3'
              }}
              title={`${String.fromCharCode(65 + col)}${row + 1}${specialTilesEnabled ? ` - ${tileStyle?.name || 'Normal'}` : ''}`}
            />
          );
        }
      }
    }
    
      return elements;
    } catch (error) {
      console.error('Error rendering board:', error);
      return [
        <div key="error" className="error-message" style={{ gridColumn: '1 / -1', gridRow: '1 / -1' }}>
          Error rendering board. Please refresh the page.
        </div>
      ];
    }
  };

  const renderResults = () => {
    if (!results || !results.topCombinations) return null;

    return (
      <div className="results-section">
        <h3>Top Scoring Combinations</h3>
        <p>{results.message}</p>
        
        {results.topCombinations.length === 0 ? (
          <p>No valid combinations found with the current board and hand tiles.</p>
        ) : (
          <div className="combinations-list">
            {results.topCombinations.map((combination, index) => (
              <div key={index} className="combination-card">
                <div className="combination-header">
                  <span className="rank">#{index + 1}</span>
                  <span className="word">{combination.word}</span>
                  <span className="score">{combination.totalScore} points</span>
                </div>
                <div className="combination-details">
                  <p>Position: {String.fromCharCode(65 + combination.startCol)}{combination.startRow + 1}</p>
                  <p>Direction: {combination.direction}</p>
                  <p>Hand tiles used: {combination.usedHandTiles.join(', ')}</p>
                  {combination.bonusesApplied && combination.bonusesApplied.length > 0 && (
                    <p>Bonuses: {combination.bonusesApplied.join(', ')}</p>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    );
  };

  // Check if SPECIAL_TILE_TYPES is properly loaded
  if (!SPECIAL_TILE_TYPES || typeof SPECIAL_TILE_TYPES !== 'object') {
    return (
      <div className="board-analyzer-page">
        <div className="error-message">
          <h2>Board Analyzer</h2>
          <p>Error loading special tiles configuration. Please refresh the page.</p>
        </div>
      </div>
    );
  }

  if (!isEnabled) {
    return (
      <div className="board-analyzer-page">
        <div className="feature-disabled">
          <h2>Board Analyzer</h2>
          <p>This feature is currently disabled. Please enable it in the application configuration.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="board-analyzer-page">
      <div className="header">
        <h2>Board Analyzer</h2>
        <p>Find the highest scoring word combinations for your current board and hand tiles.</p>
      </div>

      <div className="game-container">
        <div className="board-section">
          <h3>Game Board ({boardSize}x{boardSize})</h3>
          <div 
            className="game-board"
            style={{
              display: 'grid',
              gridTemplateColumns: `25px repeat(${boardSize}, 30px)`,
              gridTemplateRows: `20px repeat(${boardSize}, 30px)`,
              justifyContent: 'center'
            }}
          >
            {renderBoard()}
          </div>
        </div>

        <div className="hand-section">
          <h3>Hand Tiles</h3>
          <div className="hand-tiles">
            {handLetters.map((letter, index) => (
              <input
                key={`hand-${index}`}
                type="text"
                maxLength="1"
                value={letter}
                onChange={(e) => handleHandLetterChange(index, e.target.value)}
                className="hand-tile"
                placeholder={String(index + 1)}
              />
            ))}
          </div>
        </div>

        <div className="controls-section">
          <button 
            onClick={analyzeBoard} 
            disabled={loading}
            className="analyze-btn"
          >
            {loading ? 'Analyzing...' : 'Analyze Board'}
          </button>
          <button
            onClick={clearBoard}
            className="clear-btn"
          >
            Clear Letter Tiles
          </button>
          {specialTilesEnabled && (
            <button
              onClick={() => setShowSpecialTilesMode(!showSpecialTilesMode)}
              className="special-tiles-btn"
            >
              {showSpecialTilesMode ? 'Exit Special Tiles Mode' : 'Edit Special Tiles'}
            </button>
          )}
        </div>

        {specialTilesEnabled && (
          <div className="special-tiles-info">
            <h4>Special Tiles Legend:</h4>
            <div className="legend">
              {Object.entries(SPECIAL_TILE_TYPES).map(([key, type]) => (
                <div key={key} className="legend-item">
                  <div 
                    className="legend-color" 
                    style={{ backgroundColor: type.color }}
                  ></div>
                  <span>{type.name}</span>
                </div>
              ))}
            </div>
          </div>
        )}

        {specialTilesEnabled && (
          <PredefinedBoardToggle
            isEnabled={predefinedBoardEnabled}
            onToggle={() => setPredefinedBoardEnabled(!predefinedBoardEnabled)}
            onApplyStandardBoard={(standardBoard) => setSpecialTiles(standardBoard)}
            boardSize={boardSize}
          />
        )}

        {error && <div className="error-message">{error}</div>}
      </div>

      {letterScoringEnabled && (
        <LetterScoringLegend
          isVisible={letterScoringVisible}
          onToggle={() => setLetterScoringVisible(!letterScoringVisible)}
        />
      )}

      {renderResults()}
    </div>
  );
};

export default BoardAnalyzerPage;