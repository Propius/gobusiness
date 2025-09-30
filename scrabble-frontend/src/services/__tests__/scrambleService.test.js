import axios from 'axios';

// Mock axios completely
jest.mock('axios', () => ({
  create: jest.fn()
}));

const mockApi = {
  get: jest.fn(),
  post: jest.fn(),
  delete: jest.fn()
};

// Make axios.create return our mock
const mockedAxios = axios;
mockedAxios.create.mockReturnValue(mockApi);

// Import the service after mocking
const { scrambleService } = require('../scrambleService');

describe('scrambleService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    // Reset the mock to return our mockApi
    mockedAxios.create.mockReturnValue(mockApi);
  });

  describe('generateNewScramble', () => {
    test('should generate scramble without difficulty', async () => {
      const mockResponse = {
        data: {
          scrambledLetters: 'LLEOH',
          originalWord: 'session123',
          availableLetters: ['H', 'E', 'L', 'L', 'O'],
          wordLength: 5,
          difficulty: 'easy',
          hint: 'A common greeting'
        }
      };
      
      mockApi.get.mockResolvedValue(mockResponse);

      const result = await scrambleService.generateNewScramble();
      
      expect(result).toEqual(mockResponse.data);
      expect(mockApi.get).toHaveBeenCalledWith('/new');
    });

    test('should generate scramble with difficulty', async () => {
      const mockResponse = {
        data: {
          scrambledLetters: 'DIUMEM',
          originalWord: 'session456',
          availableLetters: ['M', 'E', 'D', 'I', 'U', 'M'],
          wordLength: 6,
          difficulty: 'medium',
          hint: 'A common 6-letter word'
        }
      };
      
      mockApi.get.mockResolvedValue(mockResponse);

      const result = await scrambleService.generateNewScramble('medium');
      
      expect(result).toEqual(mockResponse.data);
      expect(mockApi.get).toHaveBeenCalledWith('/new?difficulty=medium');
    });

    test('should handle error when generating scramble', async () => {
      const mockError = new Error('Service unavailable');
      
      mockApi.get.mockRejectedValue(mockError);

      await expect(scrambleService.generateNewScramble()).rejects.toThrow('Service unavailable');
    });
  });

  describe('reshuffleScramble', () => {
    test('should reshuffle scramble successfully', async () => {
      const mockResponse = {
        data: {
          scrambledLetters: 'OHELL',
          originalWord: 'session123',
          availableLetters: ['H', 'E', 'L', 'L', 'O'],
          wordLength: 5,
          difficulty: 'easy',
          hint: 'A common greeting'
        }
      };
      
      mockApi.post.mockResolvedValue(mockResponse);

      const result = await scrambleService.reshuffleScramble('session123');
      
      expect(result).toEqual(mockResponse.data);
      expect(mockApi.post).toHaveBeenCalledWith('/session123/reshuffle');
    });

    test('should handle error when reshuffling', async () => {
      const mockError = new Error('Invalid session');
      
      mockApi.post.mockRejectedValue(mockError);

      await expect(scrambleService.reshuffleScramble('invalid')).rejects.toThrow('Invalid session');
    });
  });

  describe('checkAnswer', () => {
    test('should check correct answer', async () => {
      const mockResponse = {
        data: {
          isCorrect: true,
          userAnswer: 'HELLO',
          correctAnswer: 'HELLO',
          score: 8,
          message: 'Congratulations! You unscrambled the word correctly!'
        }
      };
      
      mockApi.post.mockResolvedValue(mockResponse);

      const result = await scrambleService.checkAnswer('session123', 'HELLO');
      
      expect(result).toEqual(mockResponse.data);
      expect(mockApi.post).toHaveBeenCalledWith('/session123/check', { answer: 'HELLO' });
    });

    test('should check incorrect answer', async () => {
      const mockResponse = {
        data: {
          isCorrect: false,
          userAnswer: 'WORLD',
          correctAnswer: 'HELLO',
          score: 0,
          message: 'Incorrect! Try again or get a new scramble.'
        }
      };
      
      mockApi.post.mockResolvedValue(mockResponse);

      const result = await scrambleService.checkAnswer('session123', 'WORLD');
      
      expect(result).toEqual(mockResponse.data);
      expect(mockApi.post).toHaveBeenCalledWith('/session123/check', { answer: 'WORLD' });
    });

    test('should handle error when checking answer', async () => {
      const mockError = new Error('Network error');
      
      mockApi.post.mockRejectedValue(mockError);

      await expect(scrambleService.checkAnswer('session123', 'HELLO')).rejects.toThrow('Network error');
    });
  });

  describe('clearSession', () => {
    test('should clear session successfully', async () => {
      mockApi.delete.mockResolvedValue({});

      await expect(scrambleService.clearSession('session123')).resolves.toBeUndefined();
      expect(mockApi.delete).toHaveBeenCalledWith('/session123');
    });

    test('should handle error when clearing session', async () => {
      const mockError = new Error('Session not found');
      
      mockApi.delete.mockRejectedValue(mockError);

      await expect(scrambleService.clearSession('invalid')).rejects.toThrow('Session not found');
    });
  });

  describe('getStats', () => {
    test('should get stats successfully', async () => {
      const mockResponse = {
        data: {
          activeSessionsCount: 5,
          featureEnabled: true
        }
      };
      
      mockApi.get.mockResolvedValue(mockResponse);

      const result = await scrambleService.getStats();
      
      expect(result).toEqual(mockResponse.data);
      expect(mockApi.get).toHaveBeenCalledWith('/stats');
    });

    test('should handle error when getting stats', async () => {
      const mockError = new Error('Service unavailable');
      
      mockApi.get.mockRejectedValue(mockError);

      await expect(scrambleService.getStats()).rejects.toThrow('Service unavailable');
    });
  });
});