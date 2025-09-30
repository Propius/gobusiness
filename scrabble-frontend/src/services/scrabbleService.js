import axios from 'axios';
import { getApiUrl, getBaseUrl, getRequestDefaults } from '../config/api.config';

// Create axios instance with centralized configuration
const api = axios.create({
  baseURL: getBaseUrl(),
  ...getRequestDefaults()
});

export const scrabbleService = {
  calculateScore: async (word, specialTilesData = null, signal = null) => {
    try {
      const config = signal ? { signal } : {};
      
      // Build request body for POST request
      const requestBody = {
        word: word
      };
      
      // Add special tiles data if provided
      if (specialTilesData && specialTilesData.positions && specialTilesData.specialTiles) {
        requestBody.positions = specialTilesData.positions;
        requestBody.specialTiles = specialTilesData.specialTiles;
      }
      
      const response = await api.post(getApiUrl('scrabble', 'calculatePost'), requestBody, config);
      return response.data;
    } catch (error) {
      if (signal && signal.aborted) {
        // Don't log cancelled requests as errors
        throw new Error('Request cancelled');
      }
      
      // Handle rate limiting and server errors gracefully
      if (error.response?.status === 429 || error.response?.status >= 500) {
        console.warn('Server temporarily unavailable, using fallback response');
        // Return a fallback response for rate limiting
        return {
          word: word?.toUpperCase() || '',
          totalScore: word ? word.split('').reduce((score, char) => {
            // Simple fallback scoring without dictionary validation
            const letterScores = {
              'A': 1, 'E': 1, 'I': 1, 'O': 1, 'U': 1, 'L': 1, 'N': 1, 'S': 1, 'T': 1, 'R': 1,
              'D': 2, 'G': 2,
              'B': 3, 'C': 3, 'M': 3, 'P': 3,
              'F': 4, 'H': 4, 'V': 4, 'W': 4, 'Y': 4,
              'K': 5,
              'J': 8, 'X': 8,
              'Q': 10, 'Z': 10
            };
            return score + (letterScores[char?.toUpperCase()] || 0);
          }, 0) : 0,
          isValidWord: true, // Assume valid when server is unavailable
          validationMessage: error.response?.status === 429 ? 'Validation temporarily unavailable' : null
        };
      }
      
      console.error('Error calculating score:', error);
      throw error;
    }
  },

  saveScore: async (word, enhancedScore = null, positions = null, specialTiles = null) => {
    try {
      const requestBody = { word };

      // Include enhanced score and special tiles data if provided
      if (enhancedScore !== null && enhancedScore !== undefined) {
        requestBody.enhancedScore = enhancedScore;
      }

      if (positions !== null && positions !== undefined) {
        requestBody.positions = positions;
      }

      if (specialTiles !== null && specialTiles !== undefined) {
        requestBody.specialTiles = specialTiles;
      }

      const response = await api.post(getApiUrl('scrabble', 'scores'), requestBody);
      return response.data;
    } catch (error) {
      console.error('Error saving score:', error);
      throw error;
    }
  },

  getTopScores: async () => {
    try {
      const response = await api.get(getApiUrl('scrabble', 'topScores'));
      return response.data;
    } catch (error) {
      console.error('Error fetching top scores:', error);
      throw error;
    }
  },

  findPossibleWords: async (boardTiles, handTiles) => {
    try {
      const request = {
        boardTiles: boardTiles,
        handTiles: handTiles.filter(tile => tile.trim() !== '')
      };
      const response = await api.post(getApiUrl('scrabble', 'wordFinder'), request);
      return response.data;
    } catch (error) {
      console.error('Error finding possible words:', error);
      throw error;
    }
  },
};