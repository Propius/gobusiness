import React from 'react';
import './TileGrid.css';

const TileGrid = ({ tiles, onTileChange, onKeyDown }) => {
  const handleInputChange = (index, value) => {
    const letter = value.toUpperCase().slice(-1);
    onTileChange(index, letter);
  };

  const handleKeyDown = (index, event) => {
    if (event.key === 'Backspace' && !tiles[index] && index > 0) {
      const prevIndex = index - 1;
      document.querySelector(`input[data-index="${prevIndex}"]`).focus();
      onTileChange(prevIndex, '');
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