/**
 * Centralized API Configuration
 *
 * This file manages all backend API endpoints in a single location.
 * Update API_BASE_URL to point to your backend server.
 */

// Base API URL - can be overridden by environment variables
const API_BASE_URL = process.env.REACT_APP_API_URL || 'https://my-scrabble-api.loca.lt';

/**
 * API Configuration Object
 */
const apiConfig = {
  // Base URL for all API calls
  baseURL: API_BASE_URL,

  // API Endpoints organized by feature
  endpoints: {
    // Scrabble Calculator API
    scrabble: {
      calculate: '/api/scrabble/calculate',
      calculatePost: '/api/scrabble/calculate',
      scores: '/api/scrabble/scores',
      topScores: '/api/scrabble/scores/top',
      wordFinder: '/api/scrabble/word-finder',
      letterScores: '/api/scrabble/letter-scores',
      validate: '/api/scrabble/validate'
    },

    // Configuration API
    config: {
      tiles: '/api/config/tiles',
      specialTiles: '/api/config/special-tiles',
      scramble: '/api/config/scramble',
      letterScoring: '/api/config/letter-scoring'
    },

    // Word Scramble API
    scramble: {
      new: '/api/scramble/new',
      check: '/api/scramble/{sessionId}/check',
      reshuffle: '/api/scramble/{sessionId}/reshuffle',
      delete: '/api/scramble/{sessionId}',
      stats: '/api/scramble/stats'
    },

    // Word Finder API
    wordFinder: {
      status: '/api/word-finder/status'
    },

    // Board Analyzer API
    boardAnalyzer: {
      analyze: '/api/board-analyzer/analyze',
      status: '/api/board-analyzer/status',
      validate: '/api/board-analyzer/validate'
    },

    // Dictionary API (future implementation)
    dictionary: {
      validate: '/api/dictionary/validate',
      validateBatch: '/api/dictionary/validate/batch',
      sources: '/api/dictionary/sources'
    },

    // Word Generation API (future implementation)
    wordGen: {
      sampling: '/api/wordgen/sampling',
      exhaustive: '/api/wordgen/exhaustive',
      config: '/api/wordgen/config'
    }
  },

  // Request configuration defaults
  defaults: {
    timeout: 10000, // 10 seconds
    headers: {
      'Content-Type': 'application/json'
    }
  }
};

/**
 * Helper function to build full URL
 * @param {string} endpoint - Endpoint path from apiConfig.endpoints
 * @param {Object} params - Optional path parameters to replace in endpoint
 * @returns {string} Full URL
 */
export const buildUrl = (endpoint, params = {}) => {
  let url = `${apiConfig.baseURL}${endpoint}`;

  // Replace path parameters (e.g., {sessionId})
  Object.keys(params).forEach(key => {
    url = url.replace(`{${key}}`, params[key]);
  });

  return url;
};

/**
 * Get full URL for a specific endpoint
 * @param {string} feature - Feature name (e.g., 'scrabble', 'config')
 * @param {string} action - Action name (e.g., 'calculate', 'tiles')
 * @param {Object} params - Optional path parameters
 * @returns {string} Full URL
 */
export const getApiUrl = (feature, action, params = {}) => {
  const endpoint = apiConfig.endpoints[feature]?.[action];
  if (!endpoint) {
    throw new Error(`API endpoint not found: ${feature}.${action}`);
  }
  return buildUrl(endpoint, params);
};

/**
 * Get base URL
 * @returns {string} Base API URL
 */
export const getBaseUrl = () => apiConfig.baseURL;

/**
 * Get request defaults (timeout, headers)
 * @returns {Object} Default request configuration
 */
export const getRequestDefaults = () => apiConfig.defaults;

// Export the complete configuration for advanced usage
export default apiConfig;