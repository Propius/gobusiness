/**
 * Scrabble scoring utility for frontend calculation
 * Matches backend scoring logic exactly (ScrabbleScoreUtil.java)
 */

export const LETTER_SCORES = {
  // 1 point letters
  A: 1, E: 1, I: 1, O: 1, U: 1, L: 1, N: 1, S: 1, T: 1, R: 1,
  // 2 point letters
  D: 2, G: 2,
  // 3 point letters
  B: 3, C: 3, M: 3, P: 3,
  // 4 point letters
  F: 4, H: 4, V: 4, W: 4, Y: 4,
  // 5 point letters
  K: 5,
  // 8 point letters
  J: 8, X: 8,
  // 10 point letters
  Q: 10, Z: 10
};

/**
 * Calculate base Scrabble score for a word (without special tiles)
 * @param {string} word - The word to score (case-insensitive)
 * @returns {number} Total score for all letters in the word
 */
export function calculateWordScore(word) {
  if (!word || typeof word !== 'string') {
    return 0;
  }

  return word
    .toUpperCase()
    .split('')
    .filter(c => /[A-Z]/.test(c))
    .reduce((sum, letter) => sum + (LETTER_SCORES[letter] || 0), 0);
}

/**
 * Calculate Scrabble score for a word with special tiles
 * @param {string} word - The word to score (case-insensitive)
 * @param {Array<string>} specialTiles - Array of special tile types for each letter position
 *        Types: 'normal', 'dl' (double letter), 'tl' (triple letter),
 *               'dw' (double word), 'tw' (triple word)
 * @returns {number} Total score including special tile bonuses
 */
export function calculateScoreWithSpecialTiles(word, specialTiles) {
  if (!word || typeof word !== 'string' || !specialTiles || !Array.isArray(specialTiles)) {
    return calculateWordScore(word);
  }

  const normalizedWord = word.toUpperCase().trim();
  const letters = normalizedWord.split('').filter(c => /[A-Z]/.test(c));

  let baseScore = 0;
  let wordMultiplier = 1;

  letters.forEach((letter, index) => {
    const letterScore = LETTER_SCORES[letter] || 0;
    const tileType = specialTiles[index] || 'normal';

    let finalLetterScore = letterScore;

    // Apply letter multipliers
    if (tileType === 'dl') {
      finalLetterScore *= 2;
    } else if (tileType === 'tl') {
      finalLetterScore *= 3;
    }

    // Track word multipliers (applied after all letters)
    if (tileType === 'dw') {
      wordMultiplier *= 2;
    } else if (tileType === 'tw') {
      wordMultiplier *= 3;
    }

    baseScore += finalLetterScore;
  });

  // Apply word multiplier to final score
  return baseScore * wordMultiplier;
}

/**
 * Get the point value for a single letter
 * @param {string} letter - Single letter (case-insensitive)
 * @returns {number} Point value of the letter, or 0 if not found
 */
export function getLetterScore(letter) {
  if (!letter || typeof letter !== 'string' || letter.length !== 1) {
    return 0;
  }
  return LETTER_SCORES[letter.toUpperCase()] || 0;
}
