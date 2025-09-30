import React, { useState, useEffect } from 'react';
import { getLetterScores } from '../../services/letterScoringService';
import './LetterScoringLegend.css';

const LetterScoringLegend = ({ isVisible, onToggle }) => {
  const [letterScores, setLetterScores] = useState({});
  const [loading, setLoading] = useState(false);
  const [warning, setWarning] = useState(null);
  const [isEnabled, setIsEnabled] = useState(true);

  useEffect(() => {
    if (isVisible) {
      fetchLetterScores();
    }
  }, [isVisible]);

  const fetchLetterScores = async () => {
    try {
      setLoading(true);
      setWarning(null);
      const response = await getLetterScores();

      if (response.letterScores && Object.keys(response.letterScores).length > 0) {
        setLetterScores(response.letterScores);
        setIsEnabled(response.enabled);

        if (!response.enabled || response.error) {
          const sourceMessage = response.source === 'cache'
            ? 'Using cached letter scores'
            : response.source === 'default'
            ? 'Using default letter scores'
            : '';
          setWarning(`${response.message}${sourceMessage ? ' - ' + sourceMessage : ''}`);
        }
      } else {
        setWarning(response.message || 'No letter scores available');
      }
    } catch (err) {
      setWarning('Failed to load letter scores - using defaults');
    } finally {
      setLoading(false);
    }
  };

  const groupLettersByScore = (scores) => {
    const groups = {};
    Object.entries(scores).forEach(([letter, score]) => {
      if (!groups[score]) {
        groups[score] = [];
      }
      groups[score].push(letter);
    });
    return groups;
  };

  const scoreGroups = groupLettersByScore(letterScores);

  if (!isVisible) {
    return (
      <div className="letter-legend-toggle">
        <button 
          className="toggle-button"
          onClick={onToggle}
          title="Show letter scoring legend"
        >
          📊 Letter Values
        </button>
      </div>
    );
  }

  return (
    <div className="letter-legend-container">
      <div className="letter-legend-header">
        <h3>Scrabble Letter Values</h3>
        <button 
          className="close-button"
          onClick={onToggle}
          title="Hide letter scoring legend"
        >
          ✕
        </button>
      </div>
      
      {loading && (
        <div className="legend-loading">
          <div className="spinner"></div>
          Loading letter scores...
        </div>
      )}

      {warning && (
        <div className="legend-warning">
          <span className="warning-icon">⚠️</span>
          {warning}
        </div>
      )}

      {!loading && Object.keys(letterScores).length > 0 && (
        <div className="score-groups">
          {Object.entries(scoreGroups)
            .sort(([a], [b]) => parseInt(a) - parseInt(b))
            .map(([score, letters]) => (
              <div key={score} className="score-group">
                <div className="score-value">{score} point{score !== '1' ? 's' : ''}</div>
                <div className="score-letters">
                  {letters.sort().map(letter => (
                    <span key={letter} className="letter-tile">
                      {letter}
                      <sub>{score}</sub>
                    </span>
                  ))}
                </div>
              </div>
            ))}
        </div>
      )}
      
      <div className="legend-footer">
        <small>Standard Scrabble scoring values</small>
      </div>
    </div>
  );
};

export default LetterScoringLegend;