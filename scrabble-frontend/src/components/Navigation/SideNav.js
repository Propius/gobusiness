import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import './SideNav.css';

function SideNav({ isScrambleEnabled, isWordFinderEnabled, isBoardAnalyzerEnabled }) {
  const [isCollapsed, setIsCollapsed] = useState(false);
  const location = useLocation();

  const toggleSidebar = () => {
    setIsCollapsed(!isCollapsed);
  };

  const menuItems = [
    {
      path: '/',
      name: 'Score Calculator',
      icon: '🎯',
      enabled: true
    },
    {
      path: '/word-finder',
      name: 'Word Finder',
      icon: '🔍',
      enabled: isWordFinderEnabled
    },
    {
      path: '/board-analyzer',
      name: 'Board Analyzer',
      icon: '🏆',
      enabled: isBoardAnalyzerEnabled
    },
    {
      path: '/scramble',
      name: 'Word Scramble',
      icon: '🔤',
      enabled: isScrambleEnabled
    }
  ];

  return (
    <nav className={`side-nav ${isCollapsed ? 'collapsed' : ''}`}>
      <div className="nav-header">
        <div className="nav-brand">
          <span className="brand-icon">📝</span>
          {!isCollapsed && <span className="brand-text">Scrabble</span>}
        </div>
        <button 
          className="toggle-btn" 
          onClick={toggleSidebar}
          aria-label="Toggle navigation"
        >
          {isCollapsed ? '➤' : '◀'}
        </button>
      </div>
      
      <ul className="nav-menu">
        {menuItems.map((item) => {
          if (!item.enabled) return null;
          
          const isActive = location.pathname === item.path;
          
          return (
            <li key={item.path} className={`nav-item ${isActive ? 'active' : ''}`}>
              <Link to={item.path} className="nav-link" title={isCollapsed ? item.name : ''}>
                <span className="nav-icon">{item.icon}</span>
                {!isCollapsed && <span className="nav-text">{item.name}</span>}
              </Link>
            </li>
          );
        })}
      </ul>
    </nav>
  );
}

export default SideNav;