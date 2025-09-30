import React, { useState, useEffect } from 'react';
import { scrabbleService } from '../services/scrabbleService';
import { configService } from '../services/configService';
import EnhancedTileInput from './SpecialTiles/EnhancedTileInput';
import './WordFinder.css';

function WordFinder() {
  const [boardTileCount, setBoardTileCount] = useState(15);
  const [handTileCount, setHandTileCount] = useState(7);
  const [boardTiles, setBoardTiles] = useState([]);
  const [handTiles, setHandTiles] = useState([]);
  const [possibleWords, setPossibleWords] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [totalFound, setTotalFound] = useState(0);
  const [specialTilesEnabled, setSpecialTilesEnabled] = useState(false);
  const [specialTiles, setSpecialTiles] = useState([]);

  // Load configuration on component mount
  useEffect(() => {
    const loadConfig = async () => {
      try {
        const [tileConfig, specialTilesConfig] = await Promise.all([
          configService.getTileConfig(),
          configService.getSpecialTilesConfig()
        ]);
        
        const boardCount = tileConfig.wordFinder.boardTileCount;
        const handCount = tileConfig.wordFinder.handTileCount;
        setBoardTileCount(boardCount);
        setHandTileCount(handCount);
        setBoardTiles(Array(boardCount).fill(''));
        setHandTiles(Array(handCount).fill(''));
        setSpecialTiles(Array(boardCount).fill('normal'));
        setSpecialTilesEnabled(specialTilesConfig.wordFinder.enabled);
      } catch (error) {
        console.error('Error loading config:', error);
        // Fallback to defaults
        setBoardTiles(Array(15).fill(''));
        setHandTiles(Array(7).fill(''));
        setSpecialTiles(Array(15).fill('normal'));
        setSpecialTilesEnabled(false);
      }
    };

    loadConfig();
  }, []);

  const handleBoardTileChange = (index, value) => {
    if (value && !/^[A-Za-z]$/.test(value)) {
      return;
    }
    
    const newTiles = [...boardTiles];
    newTiles[index] = value.toUpperCase();
    setBoardTiles(newTiles);
  };

  const handleHandTileChange = (index, value) => {
    if (value && !/^[A-Za-z]$/.test(value)) {
      return;
    }
    
    const newTiles = [...handTiles];
    newTiles[index] = value.toUpperCase();
    setHandTiles(newTiles);
    
    if (value && index < handTiles.length - 1) {
      setTimeout(() => {
        const nextInput = document.querySelector(`input[data-hand-index="${index + 1}"]`);
        if (nextInput) {
          nextInput.focus();
        }
      }, 50);
    }
  };

  const handleResetBoard = () => {
    setBoardTiles(Array(boardTileCount).fill(''));
    setSpecialTiles(Array(boardTileCount).fill('normal'));
    setPossibleWords([]);
    setMessage('');
    setError('');
  };

  const handleSpecialTileChange = (index, specialTileType) => {
    const newSpecialTiles = [...specialTiles];
    newSpecialTiles[index] = specialTileType;
    setSpecialTiles(newSpecialTiles);
  };

  const handleResetHand = () => {
    setHandTiles(Array(handTileCount).fill(''));
    setPossibleWords([]);
    setMessage('');
    setError('');
  };

  const handleFindWords = async () => {
    const filledHandTiles = handTiles.filter(tile => tile.trim() !== '');
    
    if (filledHandTiles.length === 0) {
      setError('Please enter at least one hand tile');
      return;
    }

    setLoading(true);
    setError('');
    setPossibleWords([]);
    setMessage('');

    try {
      const response = await scrabbleService.findPossibleWords(boardTiles, handTiles);
      setPossibleWords(response.possibleWords);
      setTotalFound(response.totalFound);
      setMessage(response.message);
    } catch (error) {
      console.error('Error finding possible words:', error);
      setError('Failed to find possible words. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="word-finder">
      <h2>Word Finder</h2>
      <p>Enter letters on the board and in your hand to find possible words!</p>
      
      <div className="tiles-section">
        <div className="board-section">
          <h3>Board Tiles (Fixed Positions)</h3>
          <div className="board-tiles">
            {boardTiles.map((tile, index) => (
              specialTilesEnabled ? (
                <EnhancedTileInput
                  key={index}
                  value={tile}
                  onChange={handleBoardTileChange}
                  onSpecialTileChange={handleSpecialTileChange}
                  index={index}
                  specialTileType={specialTiles[index] || 'normal'}
                  specialTilesEnabled={true}
                  className="board-tile"
                  placeholder={String(index + 1)}
                  dataAttribute="board-index"
                />
              ) : (
                <input
                  key={index}
                  type="text"
                  maxLength="1"
                  value={tile}
                  onChange={(e) => handleBoardTileChange(index, e.target.value)}
                  className="board-tile"
                  data-board-index={index}
                  placeholder={index + 1}
                />
              )
            ))}
          </div>
          <button type="button" onClick={handleResetBoard} className="reset-button">
            Reset Board
          </button>
          {specialTilesEnabled && (
            <p className="special-tiles-help">
              Click ⚙ on board tiles to set special multipliers
            </p>
          )}
        </div>

        <div className="hand-section">
          <h3>Hand Tiles (Available Letters)</h3>
          <div className="hand-tiles">
            {handTiles.map((tile, index) => (
              <input
                key={index}
                type="text"
                maxLength="1"
                value={tile}
                onChange={(e) => handleHandTileChange(index, e.target.value)}
                className="hand-tile"
                data-hand-index={index}
              />
            ))}
          </div>
          <button type="button" onClick={handleResetHand} className="reset-button">
            Reset Hand
          </button>
        </div>
      </div>

      <div className="actions">
        <button 
          onClick={handleFindWords} 
          disabled={loading || handTiles.filter(t => t.trim()).length === 0}
          className="find-words-button"
        >
          {loading ? 'Finding Words...' : 'Find Possible Words'}
        </button>
      </div>

      {error && <div className="error-message">{error}</div>}
      
      {message && (
        <div className="results-info">
          {message}
        </div>
      )}

      {possibleWords.length > 0 && (
        <div className="results">
          <h3>Possible Words</h3>
          <div className="words-grid">
            {possibleWords.map((wordObj, index) => (
              <div key={index} className="word-item">
                <div className="word-text">{wordObj.word}</div>
                <div className="word-score">{wordObj.score} pts</div>
                <div className="word-details">
                  <div className="positions">Positions: {wordObj.positions.join(', ')}</div>
                  <div className="used-tiles">
                    Hand: [{wordObj.usedHandTiles.join(', ')}]
                    {wordObj.usedBoardTiles.length > 0 && (
                      <span> | Board: [{wordObj.usedBoardTiles.join(', ')}]</span>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

export default WordFinder;