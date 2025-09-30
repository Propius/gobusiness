import React, { useState, useEffect } from 'react';
import { scrambleService } from '../services/scrambleService';
import './ScrambleGame.css';

const ScrambleGame = () => {
  const [scrambleData, setScrambleData] = useState(null);
  const [userAnswer, setUserAnswer] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [feedback, setFeedback] = useState(null);
  const [gameActive, setGameActive] = useState(false);
  // eslint-disable-next-line no-unused-vars
  const [difficulty, setDifficulty] = useState('');

  useEffect(() => {
    checkIfScrambleEnabled();
  }, []);

  const checkIfScrambleEnabled = async () => {
    try {
      const stats = await scrambleService.getStats();
      setGameActive(stats.featureEnabled);
    } catch (error) {
      console.error('Error checking scramble status:', error);
      setGameActive(false);
    }
  };

  const startNewGame = async (selectedDifficulty = null) => {
    setIsLoading(true);
    setFeedback(null);
    setUserAnswer('');

    try {
      const newScramble = await scrambleService.generateNewScramble(selectedDifficulty);
      setScrambleData(newScramble);
      setDifficulty(selectedDifficulty || '');
    } catch (error) {
      console.error('Error starting new game:', error);
      setFeedback({
        type: 'error',
        message: 'Unable to start new game. Please try again.'
      });
    } finally {
      setIsLoading(false);
    }
  };

  const reshuffleLetters = async () => {
    if (!scrambleData || !scrambleData.originalWord) return;

    setIsLoading(true);
    try {
      const reshuffled = await scrambleService.reshuffleScramble(scrambleData.originalWord);
      setScrambleData(reshuffled);
      setFeedback(null);
    } catch (error) {
      console.error('Error reshuffling letters:', error);
      setFeedback({
        type: 'error',
        message: 'Unable to reshuffle letters. Please try again.'
      });
    } finally {
      setIsLoading(false);
    }
  };

  const checkAnswer = async () => {
    if (!scrambleData || !userAnswer.trim()) {
      setFeedback({
        type: 'warning',
        message: 'Please enter your answer before checking.'
      });
      return;
    }

    setIsLoading(true);
    try {
      const result = await scrambleService.checkAnswer(scrambleData.originalWord, userAnswer.trim());
      
      if (result.isCorrect) {
        setFeedback({
          type: 'success',
          message: `${result.message} Score: ${result.score} points!`,
          score: result.score,
          correctAnswer: result.correctAnswer
        });
        setScrambleData(null);
      } else {
        setFeedback({
          type: 'error',
          message: result.message
        });
      }
    } catch (error) {
      console.error('Error checking answer:', error);
      setFeedback({
        type: 'error',
        message: 'Unable to check answer. Please try again.'
      });
    } finally {
      setIsLoading(false);
    }
  };

  const handleKeyPress = (event) => {
    if (event.key === 'Enter') {
      checkAnswer();
    }
  };

  const giveUp = async () => {
    if (!scrambleData) return;

    try {
      const result = await scrambleService.checkAnswer(scrambleData.originalWord, '');
      setFeedback({
        type: 'info',
        message: `The correct answer was: ${result.correctAnswer}`,
        correctAnswer: result.correctAnswer
      });
      setScrambleData(null);
    } catch (error) {
      console.error('Error revealing answer:', error);
    }
  };

  if (!gameActive) {
    return (
      <div className="scramble-game disabled">
        <h3>🎲 Word Scramble Game</h3>
        <p>Word scramble feature is currently disabled.</p>
      </div>
    );
  }

  return (
    <div className="scramble-game">
      <div className="scramble-header">
        <h3>🎲 Word Scramble Game</h3>
        <p>Unscramble the letters to form a word!</p>
      </div>

      {!scrambleData && !feedback && (
        <div className="difficulty-selection">
          <h4>Choose Difficulty Level</h4>
          <div className="difficulty-buttons">
            <button 
              className="difficulty-btn easy"
              onClick={() => startNewGame('easy')}
              disabled={isLoading}
            >
              Easy (4-5 letters)
            </button>
            <button 
              className="difficulty-btn medium"
              onClick={() => startNewGame('medium')}
              disabled={isLoading}
            >
              Medium (6-7 letters)
            </button>
            <button 
              className="difficulty-btn hard"
              onClick={() => startNewGame('hard')}
              disabled={isLoading}
            >
              Hard (8+ letters)
            </button>
            <button 
              className="difficulty-btn random"
              onClick={() => startNewGame()}
              disabled={isLoading}
            >
              Random
            </button>
          </div>
        </div>
      )}

      {scrambleData && (
        <div className="scramble-puzzle">
          <div className="puzzle-info">
            <span className="difficulty-badge">{scrambleData.difficulty}</span>
            <span className="word-length">Length: {scrambleData.wordLength} letters</span>
          </div>

          <div className="scrambled-letters">
            {scrambleData.scrambledLetters.split('').map((letter, index) => (
              <div key={index} className="scramble-tile">
                {letter}
              </div>
            ))}
          </div>

          <div className="hint">
            <strong>Hint:</strong> {scrambleData.hint}
          </div>

          <div className="answer-section">
            <input
              type="text"
              className="answer-input"
              value={userAnswer}
              onChange={(e) => setUserAnswer(e.target.value.toUpperCase())}
              onKeyPress={handleKeyPress}
              placeholder="Enter your answer..."
              maxLength={scrambleData.wordLength}
              disabled={isLoading}
            />
            
            <div className="game-controls">
              <button 
                className="control-btn primary"
                onClick={checkAnswer}
                disabled={isLoading || !userAnswer.trim()}
              >
                Check Answer
              </button>
              <button 
                className="control-btn secondary"
                onClick={reshuffleLetters}
                disabled={isLoading}
              >
                Reshuffle Letters
              </button>
              <button 
                className="control-btn danger"
                onClick={giveUp}
                disabled={isLoading}
              >
                Give Up
              </button>
            </div>
          </div>
        </div>
      )}

      {feedback && (
        <div className={`feedback ${feedback.type}`}>
          <p>{feedback.message}</p>
          {feedback.score && (
            <div className="score-display">
              <strong>Points Earned: {feedback.score}</strong>
            </div>
          )}
          <button 
            className="new-game-btn"
            onClick={() => {
              setFeedback(null);
              setUserAnswer('');
            }}
          >
            Play Again
          </button>
        </div>
      )}

      {isLoading && (
        <div className="loading">
          <div className="spinner"></div>
          <p>Loading...</p>
        </div>
      )}
    </div>
  );
};

export default ScrambleGame;