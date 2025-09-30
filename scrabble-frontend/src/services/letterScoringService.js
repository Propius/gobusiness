import axios from 'axios';
import { getApiUrl, getBaseUrl } from '../config/api.config';

const CACHE_KEY = 'scrabble_letter_scores';

const DEFAULT_LETTER_SCORES = {
  'A': 1, 'B': 3, 'C': 3, 'D': 2, 'E': 1, 'F': 4, 'G': 2, 'H': 4, 'I': 1,
  'J': 8, 'K': 5, 'L': 1, 'M': 3, 'N': 1, 'O': 1, 'P': 3, 'Q': 10, 'R': 1,
  'S': 1, 'T': 1, 'U': 1, 'V': 4, 'W': 4, 'X': 8, 'Y': 4, 'Z': 10
};

export const getLetterScores = async () => {
  try {
    const response = await axios.get(getApiUrl('scrabble', 'letterScores'));
    const data = response.data;

    if (data.enabled && data.letterScores && Object.keys(data.letterScores).length > 0) {
      localStorage.setItem(CACHE_KEY, JSON.stringify(data.letterScores));
      return {
        enabled: true,
        letterScores: data.letterScores,
        message: data.message,
        source: 'api'
      };
    } else if (!data.enabled) {
      const cachedScores = getCachedLetterScores();
      return {
        enabled: false,
        letterScores: cachedScores,
        message: data.message || 'Letter scoring is currently disabled',
        source: cachedScores === DEFAULT_LETTER_SCORES ? 'default' : 'cache'
      };
    }

    return data;
  } catch (error) {
    console.error('Error fetching letter scores:', error);
    const cachedScores = getCachedLetterScores();
    return {
      enabled: false,
      letterScores: cachedScores,
      message: 'Failed to load letter scores from server',
      source: cachedScores === DEFAULT_LETTER_SCORES ? 'default' : 'cache',
      error: true
    };
  }
};

const getCachedLetterScores = () => {
  try {
    const cached = localStorage.getItem(CACHE_KEY);
    if (cached) {
      const parsed = JSON.parse(cached);
      if (parsed && Object.keys(parsed).length === 26) {
        return parsed;
      }
    }
  } catch (error) {
    console.error('Error reading cached letter scores:', error);
  }
  return DEFAULT_LETTER_SCORES;
};