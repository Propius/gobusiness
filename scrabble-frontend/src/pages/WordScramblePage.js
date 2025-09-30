import React, { useState } from 'react';
import ScrambleGame from '../components/ScrambleGame';
import LetterScoringLegend from '../components/LetterScoring/LetterScoringLegend';

function WordScramblePage() {
  const [letterScoringVisible, setLetterScoringVisible] = useState(false);

  const toggleLetterScoring = () => {
    setLetterScoringVisible(!letterScoringVisible);
  };

  return (
    <div className="page-content">
      <header className="page-header">
        <h1>Word Scramble Game</h1>
        <p>Unscramble the letters to form valid words!</p>
      </header>
      
      <main className="game-main">
        <ScrambleGame />
      </main>
      
      <LetterScoringLegend
        isVisible={letterScoringVisible}
        onToggle={toggleLetterScoring}
      />
    </div>
  );
}

export default WordScramblePage;