-- Initial data for Scrabble application
-- Populates default feature flags and configuration values

-- Feature flags (matching requirements.md specifications)
MERGE INTO feature_flags (flag_name, enabled, description) KEY(flag_name) VALUES
('ENABLE_DICTIONARY_VALIDATION', true, 'Enable dictionary validation for word checking'),
('ENABLE_WORD_GENERATION', false, 'Enable word generation from given letters (sampling and exhaustive modes)'),
('ENABLE_WORD_FINDER', true, 'Enable word finder for single-row board analysis'),
('ENABLE_TILES_CONFIGURATION', true, 'Enable configurable tile counts across features'),
('ENABLE_SPECIAL_TILES', true, 'Enable special tiles (DLS, TLS, DWS, TWS) and blank tiles'),
('ENABLE_BOARD_ANALYZER', true, 'Enable full 15x15 board analysis feature'),
('ENABLE_SCORING_LEGEND', true, 'Enable letter scoring legend display');

-- Tile configurations (from requirements.md and application.yml)
MERGE INTO tile_configs (config_name, config_value, description) KEY(config_name) VALUES
('SCORE_CALCULATOR_TILES', 10, 'Number of tiles for basic score calculator'),
('WORD_FINDER_BOARD_TILES', 15, 'Board size for word finder feature'),
('HAND_TILES_COUNT', 7, 'Number of hand tiles for word finder and board analyzer'),
('BOARD_ANALYZER_SIZE', 15, 'Full board size for board analyzer (15x15)'),
('MAX_ATTEMPTS_PER_LENGTH', 200, 'Maximum word generation attempts per word length'),
('MAX_SAMPLING_RESULTS', 50, 'Maximum results for sampling word generation'),
('MAX_EXHAUSTIVE_RESULTS', 200, 'Maximum results for exhaustive word generation'),
('MIN_WORD_LENGTH', 2, 'Minimum word length for generation'),
('MAX_WORD_LENGTH', 15, 'Maximum word length for generation'),
('DICTIONARY_CACHE_SIZE', 10000, 'Maximum number of words to cache in dictionary'),
('RATE_LIMIT_REQUESTS_PER_MINUTE', 120, 'API rate limit per IP address per minute'),
('RATE_LIMIT_BURST_CAPACITY', 20, 'Burst capacity for rate limiting');

-- Sample board states for testing (optional - can be removed in production)
MERGE INTO board_states (id, user_session, board_data, hand_tiles, analysis_score) KEY(id) VALUES
(1, 'demo-session-1',
 '{"tiles": ["","","P","H","O","","","","","","","","","",""], "size": 15}',
 'LDERS',
 45),
(2, 'demo-session-2',
 '{"tiles": ["","","","C","A","T","","","","","","","","",""], "size": 15}',
 'DOGHE',
 32);

-- Sample dictionary cache entries for common words (for testing)
MERGE INTO dictionary_cache (word, is_valid, dictionary_source) KEY(word) VALUES
('CAT', true, 'LANGUAGETOOL'),
('DOG', true, 'LANGUAGETOOL'),
('HELLO', true, 'LANGUAGETOOL'),
('WORLD', true, 'LANGUAGETOOL'),
('SCRABBLE', true, 'LANGUAGETOOL'),
('QUIZ', true, 'LANGUAGETOOL'),
('JAZZ', true, 'LANGUAGETOOL'),
('XYZ', false, 'LANGUAGETOOL'),
('ASDFGH', false, 'LANGUAGETOOL'),
('QWERTY', false, 'LANGUAGETOOL');