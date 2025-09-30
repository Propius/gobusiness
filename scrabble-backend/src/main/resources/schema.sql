-- Scrabble Database Schema
-- Contains all required tables for the comprehensive Scrabble application

-- Existing scores table (already implemented)
CREATE TABLE IF NOT EXISTS scores (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    word VARCHAR(255) NOT NULL,
    points INTEGER NOT NULL,
    enhanced_score INTEGER,
    positions TEXT,
    special_tiles TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Feature flags configuration table
CREATE TABLE IF NOT EXISTS feature_flags (
    flag_name VARCHAR(100) PRIMARY KEY,
    enabled BOOLEAN NOT NULL DEFAULT false,
    description VARCHAR(500),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100) DEFAULT 'system'
);

-- Tile configurations table
CREATE TABLE IF NOT EXISTS tile_configs (
    config_name VARCHAR(100) PRIMARY KEY,
    config_value INTEGER NOT NULL,
    description VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(100) DEFAULT 'system'
);

-- Dictionary cache table for performance optimization
CREATE TABLE IF NOT EXISTS dictionary_cache (
    word VARCHAR(255) PRIMARY KEY,
    is_valid BOOLEAN NOT NULL,
    dictionary_source VARCHAR(50) DEFAULT 'LANGUAGETOOL',
    cached_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    access_count INTEGER DEFAULT 1,
    last_accessed TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Board states table for board analyzer feature
CREATE TABLE IF NOT EXISTS board_states (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_session VARCHAR(255),
    board_data TEXT NOT NULL, -- JSON representation of 15x15 board
    hand_tiles VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    analysis_score INTEGER DEFAULT 0
);

-- Word generation history table
CREATE TABLE IF NOT EXISTS word_generation_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    input_letters VARCHAR(255) NOT NULL,
    generation_mode VARCHAR(20) NOT NULL, -- 'sampling' or 'exhaustive'
    results_count INTEGER DEFAULT 0,
    execution_time_ms INTEGER DEFAULT 0,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_session VARCHAR(255)
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_scores_points ON scores(points DESC);
CREATE INDEX IF NOT EXISTS idx_scores_created_at ON scores(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_dictionary_cache_source ON dictionary_cache(dictionary_source);
CREATE INDEX IF NOT EXISTS idx_dictionary_cache_accessed ON dictionary_cache(last_accessed DESC);

-- Indexes for board_states table
CREATE INDEX IF NOT EXISTS idx_board_states_user_session ON board_states(user_session);
CREATE INDEX IF NOT EXISTS idx_board_states_created_at ON board_states(created_at);

-- Indexes for word_generation_history table
CREATE INDEX IF NOT EXISTS idx_word_gen_mode ON word_generation_history(generation_mode);
CREATE INDEX IF NOT EXISTS idx_word_gen_generated_at ON word_generation_history(generated_at);
CREATE INDEX IF NOT EXISTS idx_word_gen_user_session ON word_generation_history(user_session);