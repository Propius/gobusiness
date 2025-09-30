import React, { useState } from 'react';
import SpecialTileSelector, { SPECIAL_TILE_TYPES } from './SpecialTileSelector';
import './SpecialTileSelector.css';

function EnhancedTileInput({ 
  value, 
  onChange, 
  onSpecialTileChange,
  index, 
  specialTileType = 'normal',
  specialTilesEnabled = false, 
  className = '', 
  placeholder = '',
  dataAttribute = 'index' 
}) {
  const [showSelector, setShowSelector] = useState(false);

  const handleSpecialTileChange = (position, type) => {
    if (onSpecialTileChange) {
      onSpecialTileChange(position, type);
    }
    setShowSelector(false);
  };

  const tileType = SPECIAL_TILE_TYPES[specialTileType] || SPECIAL_TILE_TYPES.normal;
  
  return (
    <div className={`tile-input-container ${specialTileType} ${specialTilesEnabled ? 'has-special-tile' : ''}`}>
      <input
        type="text"
        maxLength="1"
        value={value}
        onChange={(e) => onChange(index, e.target.value)}
        className={`tile-input ${className}`}
        placeholder={placeholder}
        data-special-tile={specialTileType}
        data-multiplier={tileType.multiplier}
        data-multiplier-type={tileType.type}
        {...{ [`data-${dataAttribute}`]: index }}
        style={specialTilesEnabled ? { backgroundColor: tileType.color } : {}}
      />
      
      {specialTilesEnabled && specialTileType !== 'normal' && (
        <div className="special-tile-indicator">
          {tileType.multiplier}x{tileType.type === 'letter' ? 'L' : 'W'}
        </div>
      )}
      
      {specialTilesEnabled && (
        <>
          {showSelector && (
            <SpecialTileSelector
              selectedType={specialTileType}
              onTypeChange={handleSpecialTileChange}
              position={index}
            />
          )}
          <button
            type="button"
            className="special-tile-toggle"
            onClick={() => setShowSelector(!showSelector)}
            style={{
              position: 'absolute',
              top: '-2px',
              right: '-2px',
              width: '12px',
              height: '12px',
              fontSize: '8px',
              padding: '0',
              border: 'none',
              borderRadius: '50%',
              backgroundColor: '#007bff',
              color: 'white',
              cursor: 'pointer',
              zIndex: 15
            }}
          >
            ⚙
          </button>
        </>
      )}
    </div>
  );
}

export default EnhancedTileInput;