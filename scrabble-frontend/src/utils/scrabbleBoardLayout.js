// Standard Scrabble board layout with special tile positions
export const createStandardScrabbleBoard = (boardSize = 15) => {
  // Initialize all tiles as normal
  const specialTiles = Array(boardSize * boardSize).fill('normal');
  
  if (boardSize === 15) {
    // Standard 15x15 Scrabble board layout
    
    // Triple Word Score positions
    const tripleWordPositions = [
      [0, 0], [0, 7], [0, 14], [7, 0], [7, 14], [14, 0], [14, 7], [14, 14]
    ];
    
    // Double Word Score positions
    const doubleWordPositions = [
      [1, 1], [2, 2], [3, 3], [4, 4], [7, 7], [10, 10], [11, 11], [12, 12], [13, 13],
      [1, 13], [2, 12], [3, 11], [4, 10], [10, 4], [11, 3], [12, 2], [13, 1]
    ];
    
    // Triple Letter Score positions
    const tripleLetterPositions = [
      [1, 5], [1, 9], [5, 1], [5, 5], [5, 9], [5, 13], [9, 1], [9, 5], [9, 9], [9, 13], [13, 5], [13, 9]
    ];
    
    // Double Letter Score positions
    const doubleLetterPositions = [
      [0, 3], [0, 11], [2, 6], [2, 8], [3, 0], [3, 7], [3, 14], [6, 2], [6, 6], [6, 8], [6, 12],
      [7, 3], [7, 11], [8, 2], [8, 6], [8, 8], [8, 12], [11, 0], [11, 7], [11, 14], [12, 6], [12, 8], [14, 3], [14, 11]
    ];
    
    // Apply special tile positions using frontend tile type names
    tripleWordPositions.forEach(([row, col]) => {
      const index = row * boardSize + col;
      specialTiles[index] = 'tw';
    });
    
    doubleWordPositions.forEach(([row, col]) => {
      const index = row * boardSize + col;
      specialTiles[index] = 'dw';
    });
    
    tripleLetterPositions.forEach(([row, col]) => {
      const index = row * boardSize + col;
      specialTiles[index] = 'tl';
    });
    
    doubleLetterPositions.forEach(([row, col]) => {
      const index = row * boardSize + col;
      specialTiles[index] = 'dl';
    });
    
  } else {
    // For non-standard board sizes, create a simple pattern
    // Place some double letter scores at strategic positions
    for (let i = 2; i < boardSize - 2; i += 4) {
      for (let j = 2; j < boardSize - 2; j += 4) {
        const index = i * boardSize + j;
        specialTiles[index] = 'dl';
      }
    }
    
    // Place triple word at corners if board is large enough
    if (boardSize >= 7) {
      specialTiles[0] = 'tw'; // top-left
      specialTiles[boardSize - 1] = 'tw'; // top-right
      specialTiles[(boardSize - 1) * boardSize] = 'tw'; // bottom-left
      specialTiles[boardSize * boardSize - 1] = 'tw'; // bottom-right
    }
  }
  
  return specialTiles;
};

// Helper function to get special tile display info
export const getSpecialTileInfo = (tileType) => {
  const tileInfo = {
    'normal': { label: '', className: 'normal-tile', color: '#f4f4f4' },
    'double_letter': { label: 'DL', className: 'double-letter-tile', color: '#87ceeb' },
    'triple_letter': { label: 'TL', className: 'triple-letter-tile', color: '#0066cc' },
    'double_word': { label: 'DW', className: 'double-word-tile', color: '#ffb6c1' },
    'triple_word': { label: 'TW', className: 'triple-word-tile', color: '#ff6347' }
  };
  
  return tileInfo[tileType] || tileInfo['normal'];
};

// Helper function to convert 1D index to 2D coordinates
export const indexToCoordinates = (index, boardSize) => {
  const row = Math.floor(index / boardSize);
  const col = index % boardSize;
  return [row, col];
};

// Helper function to convert 2D coordinates to 1D index
export const coordinatesToIndex = (row, col, boardSize) => {
  return row * boardSize + col;
};