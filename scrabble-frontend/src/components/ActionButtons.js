import React from 'react';
import './ActionButtons.css';

const ActionButtons = ({ onResetTiles, onSaveScore, onViewTopScores, disabled }) => {
  return (
    <div className="action-buttons">
      <button 
        className="action-button reset-button"
        onClick={onResetTiles}
      >
        Reset Tiles
      </button>
      <button 
        className="action-button save-button"
        onClick={onSaveScore}
        disabled={disabled}
      >
        Save Score
      </button>
      <button 
        className="action-button view-button"
        onClick={onViewTopScores}
      >
        View Top Scores
      </button>
    </div>
  );
};

export default ActionButtons;