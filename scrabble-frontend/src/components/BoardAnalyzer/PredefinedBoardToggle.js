import React from 'react';
import { createStandardScrabbleBoard } from '../../utils/scrabbleBoardLayout';
import './PredefinedBoardToggle.css';

const PredefinedBoardToggle = ({ isEnabled, onToggle, onApplyStandardBoard, boardSize = 15 }) => {
  
  const handleToggle = (e) => {
    const enabled = e.target.checked;
    if (enabled) {
      // Auto-apply standard board when enabled
      const standardBoard = createStandardScrabbleBoard(boardSize);
      onApplyStandardBoard(standardBoard);
    } else {
      // Auto-remove standard board when disabled (reset to normal)
      const normalBoard = Array(boardSize * boardSize).fill('normal');
      onApplyStandardBoard(normalBoard);
    }
    onToggle();
  };

  return (
    <div className="predefined-board-toggle">
      <div className="toggle-container">
        <label className="toggle-label">
          <input
            type="checkbox"
            checked={isEnabled}
            onChange={handleToggle}
            className="toggle-checkbox"
          />
          <span className="toggle-slider"></span>
          Use Standard Scrabble Board Layout
        </label>
      </div>
      
      {isEnabled && (
        <div className="board-info">
          <small>
            ✓ Standard 15×15 Scrabble board applied with:<br/>
            • Triple Word (TW) at corners and center edges<br/>
            • Double Word (DW) in diagonal patterns<br/>
            • Triple Letter (TL) and Double Letter (DL) scattered
          </small>
        </div>
      )}
    </div>
  );
};

export default PredefinedBoardToggle;