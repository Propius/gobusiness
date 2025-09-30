import { getFeatureFlags } from '../configService';
import axios from 'axios';

jest.mock('axios');
const mockedAxios = axios;

describe('configService', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });

  test('getFeatureFlags returns feature flags', async () => {
    const mockResponse = {
      data: {
        scrambleEnabled: true,
        wordFinderEnabled: true,
        boardAnalyzerEnabled: false
      }
    };
    
    mockedAxios.get.mockResolvedValue(mockResponse);
    
    const result = await getFeatureFlags();
    
    expect(result).toEqual(mockResponse.data);
    expect(mockedAxios.get).toHaveBeenCalledWith('/api/scrabble/config');
  });

  test('getFeatureFlags handles error', async () => {
    mockedAxios.get.mockRejectedValue(new Error('Network error'));
    
    await expect(getFeatureFlags()).rejects.toThrow('Network error');
  });
});