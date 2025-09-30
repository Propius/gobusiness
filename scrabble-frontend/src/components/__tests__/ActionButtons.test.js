import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import ActionButtons from '../ActionButtons';

describe('ActionButtons', () => {
  const mockProps = {
    onResetTiles: jest.fn(),
    onSaveScore: jest.fn(),
    onViewTopScores: jest.fn(),
    disabled: false,
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders all three buttons', () => {
    render(<ActionButtons {...mockProps} />);
    
    expect(screen.getByText('Reset Tiles')).toBeInTheDocument();
    expect(screen.getByText('Save Score')).toBeInTheDocument();
    expect(screen.getByText('View Top Scores')).toBeInTheDocument();
  });

  test('calls onResetTiles when Reset Tiles button is clicked', () => {
    render(<ActionButtons {...mockProps} />);
    
    const resetButton = screen.getByText('Reset Tiles');
    fireEvent.click(resetButton);
    
    expect(mockProps.onResetTiles).toHaveBeenCalledTimes(1);
  });

  test('calls onSaveScore when Save Score button is clicked', () => {
    render(<ActionButtons {...mockProps} />);
    
    const saveButton = screen.getByText('Save Score');
    fireEvent.click(saveButton);
    
    expect(mockProps.onSaveScore).toHaveBeenCalledTimes(1);
  });

  test('calls onViewTopScores when View Top Scores button is clicked', () => {
    render(<ActionButtons {...mockProps} />);
    
    const viewButton = screen.getByText('View Top Scores');
    fireEvent.click(viewButton);
    
    expect(mockProps.onViewTopScores).toHaveBeenCalledTimes(1);
  });

  test('disables Save Score button when disabled prop is true', () => {
    render(<ActionButtons {...mockProps} disabled={true} />);
    
    const saveButton = screen.getByText('Save Score');
    expect(saveButton).toBeDisabled();
  });

  test('enables Save Score button when disabled prop is false', () => {
    render(<ActionButtons {...mockProps} disabled={false} />);
    
    const saveButton = screen.getByText('Save Score');
    expect(saveButton).not.toBeDisabled();
  });

  test('does not call onSaveScore when Save Score button is disabled and clicked', () => {
    render(<ActionButtons {...mockProps} disabled={true} />);
    
    const saveButton = screen.getByText('Save Score');
    fireEvent.click(saveButton);
    
    expect(mockProps.onSaveScore).not.toHaveBeenCalled();
  });

  test('Reset Tiles and View Top Scores buttons are never disabled', () => {
    render(<ActionButtons {...mockProps} disabled={true} />);
    
    const resetButton = screen.getByText('Reset Tiles');
    const viewButton = screen.getByText('View Top Scores');
    
    expect(resetButton).not.toBeDisabled();
    expect(viewButton).not.toBeDisabled();
  });
});