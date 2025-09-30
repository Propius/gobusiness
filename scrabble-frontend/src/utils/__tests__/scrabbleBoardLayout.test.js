import { 
  createStandardScrabbleBoard, 
  SPECIAL_TILE_POSITIONS,
  getSpecialTileAtPosition 
} from '../scrabbleBoardLayout';

describe('scrabbleBoardLayout', () => {
  describe('createStandardScrabbleBoard', () => {
    test('creates 15x15 board by default', () => {
      const board = createStandardScrabbleBoard();
      expect(board).toHaveLength(225); // 15 * 15
    });

    test('creates board with custom size', () => {
      const board = createStandardScrabbleBoard(10);
      expect(board).toHaveLength(100); // 10 * 10
    });

    test('places triple word scores correctly for 15x15 board', () => {
      const board = createStandardScrabbleBoard();
      
      // Check corners for triple word
      expect(board[0]).toBe('triple_word'); // (0,0)
      expect(board[7]).toBe('triple_word'); // (0,7)
      expect(board[14]).toBe('triple_word'); // (0,14)
      expect(board[105]).toBe('triple_word'); // (7,0)
      expect(board[119]).toBe('triple_word'); // (7,14)
      expect(board[210]).toBe('triple_word'); // (14,0)
      expect(board[217]).toBe('triple_word'); // (14,7)
      expect(board[224]).toBe('triple_word'); // (14,14)
    });

    test('places double word scores correctly', () => {
      const board = createStandardScrabbleBoard();
      
      // Check some double word positions
      expect(board[16]).toBe('double_word'); // (1,1)
      expect(board[28]).toBe('double_word'); // (1,13)
      expect(board[112]).toBe('double_word'); // (7,7) - center
      expect(board[196]).toBe('double_word'); // (13,1)
      expect(board[208]).toBe('double_word'); // (13,13)
    });

    test('places triple letter scores correctly', () => {
      const board = createStandardScrabbleBoard();
      
      // Check some triple letter positions
      expect(board[20]).toBe('triple_letter'); // (1,5)
      expect(board[24]).toBe('triple_letter'); // (1,9)
      expect(board[76]).toBe('triple_letter'); // (5,1)
      expect(board[80]).toBe('triple_letter'); // (5,5)
    });

    test('places double letter scores correctly', () => {
      const board = createStandardScrabbleBoard();
      
      // Check some double letter positions
      expect(board[18]).toBe('double_letter'); // (1,3)
      expect(board[26]).toBe('double_letter'); // (1,11)
      expect(board[48]).toBe('double_letter'); // (3,3)
      expect(board[56]).toBe('double_letter'); // (3,11)
    });

    test('fills remaining positions with normal tiles', () => {
      const board = createStandardScrabbleBoard();
      
      // Check that some positions are normal
      expect(board[1]).toBe('normal'); // (0,1)
      expect(board[15]).toBe('normal'); // (1,0)
      expect(board[113]).toBe('normal'); // (7,8)
    });

    test('handles board size 1', () => {
      const board = createStandardScrabbleBoard(1);
      expect(board).toHaveLength(1);
      expect(board[0]).toBe('normal');
    });

    test('handles board size 3', () => {
      const board = createStandardScrabbleBoard(3);
      expect(board).toHaveLength(9); // 3 * 3
      
      // Center should be double_word for small boards
      expect(board[4]).toBe('double_word'); // (1,1) - center of 3x3
    });
  });

  describe('SPECIAL_TILE_POSITIONS', () => {
    test('contains all required tile types', () => {
      expect(SPECIAL_TILE_POSITIONS).toHaveProperty('TRIPLE_WORD');
      expect(SPECIAL_TILE_POSITIONS).toHaveProperty('DOUBLE_WORD');
      expect(SPECIAL_TILE_POSITIONS).toHaveProperty('TRIPLE_LETTER');
      expect(SPECIAL_TILE_POSITIONS).toHaveProperty('DOUBLE_LETTER');
    });

    test('triple word positions are arrays', () => {
      expect(Array.isArray(SPECIAL_TILE_POSITIONS.TRIPLE_WORD)).toBe(true);
      expect(SPECIAL_TILE_POSITIONS.TRIPLE_WORD.length).toBeGreaterThan(0);
    });

    test('double word positions are arrays', () => {
      expect(Array.isArray(SPECIAL_TILE_POSITIONS.DOUBLE_WORD)).toBe(true);
      expect(SPECIAL_TILE_POSITIONS.DOUBLE_WORD.length).toBeGreaterThan(0);
    });

    test('triple letter positions are arrays', () => {
      expect(Array.isArray(SPECIAL_TILE_POSITIONS.TRIPLE_LETTER)).toBe(true);
      expect(SPECIAL_TILE_POSITIONS.TRIPLE_LETTER.length).toBeGreaterThan(0);
    });

    test('double letter positions are arrays', () => {
      expect(Array.isArray(SPECIAL_TILE_POSITIONS.DOUBLE_LETTER)).toBe(true);
      expect(SPECIAL_TILE_POSITIONS.DOUBLE_LETTER.length).toBeGreaterThan(0);
    });

    test('positions are valid coordinates', () => {
      SPECIAL_TILE_POSITIONS.TRIPLE_WORD.forEach(([row, col]) => {
        expect(typeof row).toBe('number');
        expect(typeof col).toBe('number');
        expect(row).toBeGreaterThanOrEqual(0);
        expect(col).toBeGreaterThanOrEqual(0);
        expect(row).toBeLessThan(15);
        expect(col).toBeLessThan(15);
      });
    });
  });

  describe('getSpecialTileAtPosition', () => {
    test('returns correct tile type for known positions', () => {
      expect(getSpecialTileAtPosition(0, 0, 15)).toBe('triple_word');
      expect(getSpecialTileAtPosition(1, 1, 15)).toBe('double_word');
      expect(getSpecialTileAtPosition(1, 5, 15)).toBe('triple_letter');
      expect(getSpecialTileAtPosition(1, 3, 15)).toBe('double_letter');
    });

    test('returns normal for positions without special tiles', () => {
      expect(getSpecialTileAtPosition(0, 1, 15)).toBe('normal');
      expect(getSpecialTileAtPosition(1, 0, 15)).toBe('normal');
      expect(getSpecialTileAtPosition(2, 2, 15)).toBe('normal');
    });

    test('handles different board sizes', () => {
      expect(getSpecialTileAtPosition(0, 0, 10)).toBe('triple_word');
      expect(getSpecialTileAtPosition(5, 5, 10)).toBe('double_word'); // Center of 10x10
    });

    test('handles edge cases', () => {
      expect(getSpecialTileAtPosition(-1, 0, 15)).toBe('normal');
      expect(getSpecialTileAtPosition(0, -1, 15)).toBe('normal');
      expect(getSpecialTileAtPosition(15, 0, 15)).toBe('normal');
      expect(getSpecialTileAtPosition(0, 15, 15)).toBe('normal');
    });

    test('handles board size 1', () => {
      expect(getSpecialTileAtPosition(0, 0, 1)).toBe('normal');
    });
  });

  describe('board symmetry', () => {
    test('board has correct symmetry', () => {
      const board = createStandardScrabbleBoard();
      const size = 15;
      
      // Check horizontal symmetry
      for (let row = 0; row < size; row++) {
        for (let col = 0; col < size / 2; col++) {
          const leftIndex = row * size + col;
          const rightIndex = row * size + (size - 1 - col);
          expect(board[leftIndex]).toBe(board[rightIndex]);
        }
      }
      
      // Check vertical symmetry
      for (let row = 0; row < size / 2; row++) {
        for (let col = 0; col < size; col++) {
          const topIndex = row * size + col;
          const bottomIndex = (size - 1 - row) * size + col;
          expect(board[topIndex]).toBe(board[bottomIndex]);
        }
      }
    });
  });

  describe('performance', () => {
    test('creates board efficiently', () => {
      const start = Date.now();
      const board = createStandardScrabbleBoard();
      const end = Date.now();
      
      expect(board).toHaveLength(225);
      expect(end - start).toBeLessThan(100); // Should complete in less than 100ms
    });

    test('handles large board sizes', () => {
      const board = createStandardScrabbleBoard(20);
      expect(board).toHaveLength(400);
      expect(board.every(tile => 
        ['normal', 'double_letter', 'triple_letter', 'double_word', 'triple_word'].includes(tile)
      )).toBe(true);
    });
  });

  describe('board correctness', () => {
    test('center tile is double word for standard board', () => {
      const board = createStandardScrabbleBoard();
      const centerIndex = 7 * 15 + 7; // (7,7) in 15x15 board
      expect(board[centerIndex]).toBe('double_word');
    });

    test('all positions are filled', () => {
      const board = createStandardScrabbleBoard();
      expect(board.every(tile => tile !== undefined && tile !== null)).toBe(true);
    });

    test('only valid tile types are used', () => {
      const board = createStandardScrabbleBoard();
      const validTypes = ['normal', 'double_letter', 'triple_letter', 'double_word', 'triple_word'];
      expect(board.every(tile => validTypes.includes(tile))).toBe(true);
    });
  });
});