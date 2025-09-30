import React from 'react';
import './SpecialTileSelector.css';

const SPECIAL_TILE_TYPES = {
  normal: { id: 'normal', name: 'Normal', color: '#f5f5dc', multiplier: 1, type: 'none' },
  dl: { id: 'dl', name: 'Double Letter', color: '#e6f3ff', multiplier: 2, type: 'letter' },
  tl: { id: 'tl', name: 'Triple Letter', color: '#cce0ff', multiplier: 3, type: 'letter' },
  dw: { id: 'dw', name: 'Double Word', color: '#ffe6f0', multiplier: 2, type: 'word' },
  tw: { id: 'tw', name: 'Triple Word', color: '#ffcccc', multiplier: 3, type: 'word' }
};

function SpecialTileSelector({ selectedType, onTypeChange, position }) {
  return (
    <div className="special-tile-selector">
      <select 
        value={selectedType} 
        onChange={(e) => onTypeChange(position, e.target.value)}
        className="tile-type-select"
      >
        {Object.entries(SPECIAL_TILE_TYPES).map(([key, type]) => (
          <option key={key} value={key}>
            {type.name}
          </option>
        ))}
      </select>
    </div>
  );
}

export { SPECIAL_TILE_TYPES };
export default SpecialTileSelector;