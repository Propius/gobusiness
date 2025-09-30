import axios from 'axios';
import { getApiUrl, getBaseUrl, getRequestDefaults } from '../config/api.config';

const api = axios.create({
  baseURL: getBaseUrl(),
  ...getRequestDefaults()
});

export const wordFinderService = {
  getStatus: async () => {
    try {
      const response = await api.get(getApiUrl('wordFinder', 'status'));
      return response.data;
    } catch (error) {
      console.error('Error fetching word finder status:', error);
      throw error;
    }
  },
};