import React from 'react';
import EnhancedTileInput from './SpecialTiles/EnhancedTileInput';
import './TileGrid.css';

function EnhancedTileGrid({ tiles, specialTiles = [], onTileChange, onSpecialTileChange, specialTilesEnabled = false }) {
  return (
    <div className="tile-grid-section">
      <h2>Enter Your Letters</h2>
      <div className="tile-grid">
        {tiles.map((tile, index) => (
          <EnhancedTileInput
            key={index}
            value={tile}
            onChange={onTileChange}
            onSpecialTileChange={onSpecialTileChange}
            index={index}
            specialTileType={specialTiles[index] || 'normal'}
            specialTilesEnabled={specialTilesEnabled}
            className="tile-input"
            dataAttribute="index"
          />
        ))}
      </div>
      {specialTilesEnabled && (
        <p className="special-tiles-help">
          Click the ⚙ button on tiles to set special multipliers (Double/Triple Letter/Word)
        </p>
      )}
    </div>
  );
}

export default EnhancedTileGrid;