import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import SideNav from './components/Navigation/SideNav';
import ScoreCalculator from './pages/ScoreCalculator';
import WordScramblePage from './pages/WordScramblePage';
import WordFinderPage from './pages/WordFinderPage';
import BoardAnalyzerPage from './pages/BoardAnalyzerPage';
import { scrambleService } from './services/scrambleService';
import { wordFinderService } from './services/wordFinderService';
import { getApiUrl } from './config/api.config';
import axios from 'axios';
import './App.css';
import './pages/Layout.css';

function App() {
  const [isScrambleEnabled, setIsScrambleEnabled] = useState(false);
  const [isWordFinderEnabled, setIsWordFinderEnabled] = useState(false);
  const [isBoardAnalyzerEnabled, setIsBoardAnalyzerEnabled] = useState(false);
  const [isNavCollapsed, setIsNavCollapsed] = useState(false);


  // Check if features are enabled
  useEffect(() => {
    const checkFeatures = async () => {
      try {
        // Check scramble feature
        const scrambleStats = await scrambleService.getStats();
        setIsScrambleEnabled(scrambleStats.featureEnabled);
      } catch (error) {
        console.error('Error checking scramble feature status:', error);
        setIsScrambleEnabled(false);
      }

      try {
        // Check word finder feature
        const wordFinderStats = await wordFinderService.getStatus();
        setIsWordFinderEnabled(wordFinderStats.featureEnabled);
      } catch (error) {
        console.error('Error checking word finder feature status:', error);
        setIsWordFinderEnabled(false);
      }
      
      try {
        // Check board analyzer feature
        const boardAnalyzerResponse = await axios.get(getApiUrl('boardAnalyzer', 'status'));
        setIsBoardAnalyzerEnabled(boardAnalyzerResponse.data.enabled);
      } catch (error) {
        console.error('Error checking board analyzer feature status:', error);
        setIsBoardAnalyzerEnabled(false);
      }
    };

    checkFeatures();
  }, []);


  return (
    <Router>
      <div className="app-layout">
        <SideNav 
          isScrambleEnabled={isScrambleEnabled} 
          isWordFinderEnabled={isWordFinderEnabled}
          isBoardAnalyzerEnabled={isBoardAnalyzerEnabled}
        />
        
        <main className={`main-content ${isNavCollapsed ? 'nav-collapsed' : ''}`}>
          <Routes>
            <Route path="/" element={<ScoreCalculator />} />
            {isScrambleEnabled && (
              <Route path="/scramble" element={<WordScramblePage />} />
            )}
            {isWordFinderEnabled && (
              <Route path="/word-finder" element={<WordFinderPage />} />
            )}
            {isBoardAnalyzerEnabled && (
              <Route path="/board-analyzer" element={<BoardAnalyzerPage />} />
            )}
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;