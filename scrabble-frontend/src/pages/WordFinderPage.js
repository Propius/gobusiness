import React, { useState, useEffect } from 'react';
import WordFinder from '../components/WordFinder';
import LetterScoringLegend from '../components/LetterScoring/LetterScoringLegend';
import { configService } from '../services/configService';

function WordFinderPage() {
  const [letterScoringVisible, setLetterScoringVisible] = useState(false);
  const [letterScoringEnabled, setLetterScoringEnabled] = useState(false);

  useEffect(() => {
    const loadConfig = async () => {
      try {
        const letterScoringConfig = await configService.getLetterScoringConfig();
        setLetterScoringEnabled(letterScoringConfig.wordFinder.enabled);
      } catch (error) {
        console.error('Error loading config:', error);
        setLetterScoringEnabled(false);
      }
    };

    loadConfig();
  }, []);

  const toggleLetterScoring = () => {
    setLetterScoringVisible(!letterScoringVisible);
  };

  return (
    <div className="page-content">
      <header className="page-header">
        <h1>Word Finder</h1>
        <p>Find possible words using board tiles and your hand!</p>
      </header>
      
      <main className="finder-main">
        <WordFinder />
      </main>
      
      {letterScoringEnabled && (
        <LetterScoringLegend
          isVisible={letterScoringVisible}
          onToggle={toggleLetterScoring}
        />
      )}
    </div>
  );
}

export default WordFinderPage;