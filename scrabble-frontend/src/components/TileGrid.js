import React from 'react';
import './TileGrid.css';

const TileGrid = ({ tiles, onTileChange, onKeyDown }) => {
  const handleInputChange = (index, value) => {
    const letter = value.toUpperCase().slice(-1);
    onTileChange(index, letter);
  };

  const handleKeyDown = (index, event) => {
    if (event.key === 'Backspace') {
      event.preventDefault(); // Prevent default browser behavior

      // Find the last filled tile from right to left for smooth deletion
      let lastFilledIndex = -1;
      for (let i = tiles.length - 1; i >= 0; i--) {
        if (tiles[i]) {
          lastFilledIndex = i;
          break;
        }
      }

      // If found a filled tile, clear it and move cursor there
      if (lastFilledIndex >= 0) {
        onTileChange(lastFilledIndex, '');
        setTimeout(() => {
          const targetInput = document.querySelector(`input[data-index="${lastFilledIndex}"]`);
          if (targetInput) {
            targetInput.focus();
          }
        }, 10);
      }
    } else if (event.key === 'ArrowLeft' && index > 0) {
      document.querySelector(`input[data-index="${index - 1}"]`).focus();
    } else if (event.key === 'ArrowRight' && index < tiles.length - 1) {
      document.querySelector(`input[data-index="${index + 1}"]`).focus();
    }

    if (onKeyDown) {
      onKeyDown(index, event);
    }
  };

  const handleInputFocus = (event) => {
    event.target.select();
  };

  return (
    <div className="tile-grid">
      {tiles.map((tile, index) => (
        <input
          key={index}
          data-index={index}
          type="text"
          className="tile-input"
          value={tile}
          onChange={(e) => handleInputChange(index, e.target.value)}
          onKeyDown={(e) => handleKeyDown(index, e)}
          onFocus={handleInputFocus}
          maxLength="1"
          autoComplete="off"
        />
      ))}
    </div>
  );
};

export default TileGrid;