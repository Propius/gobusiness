import axios from 'axios';
import { scrabbleService } from '../scrabbleService';

jest.mock('axios');
const mockedAxios = axios;

const mockApi = {
  get: jest.fn(),
  post: jest.fn()
};

describe('scrabbleService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockedAxios.create.mockReturnValue(mockApi);
  });

  describe('calculateScore', () => {
    test('should calculate score successfully', async () => {
      const mockResponse = {
        data: {
          word: 'TEST',
          totalScore: 4
        }
      };
      
      mockApi.get.mockResolvedValue(mockResponse);

      const result = await scrabbleService.calculateScore('test');
      
      expect(result).toEqual({ word: 'TEST', totalScore: 4 });
      expect(mockApi.get).toHaveBeenCalledWith('/calculate?word=test');
    });

    test('should calculate score with special tiles', async () => {
      const mockResponse = {
        data: {
          word: 'HELLO',
          totalScore: 16
        }
      };
      
      const specialTilesData = {
        positions: [0, 1],
        specialTiles: ['double_letter', 'triple_word']
      };
      
      mockApi.get.mockResolvedValue(mockResponse);

      const result = await scrabbleService.calculateScore('HELLO', specialTilesData);
      
      expect(result).toEqual(mockResponse.data);
      expect(mockApi.get).toHaveBeenCalledWith(
        '/calculate?word=HELLO&positions=%5B0%2C1%5D&specialTiles=%5B%22double_letter%22%2C%22triple_word%22%5D'
      );
    });

    test('should handle rate limiting with fallback', async () => {
      const mockError = {
        response: { status: 429 }
      };
      
      mockApi.get.mockRejectedValue(mockError);

      const result = await scrabbleService.calculateScore('HELLO');
      
      expect(result.word).toBe('HELLO');
      expect(result.totalScore).toBe(8); // H=4 + E=1 + L=1 + L=1 + O=1
      expect(result.isValidWord).toBe(true);
      expect(result.validationMessage).toBe('Validation temporarily unavailable');
    });

    test('should handle server error with fallback', async () => {
      const mockError = {
        response: { status: 500 }
      };
      
      mockApi.get.mockRejectedValue(mockError);

      const result = await scrabbleService.calculateScore('CAT');
      
      expect(result.word).toBe('CAT');
      expect(result.totalScore).toBe(5); // C=3 + A=1 + T=1
      expect(result.isValidWord).toBe(true);
    });

    test('should handle error when calculating score', async () => {
      const mockError = new Error('Network error');
      
      mockApi.get.mockRejectedValue(mockError);

      await expect(scrabbleService.calculateScore('test')).rejects.toThrow('Network error');
    });

    test('should handle request cancellation', async () => {
      const controller = new AbortController();
      const signal = controller.signal;
      
      mockApi.get.mockRejectedValue({ signal: { aborted: true } });

      await expect(scrabbleService.calculateScore('TEST', null, signal))
        .rejects.toThrow('Request cancelled');
    });
  });

  describe('saveScore', () => {
    test('should save score successfully', async () => {
      const mockResponse = {
        data: {
          id: 1,
          word: 'TEST',
          points: 4,
          createdAt: '2024-01-01T00:00:00Z'
        }
      };
      
      mockApi.post.mockResolvedValue(mockResponse);

      const result = await scrabbleService.saveScore('test');
      
      expect(result).toEqual({
        id: 1,
        word: 'TEST',
        points: 4,
        createdAt: '2024-01-01T00:00:00Z'
      });
      expect(mockApi.post).toHaveBeenCalledWith('/scores', { word: 'test' });
    });

    test('should handle error when saving score', async () => {
      const mockError = new Error('Server error');
      
      mockApi.post.mockRejectedValue(mockError);

      await expect(scrabbleService.saveScore('test')).rejects.toThrow('Server error');
    });
  });

  describe('getTopScores', () => {
    test('should fetch top scores successfully', async () => {
      const mockResponse = {
        data: [
          {
            id: 1,
            word: 'QUIZ',
            points: 22,
            createdAt: '2024-01-01T00:00:00Z'
          },
          {
            id: 2,
            word: 'TEST',
            points: 4,
            createdAt: '2024-01-01T01:00:00Z'
          }
        ]
      };
      
      mockApi.get.mockResolvedValue(mockResponse);

      const result = await scrabbleService.getTopScores();
      
      expect(result).toHaveLength(2);
      expect(result[0]).toEqual({
        id: 1,
        word: 'QUIZ',
        points: 22,
        createdAt: '2024-01-01T00:00:00Z'
      });
      expect(mockApi.get).toHaveBeenCalledWith('/scores/top');
    });

    test('should handle error when fetching top scores', async () => {
      const mockError = new Error('Network error');
      
      mockApi.get.mockRejectedValue(mockError);

      await expect(scrabbleService.getTopScores()).rejects.toThrow('Network error');
    });
  });

  describe('findPossibleWords', () => {
    test('should find possible words successfully', async () => {
      const mockResponse = {
        data: {
          possibleWords: ['CAT', 'DOG', 'COD'],
          wordCount: 3,
          message: 'Found 3 possible words'
        }
      };
      
      const boardTiles = ['C', 'A', 'T'];
      const handTiles = ['D', 'O', 'G', ''];
      
      mockApi.post.mockResolvedValue(mockResponse);

      const result = await scrabbleService.findPossibleWords(boardTiles, handTiles);
      
      expect(result).toEqual(mockResponse.data);
      expect(mockApi.post).toHaveBeenCalledWith('/word-finder', {
        boardTiles: ['C', 'A', 'T'],
        handTiles: ['D', 'O', 'G'] // Empty strings filtered out
      });
    });

    test('should filter empty hand tiles', async () => {
      const mockResponse = {
        data: { possibleWords: [], wordCount: 0, message: 'No words found' }
      };
      
      const boardTiles = ['A'];
      const handTiles = ['B', '', 'C', '   ', 'D'];
      
      mockApi.post.mockResolvedValue(mockResponse);

      await scrabbleService.findPossibleWords(boardTiles, handTiles);
      
      expect(mockApi.post).toHaveBeenCalledWith('/word-finder', {
        boardTiles: ['A'],
        handTiles: ['B', 'C', 'D'] // Empty and whitespace-only strings filtered
      });
    });

    test('should handle find error', async () => {
      const mockError = new Error('Find failed');
      
      mockApi.post.mockRejectedValue(mockError);

      await expect(scrabbleService.findPossibleWords(['A'], ['B']))
        .rejects.toThrow('Find failed');
    });
  });
});