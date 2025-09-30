import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import TileGrid from '../TileGrid';

// Mock CSS import
jest.mock('../TileGrid.css', () => {});

describe('TileGrid', () => {
  const defaultProps = {
    tiles: Array(10).fill(''),
    onTileChange: jest.fn(),
    onKeyDown: jest.fn(),
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders 10 empty tiles', () => {
    render(<TileGrid {...defaultProps} />);
    
    const inputs = screen.getAllByRole('textbox');
    expect(inputs).toHaveLength(10);
    
    inputs.forEach(input => {
      expect(input).toHaveValue('');
    });
  });

  test('renders tiles with provided values', () => {
    const tilesWithValues = ['T', 'E', 'S', 'T', '', '', '', '', '', ''];
    render(<TileGrid {...defaultProps} tiles={tilesWithValues} />);
    
    const inputs = screen.getAllByRole('textbox');
    expect(inputs[0]).toHaveValue('T');
    expect(inputs[1]).toHaveValue('E');
    expect(inputs[2]).toHaveValue('S');
    expect(inputs[3]).toHaveValue('T');
    expect(inputs[4]).toHaveValue('');
  });

  test('calls onTileChange when input value changes', () => {
    render(<TileGrid {...defaultProps} />);
    
    const firstInput = screen.getAllByRole('textbox')[0];
    fireEvent.change(firstInput, { target: { value: 'A' } });
    
    expect(defaultProps.onTileChange).toHaveBeenCalledWith(0, 'A');
  });

  test('converts lowercase to uppercase', () => {
    render(<TileGrid {...defaultProps} />);
    
    const firstInput = screen.getAllByRole('textbox')[0];
    fireEvent.change(firstInput, { target: { value: 'a' } });
    
    expect(defaultProps.onTileChange).toHaveBeenCalledWith(0, 'A');
  });

  test('handles multiple characters by taking the last one', () => {
    render(<TileGrid {...defaultProps} />);
    
    const firstInput = screen.getAllByRole('textbox')[0];
    fireEvent.change(firstInput, { target: { value: 'ABC' } });
    
    expect(defaultProps.onTileChange).toHaveBeenCalledWith(0, 'C');
  });

  test('handles arrow key navigation', () => {
    render(<TileGrid {...defaultProps} />);
    
    const inputs = screen.getAllByRole('textbox');
    const firstInput = inputs[0];
    const secondInput = inputs[1];
    
    firstInput.focus();
    fireEvent.keyDown(firstInput, { key: 'ArrowRight' });
    
    expect(document.activeElement).toBe(secondInput);
  });

  test('handles backspace navigation', () => {
    const tilesWithValues = ['T', '', '', '', '', '', '', '', '', ''];
    render(<TileGrid {...defaultProps} tiles={tilesWithValues} />);
    
    const inputs = screen.getAllByRole('textbox');
    const secondInput = inputs[1];
    
    secondInput.focus();
    fireEvent.keyDown(secondInput, { key: 'Backspace' });
    
    expect(defaultProps.onTileChange).toHaveBeenCalledWith(0, '');
  });

  test('selects input text on focus', () => {
    const tilesWithValues = ['A', '', '', '', '', '', '', '', '', ''];
    render(<TileGrid {...defaultProps} tiles={tilesWithValues} />);
    
    const firstInput = screen.getAllByRole('textbox')[0];
    const selectSpy = jest.spyOn(firstInput, 'select');
    
    fireEvent.focus(firstInput);
    
    expect(selectSpy).toHaveBeenCalled();
  });

  test('calls onKeyDown prop when provided', () => {
    render(<TileGrid {...defaultProps} />);
    
    const firstInput = screen.getAllByRole('textbox')[0];
    fireEvent.keyDown(firstInput, { key: 'Enter' });
    
    expect(defaultProps.onKeyDown).toHaveBeenCalledWith(0, expect.any(Object));
  });

  test('works without onKeyDown prop', () => {
    const propsWithoutKeyDown = {
      tiles: Array(5).fill(''),
      onTileChange: jest.fn(),
    };
    
    render(<TileGrid {...propsWithoutKeyDown} />);
    
    const firstInput = screen.getAllByRole('textbox')[0];
    fireEvent.keyDown(firstInput, { key: 'Enter' });
    
    // Should not throw error
    expect(firstInput).toBeInTheDocument();
  });

  test('has correct attributes', () => {
    render(<TileGrid {...defaultProps} />);
    
    const inputs = screen.getAllByRole('textbox');
    inputs.forEach((input, index) => {
      expect(input).toHaveAttribute('maxLength', '1');
      expect(input).toHaveAttribute('autoComplete', 'off');
      expect(input).toHaveAttribute('data-index', index.toString());
      expect(input).toHaveClass('tile-input');
    });
  });

  test('handles empty string input', () => {
    render(<TileGrid {...defaultProps} />);
    
    const firstInput = screen.getAllByRole('textbox')[0];
    fireEvent.change(firstInput, { target: { value: '' } });
    
    expect(defaultProps.onTileChange).toHaveBeenCalledWith(0, '');
  });

  test('handles boundary navigation correctly', () => {
    render(<TileGrid {...defaultProps} />);
    
    const inputs = screen.getAllByRole('textbox');
    const firstInput = inputs[0];
    const lastInput = inputs[inputs.length - 1];
    
    // Try to navigate left from first input
    firstInput.focus();
    fireEvent.keyDown(firstInput, { key: 'ArrowLeft' });
    expect(document.activeElement).toBe(firstInput);
    
    // Try to navigate right from last input
    lastInput.focus();
    fireEvent.keyDown(lastInput, { key: 'ArrowRight' });
    expect(document.activeElement).toBe(lastInput);
  });

  test('backspace does not navigate from first position with empty tile', () => {
    const tiles = ['', 'A', '', '', '', '', '', '', '', ''];
    render(<TileGrid {...defaultProps} tiles={tiles} />);
    
    const firstInput = screen.getAllByRole('textbox')[0];
    firstInput.focus();
    fireEvent.keyDown(firstInput, { key: 'Backspace' });
    
    // Should stay focused on first input
    expect(document.activeElement).toBe(firstInput);
  });
});