import React from 'react';
import './TopScoresModal.css';

const TopScoresModal = ({ isOpen, onClose, scores, loading, error }) => {
  if (!isOpen) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Top 10 Scores</h2>
          <button className="close-button" onClick={onClose}>×</button>
        </div>
        <div className="modal-body">
          {loading && <p>Loading scores...</p>}
          {error && <p className="error-message">Error loading scores: {error}</p>}
          {!loading && !error && (
            <div className="scores-list">
              {scores.length === 0 ? (
                <p className="no-scores">No scores saved yet!</p>
              ) : (
                <table className="scores-table">
                  <thead>
                    <tr>
                      <th>Rank</th>
                      <th>Word</th>
                      <th>Score</th>
                      <th>Date</th>
                    </tr>
                  </thead>
                  <tbody>
                    {scores.map((score, index) => (
                      <tr key={score.id}>
                        <td>{index + 1}</td>
                        <td className="word-cell">{score.word}</td>
                        <td className="score-cell">{score.points}</td>
                        <td className="date-cell">
                          {new Date(score.createdAt).toLocaleDateString()}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default TopScoresModal;