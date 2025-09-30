import axios from 'axios';
import { getApiUrl, getBaseUrl, getRequestDefaults } from '../config/api.config';

const api = axios.create({
  baseURL: getBaseUrl(),
  ...getRequestDefaults()
});

export const configService = {
  getTileConfig: async () => {
    try {
      const response = await api.get(getApiUrl('config', 'tiles'));
      return response.data;
    } catch (error) {
      console.error('Error fetching tile config:', error);
      throw error;
    }
  },

  getSpecialTilesConfig: async () => {
    try {
      const response = await api.get(getApiUrl('config', 'specialTiles'));
      return response.data;
    } catch (error) {
      console.error('Error fetching special tiles config:', error);
      throw error;
    }
  },

  getScrambleConfig: async () => {
    try {
      const response = await api.get(getApiUrl('config', 'scramble'));
      return response.data;
    } catch (error) {
      console.error('Error fetching scramble config:', error);
      throw error;
    }
  },

  getLetterScoringConfig: async () => {
    try {
      const response = await api.get(getApiUrl('config', 'letterScoring'));
      return response.data;
    } catch (error) {
      console.error('Error fetching letter scoring config:', error);
      throw error;
    }
  },
};