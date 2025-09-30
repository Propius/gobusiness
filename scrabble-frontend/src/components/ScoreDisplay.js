import React from 'react';
import './ScoreDisplay.css';

const ScoreDisplay = ({ score, isValidWord, validationMessage }) => {
  return (
    <div className="score-display">
      <div className="score-main">
        <span className="score-label">Score:</span>
        <span className="score-value">{score}</span>
      </div>
      {isValidWord === false && validationMessage && (
        <div className="validation-warning">
          <span className="validation-icon">⚠️</span>
          <span className="validation-text">{validationMessage}</span>
        </div>
      )}
    </div>
  );
};

export default ScoreDisplay;