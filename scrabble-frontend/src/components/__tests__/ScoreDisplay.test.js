import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import ScoreDisplay from '../ScoreDisplay';

// Mock CSS import
jest.mock('../ScoreDisplay.css', () => {});

describe('ScoreDisplay', () => {
  test('renders score with label', () => {
    render(<ScoreDisplay score={15} />);
    
    expect(screen.getByText('Score:')).toBeInTheDocument();
    expect(screen.getByText('15')).toBeInTheDocument();
  });

  test('renders zero score', () => {
    render(<ScoreDisplay score={0} />);
    
    expect(screen.getByText('Score:')).toBeInTheDocument();
    expect(screen.getByText('0')).toBeInTheDocument();
  });

  test('renders high score', () => {
    render(<ScoreDisplay score={999} />);
    
    expect(screen.getByText('Score:')).toBeInTheDocument();
    expect(screen.getByText('999')).toBeInTheDocument();
  });

  test('has correct CSS classes', () => {
    render(<ScoreDisplay score={42} />);
    
    const scoreDisplay = screen.getByText('Score:').parentElement;
    expect(scoreDisplay).toHaveClass('score-main');
    
    const scoreValue = screen.getByText('42');
    expect(scoreValue).toHaveClass('score-value');
  });

  test('displays validation warning for invalid words', () => {
    render(<ScoreDisplay score={0} isValidWord={false} validationMessage="Invalid word" />);
    
    expect(screen.getByText('Invalid word')).toBeInTheDocument();
    expect(screen.getByText('⚠️')).toBeInTheDocument();
  });

  test('does not display validation warning when word is valid', () => {
    render(<ScoreDisplay score={42} isValidWord={true} />);
    
    expect(screen.queryByText('⚠️')).not.toBeInTheDocument();
  });

  test('does not display validation warning when validationMessage is not provided', () => {
    render(<ScoreDisplay score={0} isValidWord={false} />);
    
    expect(screen.queryByText('⚠️')).not.toBeInTheDocument();
  });

  test('handles negative score', () => {
    render(<ScoreDisplay score={-5} />);
    expect(screen.getByText('-5')).toBeInTheDocument();
  });

  test('handles undefined score', () => {
    render(<ScoreDisplay />);
    // Should render without crashing
    expect(screen.getByText('Score:')).toBeInTheDocument();
  });

  test('validation warning has correct CSS classes', () => {
    render(<ScoreDisplay score={0} isValidWord={false} validationMessage="Test warning" />);
    
    const warning = document.querySelector('.validation-warning');
    expect(warning).toBeInTheDocument();
    
    const icon = document.querySelector('.validation-icon');
    expect(icon).toBeInTheDocument();
    expect(icon).toHaveTextContent('⚠️');
    
    const text = document.querySelector('.validation-text');
    expect(text).toBeInTheDocument();
    expect(text).toHaveTextContent('Test warning');
  });

  test('score display container has correct class', () => {
    render(<ScoreDisplay score={100} />);
    
    const container = document.querySelector('.score-display');
    expect(container).toBeInTheDocument();
  });

  test('handles empty string validation message', () => {
    render(<ScoreDisplay score={0} isValidWord={false} validationMessage="" />);
    
    expect(screen.queryByText('⚠️')).not.toBeInTheDocument();
  });
});