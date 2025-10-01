import React from 'react';
import './ActionButtons.css';

const ActionButtons = ({ onResetTiles, onSaveScore, onViewTopScores, disabled, isSaving }) => {
  return (
    <div className="action-buttons">
      <button
        className="action-button reset-button"
        onClick={onResetTiles}
        disabled={isSaving}
      >
        Reset Tiles
      </button>
      <button
        className="action-button save-button"
        onClick={onSaveScore}
        disabled={disabled}
      >
        {isSaving ? 'Saving...' : 'Save Score'}
      </button>
      <button
        className="action-button view-button"
        onClick={onViewTopScores}
        disabled={isSaving}
      >
        View Top Scores
      </button>
    </div>
  );
};

export default ActionButtons;