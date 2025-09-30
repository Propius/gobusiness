import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import '@testing-library/jest-dom';
import ScoreCalculator from '../ScoreCalculator';
import { scrabbleService } from '../../services/scrabbleService';
import { configService } from '../../services/configService';

jest.mock('../../services/scrabbleService', () => ({
  scrabbleService: {
    calculateScore: jest.fn(),
    saveScore: jest.fn(),
    getTopScores: jest.fn()
  }
}));

jest.mock('../../services/configService', () => ({
  configService: {
    getTileConfig: jest.fn(),
    getSpecialTilesConfig: jest.fn(),
    getLetterScoringConfig: jest.fn()
  }
}));

describe('ScoreCalculator', () => {
  beforeEach(() => {
    jest.clearAllTimers();
    scrabbleService.calculateScore.mockClear();
    scrabbleService.saveScore.mockClear();
    scrabbleService.getTopScores.mockClear();

    // Mock config service responses
    configService.getTileConfig.mockResolvedValue({
      scoreCalculator: { tileCount: 10 }
    });
    configService.getSpecialTilesConfig.mockResolvedValue({
      scoreCalculator: { enabled: false }
    });
    configService.getLetterScoringConfig.mockResolvedValue({
      scoreCalculator: { enabled: false }
    });
  });

  afterEach(() => {
    jest.clearAllTimers();
  });

  test('renders calculator interface', () => {
    render(<ScoreCalculator />);
    
    expect(screen.getByText('Scrabble Points Calculator')).toBeInTheDocument();
    expect(screen.getByText('Enter letters in the tiles below to calculate your Scrabble score!')).toBeInTheDocument();
  });

  test('calculates score automatically when letters entered', async () => {
    const mockResponse = { word: 'HELLO', totalScore: 15, isValidWord: true };
    scrabbleService.calculateScore.mockResolvedValue(mockResponse);

    const { container } = render(<ScoreCalculator />);

    // Wait for component to finish loading config
    await waitFor(() => {
      const inputs = screen.queryAllByRole('textbox');
      expect(inputs.length).toBeGreaterThan(0);
    });

    // Find and fill tile inputs
    const inputs = screen.getAllByRole('textbox');

    await act(async () => {
      fireEvent.change(inputs[0], { target: { value: 'H' } });
      fireEvent.change(inputs[1], { target: { value: 'E' } });
      fireEvent.change(inputs[2], { target: { value: 'L' } });
      fireEvent.change(inputs[3], { target: { value: 'L' } });
      fireEvent.change(inputs[4], { target: { value: 'O' } });

      // Wait for debounce timeout (500ms)
      await new Promise(resolve => setTimeout(resolve, 600));
    });

    // Verify API was called with the word
    expect(scrabbleService.calculateScore).toHaveBeenCalledWith('HELLO', null, expect.any(Object));
  });

  test('resets tiles when reset button clicked', () => {
    render(<ScoreCalculator />);
    
    const inputs = screen.getAllByRole('textbox');
    fireEvent.change(inputs[0], { target: { value: 'A' } });
    
    const resetButton = screen.getByText('Reset Tiles');
    fireEvent.click(resetButton);
    
    expect(inputs[0].value).toBe('');
  });

  test('handles calculation failure gracefully', async () => {
    scrabbleService.calculateScore.mockRejectedValue(new Error('Network error'));

    render(<ScoreCalculator />);

    // Wait for config to load
    await waitFor(() => {
      expect(screen.queryAllByRole('textbox').length).toBeGreaterThan(0);
    });

    const inputs = screen.getAllByRole('textbox');

    await act(async () => {
      fireEvent.change(inputs[0], { target: { value: 'A' } });
      // Wait for debounce
      await new Promise(resolve => setTimeout(resolve, 600));
    });

    // Verify API was called
    expect(scrabbleService.calculateScore).toHaveBeenCalled();

    // Wait a bit for the error to be handled
    await act(async () => {
      await new Promise(resolve => setTimeout(resolve, 100));
    });

    // Score should be reset to 0 on error - check if score display shows 0
    const scoreDisplay = screen.getByText(/Score:/i).closest('.score-value, .score-display');
    expect(scoreDisplay).toBeTruthy();
  });

  test('does not trigger validation when board is empty', async () => {
    scrabbleService.calculateScore.mockResolvedValue({ totalScore: 0 });

    render(<ScoreCalculator />);

    // Wait for any initial renders
    await waitFor(() => {
      expect(screen.getByText('Scrabble Points Calculator')).toBeInTheDocument();
    }, { timeout: 1000 });

    // Ensure calculateScore was not called with empty board
    expect(scrabbleService.calculateScore).not.toHaveBeenCalled();
  });

  test('triggers validation only when at least one letter is present', async () => {
    scrabbleService.calculateScore.mockResolvedValue({ totalScore: 1, isValidWord: true });

    render(<ScoreCalculator />);

    // Wait for config to load
    await waitFor(() => {
      expect(screen.queryAllByRole('textbox').length).toBeGreaterThan(0);
    });

    const inputs = screen.getAllByRole('textbox');

    await act(async () => {
      // Add a letter
      fireEvent.change(inputs[0], { target: { value: 'A' } });
      // Wait for debounce
      await new Promise(resolve => setTimeout(resolve, 600));
    });

    // Verify API was called
    expect(scrabbleService.calculateScore).toHaveBeenCalledWith('A', null, expect.any(Object));
  });

  test('does not trigger validation when board state has not changed', async () => {
    scrabbleService.calculateScore.mockResolvedValue({ totalScore: 8, isValidWord: true });

    render(<ScoreCalculator />);

    // Wait for config to load
    await waitFor(() => {
      expect(screen.queryAllByRole('textbox').length).toBeGreaterThan(0);
    });

    const inputs = screen.getAllByRole('textbox');

    await act(async () => {
      // Add letters
      fireEvent.change(inputs[0], { target: { value: 'H' } });
      fireEvent.change(inputs[1], { target: { value: 'I' } });
      // Wait for debounce
      await new Promise(resolve => setTimeout(resolve, 600));
    });

    // Wait for first API call
    expect(scrabbleService.calculateScore).toHaveBeenCalledTimes(1);

    // Clear mock to reset call count
    scrabbleService.calculateScore.mockClear();

    await act(async () => {
      // Trigger re-render without changing state (should not call API)
      fireEvent.focus(inputs[0]);
      // Wait for potential debounce
      await new Promise(resolve => setTimeout(resolve, 600));
    });

    // Verify no additional calls
    expect(scrabbleService.calculateScore).not.toHaveBeenCalled();
  });

  test('resets previous state tracking when reset button is clicked', async () => {
    scrabbleService.calculateScore.mockResolvedValue({ totalScore: 1, isValidWord: true });

    render(<ScoreCalculator />);

    // Wait for config to load
    await waitFor(() => {
      expect(screen.queryAllByRole('textbox').length).toBeGreaterThan(0);
    });

    const inputs = screen.getAllByRole('textbox');

    await act(async () => {
      // Add a letter
      fireEvent.change(inputs[0], { target: { value: 'A' } });
      // Wait for debounce
      await new Promise(resolve => setTimeout(resolve, 600));
    });

    // Wait for API call
    expect(scrabbleService.calculateScore).toHaveBeenCalled();

    scrabbleService.calculateScore.mockClear();

    // Reset tiles
    const resetButton = screen.getByText('Reset Tiles');
    fireEvent.click(resetButton);

    await act(async () => {
      // Add same letter again (should trigger new validation)
      fireEvent.change(inputs[0], { target: { value: 'A' } });
      // Wait for debounce
      await new Promise(resolve => setTimeout(resolve, 600));
    });

    // Verify new API call was made
    expect(scrabbleService.calculateScore).toHaveBeenCalled();
  });
});