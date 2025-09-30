import axios from 'axios';
import { getApiUrl, getBaseUrl, getRequestDefaults } from '../config/api.config';

const api = axios.create({
  baseURL: getBaseUrl(),
  ...getRequestDefaults()
});

export const scrambleService = {
  generateNewScramble: async (difficulty = null) => {
    try {
      const url = difficulty
        ? `${getApiUrl('scramble', 'new')}?difficulty=${difficulty}`
        : getApiUrl('scramble', 'new');
      const response = await api.get(url);
      return response.data;
    } catch (error) {
      console.error('Error generating new scramble:', error);
      throw error;
    }
  },

  reshuffleScramble: async (sessionId) => {
    try {
      const response = await api.post(getApiUrl('scramble', 'reshuffle', { sessionId }));
      return response.data;
    } catch (error) {
      console.error('Error reshuffling scramble:', error);
      throw error;
    }
  },

  checkAnswer: async (sessionId, answer) => {
    try {
      const response = await api.post(getApiUrl('scramble', 'check', { sessionId }), { answer });
      return response.data;
    } catch (error) {
      console.error('Error checking answer:', error);
      throw error;
    }
  },

  clearSession: async (sessionId) => {
    try {
      await api.delete(getApiUrl('scramble', 'delete', { sessionId }));
    } catch (error) {
      console.error('Error clearing session:', error);
      throw error;
    }
  },

  getStats: async () => {
    try {
      const response = await api.get(getApiUrl('scramble', 'stats'));
      return response.data;
    } catch (error) {
      console.error('Error fetching scramble stats:', error);
      throw error;
    }
  },
};