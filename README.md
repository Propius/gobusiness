# Scrabble Game Suite

A comprehensive full-stack Scrabble gaming application with multiple game modes and advanced features, built with React.js frontend and Spring Boot backend.

## Features

### 🎯 Core Game Modes
- **Score Calculator**: Calculate points for any word using official Scrabble letter values
- **Word Finder**: Find all possible words from board tiles and hand tiles (linear arrangement)
- **Board Analyzer**: Advanced 2D board analysis with coordinate system for optimal word placement
- **Word Scramble**: Interactive word unscrambling game with difficulty levels

### 🎨 Special Tiles System
- **Visual Special Tiles**: Color-coded special tiles (Double/Triple Letter/Word)
- **Accurate Scoring**: Proper Scrabble scoring with special tile bonuses
- **Interactive Editor**: Edit special tile placement with live preview
- **Scrabble Rules Compliance**: Bonuses only apply to newly placed tiles

### 🧠 Advanced Features
- **Dictionary Validation**: Lightning-fast offline word validation using LanguageTool
- **Scrabble Rules Engine**: Enforces proper connection rules, center square requirements
- **Smart Word Generation**: Finds highest scoring words efficiently
- **Coordinate System**: User-friendly A1-O15 grid references
- **Bingo Bonus**: 50-point bonus for using all 7 tiles

### 🎮 User Experience
- **Responsive Design**: Mobile-friendly interface that works on all devices
- **Keyboard Navigation**: Full keyboard support with Tab/Arrow key navigation
- **Real-time Updates**: Instant score calculation and word validation
- **Clean Interface**: Intuitive board layout with clear visual feedback
- **Feature Toggles**: Configurable features with runtime switching

## Technology Stack

### Backend Dependencies & Libraries

#### Core Framework
- **Java 21**: Modern LTS version with enhanced performance and features
- **Spring Boot 3.2.2**: Enterprise-grade Java framework with auto-configuration
  - `spring-boot-starter-web`: RESTful web services with embedded Tomcat
  - `spring-boot-starter-data-jpa`: JPA/Hibernate for database operations
  - `spring-boot-starter-validation`: Bean validation with Hibernate Validator
  - `spring-boot-starter-test`: Comprehensive testing framework

#### Database & Persistence
- **H2 Database 2.2.224**: Fast in-memory database for development
  - JDBC URL: `jdbc:h2:mem:scrabble`
  - Web console available at `/h2-console`
  - Automatic schema creation and data population

#### Documentation & API
- **SpringDoc OpenAPI 3 (2.3.0)**: Automatic API documentation generation
  - Interactive Swagger UI at `/swagger-ui.html`
  - OpenAPI specification generation
  - Request/response schema documentation

#### Text Processing & Language
- **Apache Commons Text 1.11.0**: Advanced text manipulation utilities
  - String algorithms and text processing
  - Word validation and manipulation
- **LanguageTool English 6.3**: Professional spell checking and grammar
  - Comprehensive English dictionary
  - Word validation and suggestion engine
  - Offline processing for fast performance

#### Build & Development
- **Maven Wrapper**: Consistent build environment across systems
- **Spring Boot Maven Plugin**: Application packaging and execution
- **JUnit 5**: Modern testing framework with Jupiter API
- **Mockito**: Mocking framework for unit tests

### Frontend Dependencies & Libraries

#### Core Framework
- **React 18.2.0**: Modern JavaScript library with concurrent features
  - Functional components with hooks
  - Enhanced performance with automatic batching
  - Concurrent rendering capabilities
- **React DOM 18.2.0**: React renderer for web applications
- **React Router DOM 7.9.3**: Declarative routing for React applications
  - Client-side navigation
  - Nested routing support
  - Navigation guards and data loading

#### HTTP & API Communication
- **Axios 1.6.7**: Promise-based HTTP client
  - Request/response interceptors
  - Automatic JSON parsing
  - Error handling and retry logic
  - Base URL configuration for API endpoints

#### Testing Framework
- **Jest**: JavaScript testing framework (via react-scripts)
  - Unit testing with mocking capabilities
  - Code coverage reporting
  - Snapshot testing
- **React Testing Library 13.4.0**: Testing utilities for React components
  - User-centric testing approach
  - DOM interaction simulation
  - Accessibility-focused queries
- **Jest DOM 5.17.0**: Custom Jest matchers for DOM testing
- **User Event 14.5.2**: Advanced user interaction simulation

#### Development & Build Tools
- **React Scripts 5.0.1**: Zero-configuration build toolchain
  - Webpack bundling with hot reload
  - Babel transpilation for modern JavaScript
  - ESLint integration for code quality
  - PostCSS for advanced CSS processing
  - Development server with proxy support

#### Performance & Monitoring
- **Web Vitals 2.1.4**: Essential metrics for user experience
  - Core Web Vitals measurement (LCP, FID, CLS)
  - Performance monitoring and reporting
  - Real user metrics collection

#### Code Quality & Standards
- **ESLint**: Static code analysis (configured via react-app preset)
  - React-specific linting rules
  - Hook dependency validation
  - Best practices enforcement
- **Browserslist**: Target browser configuration
  - Production: Modern browsers (>0.2% usage)
  - Development: Latest Chrome, Firefox, Safari

## Library Documentation & Dependency Management

### Backend Library Details

#### Spring Boot Ecosystem
```xml
<!-- Core Spring Boot Starters -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <!-- Includes: Spring MVC, Tomcat, Jackson, Validation -->
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <!-- Includes: Hibernate, Spring Data JPA, HikariCP -->
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
    <!-- Includes: Hibernate Validator, Bean Validation API -->
</dependency>
```

#### Database Configuration
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
    <!-- Version managed by Spring Boot (2.2.224) -->
</dependency>
```

**H2 Database Features:**
- In-memory storage for fast testing and development
- SQL compatibility with major databases
- Web-based admin console
- Zero configuration required
- Automatic DDL generation

#### API Documentation
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

**SpringDoc OpenAPI Features:**
- Automatic API documentation from code annotations
- Interactive Swagger UI interface
- OpenAPI 3.0 specification compliance
- Integration with Spring Security
- Customizable UI themes and layouts

#### Language Processing Libraries
```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-text</artifactId>
    <version>1.11.0</version>
</dependency>

<dependency>
    <groupId>org.languagetool</groupId>
    <artifactId>language-en</artifactId>
    <version>6.3</version>
</dependency>
```

**Apache Commons Text Capabilities:**
- String similarity algorithms (Levenshtein, Cosine, etc.)
- Text transformation utilities
- String escaping and unescaping
- Random string generation
- WordUtils for word manipulation

**LanguageTool Features:**
- Advanced spell checking engine
- Grammar and style checking
- Rule-based language processing
- Support for 25+ languages
- Offline processing capabilities

### Frontend Library Details

#### React Ecosystem
```json
{
  "react": "^18.2.0",
  "react-dom": "^18.2.0",
  "react-router-dom": "^7.9.3"
}
```

**React 18.2.0 New Features:**
- Concurrent rendering for better performance
- Automatic batching of state updates
- Suspense improvements for data fetching
- Server-side rendering enhancements
- Strict mode improvements for detecting side effects

**React Router DOM 7.9.3 Features:**
- File-based routing support
- Enhanced data loading patterns
- Improved error boundaries
- Type-safe route parameters
- Advanced navigation guards

#### HTTP Client Configuration
```json
{
  "axios": "^1.6.7"
}
```

**Axios Configuration Example:**
```javascript
// API base configuration
const apiClient = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Request interceptor for authentication
apiClient.interceptors.request.use(config => {
  // Add authentication token if available
  return config;
});

// Response interceptor for error handling
apiClient.interceptors.response.use(
  response => response,
  error => {
    console.error('API Error:', error);
    return Promise.reject(error);
  }
);
```

#### Testing Infrastructure
```json
{
  "@testing-library/react": "^13.4.0",
  "@testing-library/jest-dom": "^5.17.0",
  "@testing-library/user-event": "^14.5.2"
}
```

**Testing Best Practices:**
- Component testing with React Testing Library
- User interaction simulation with User Event
- Custom Jest matchers for DOM assertions
- Accessibility-first testing approach
- Mock API responses for isolated testing

#### Build and Development Tools
```json
{
  "react-scripts": "5.0.1",
  "web-vitals": "^2.1.4"
}
```

**React Scripts Includes:**
- Webpack 5 for bundling and optimization
- Babel for JavaScript transpilation
- PostCSS for CSS processing
- ESLint for code quality
- Jest for testing
- Development server with hot reload

### Version Management Strategy

#### Backend Dependencies
- **Spring Boot Parent**: Manages 200+ dependency versions automatically
- **Maven Wrapper**: Ensures consistent Maven version (3.9.x)
- **Java 21**: LTS version with support until 2031
- **Explicit Versions**: Only specified for libraries not managed by Spring Boot

#### Frontend Dependencies
- **React 18**: Stable release with concurrent features
- **Semantic Versioning**: Using caret ranges (^) for minor updates
- **Lock File**: package-lock.json ensures consistent installations
- **Regular Updates**: Monthly dependency review and updates

### Security Considerations

#### Backend Security
- **Spring Boot Security**: Automatic security headers and CSRF protection
- **Input Validation**: Bean Validation API with custom validators
- **SQL Injection Prevention**: JPA/Hibernate parameterized queries
- **Dependency Scanning**: Regular security audits of Maven dependencies

#### Frontend Security
- **XSS Prevention**: React's automatic escaping of user input
- **HTTPS Enforcement**: Secure communication in production
- **Content Security Policy**: Restricts resource loading
- **Dependency Auditing**: npm audit for vulnerability scanning

### Performance Optimizations

#### Backend Performance
- **Connection Pooling**: HikariCP for efficient database connections
- **JPA Query Optimization**: Lazy loading and query optimization
- **HTTP Caching**: Cache headers for static resources
- **Memory Management**: JVM tuning for optimal performance

#### Frontend Performance
- **Code Splitting**: Webpack automatic chunk splitting
- **Tree Shaking**: Elimination of unused code
- **Asset Optimization**: Image compression and lazy loading
- **Bundle Analysis**: Regular bundle size monitoring

### Development Workflow

#### Backend Development
```bash
# Dependency analysis
./mvnw dependency:tree
./mvnw dependency:analyze

# Security scanning
./mvnw org.owasp:dependency-check-maven:check

# Code coverage
./mvnw jacoco:report
```

#### Frontend Development
```bash
# Dependency management
npm audit                    # Security vulnerabilities
npm outdated                # Update candidates
npm ls                      # Dependency tree

# Bundle analysis
npm run build               # Production build
npx webpack-bundle-analyzer build/static/js/*.js
```

## Letter Scoring Rules

| Letters | Points |
|---------|--------|
| A, E, I, O, U, L, N, S, T, R | 1 |
| D, G | 2 |
| B, C, M, P | 3 |
| F, H, V, W, Y | 4 |
| K | 6 |
| J, X | 8 |
| Q, Z | 10 |

## Quick Start

### Prerequisites
- **Java 21+** (for backend)
- **Node.js 16+** and **npm** (for frontend)
- **Docker & Docker Compose** (optional, recommended)

### Option 1: Docker Compose (Recommended)
```bash
# Start both services
docker-compose up -d

# Stop services
docker-compose down

# Rebuild after code changes
docker-compose up --build
```

### Option 2: Local Development
```bash
# Terminal 1 - Backend
cd scrabble-backend
./mvnw spring-boot:run

# Terminal 2 - Frontend
cd scrabble-frontend
npm install && npm start
```

### Application URLs
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **Database Console**: http://localhost:8080/h2-console
  - URL: `jdbc:h2:mem:scrabble`
  - User: `sa` | Password: (empty)

## Development Commands

### Backend Development
```bash
cd scrabble-backend
./mvnw clean compile          # Build
./mvnw test                   # Run tests  
./mvnw spring-boot:run        # Start server
./mvnw package               # Package for deployment
```

### Frontend Development
```bash
cd scrabble-frontend
npm install                   # Install dependencies
npm start                    # Start dev server
npm test                     # Run tests
npm run build               # Production build
```

### Docker Development
```bash
# Basic operations
docker-compose up -d         # Start services
docker-compose down          # Stop services
docker-compose logs          # View logs
docker-compose ps            # Check status

# Development workflow
docker-compose up --build    # Rebuild and start
docker-compose build service-name  # Rebuild specific service
```

**Development Tip**: Use local development for faster iteration, Docker for production-like testing.

## API Endpoints

### Score Calculator
- **GET** `/api/scrabble/calculate?word={word}` - Calculate Scrabble points for a word
- **POST** `/api/scrabble/scores` - Save a score to persistent storage
  ```json
  { "word": "HELLO" }
  ```
- **GET** `/api/scrabble/scores/top` - Get top 10 high scores

### Word Finder
- **POST** `/api/scrabble/word-finder` - Find possible words from board and hand tiles
  ```json
  {
    "boardTiles": ["T", "", "A", "R", "E", "", "", "", "", "", "", "", "", "", ""],
    "handTiles": ["S", "P", "O", "R", "T", "S", ""]
  }
  ```

### Board Analyzer  
- **POST** `/api/board-analyzer/analyze` - Analyze 15×15 board for optimal word placement
  ```json
  {
    "boardLetters": ["", "", "H", "E", "L", "L", "O", "", "", ""],
    "handLetters": ["W", "O", "R", "L", "D", "S", ""],
    "specialTiles": ["normal", "dw", "normal", "tl", "normal", "dl"]
  }
  ```
- **GET** `/api/board-analyzer/status` - Check if board analyzer is enabled

### Word Scramble
- **GET** `/api/scramble/new?difficulty={difficulty}` - Generate new scrambled word
  - Query params: `difficulty` (optional): "easy", "medium", "hard"
- **POST** `/api/scramble/{sessionId}/check` - Check scrambled word answer
  ```json
  { "answer": "HELLO" }
  ```
- **POST** `/api/scramble/{sessionId}/reshuffle` - Reshuffle current word
- **GET** `/api/scramble/stats` - Get scramble game statistics

### Configuration
- **GET** `/api/config/tiles` - Get tile count configuration for all game modes
- **GET** `/api/config/special-tiles` - Get special tiles feature status for all modes  
- **GET** `/api/config/scramble` - Get word scramble settings and difficulty levels
- **GET** `/api/config/letter-scoring` - Get letter scoring display configuration
- **GET** `/api/word-finder/status` - Get word finder feature status

## Usage Instructions

### Score Calculator
1. **Enter Letters**: Click on any tile or use Tab/Arrow keys to navigate between tiles
2. **Real-time Scoring**: Watch your score update automatically as you type
3. **Special Tiles**: Enable special tiles for bonus scoring
4. **Save Score**: Click "Save Score" to permanently save your current word and score
5. **View Scores**: Click "View Top Scores" to see the leaderboard

### Word Finder
1. **Board Tiles**: Enter existing letters on the game board (linear arrangement)
2. **Hand Tiles**: Enter the letters you have in your hand
3. **Find Words**: Click "Find Words" to see all possible word combinations
4. **Results**: View top 10 highest scoring words with positions and tile usage

### Board Analyzer
1. **2D Board Setup**: Enter letters on the 15×15 Scrabble board using A1-O15 coordinates
2. **Hand Tiles**: Enter your 7 hand tiles
3. **Special Tiles**: Toggle edit mode to place special tiles (DL, TL, DW, TW)
4. **Analyze**: Click "Analyze Board" to find optimal word placements
5. **Results**: View top combinations with exact positions and scores

### Word Scramble
1. **Choose Difficulty**: Select easy (4-5 letters), medium (6-7), or hard (8-10)
2. **Unscramble**: Rearrange the letters to form the original word
3. **Hints**: Use "Reshuffle" to get a new arrangement
4. **Submit**: Enter your answer and get instant feedback

## Project Structure

```
gobusiness/
├── CLAUDE.md                           # Project instructions for Claude Code
├── README.md                           # This documentation
├── docker-compose.yml                  # Multi-service container orchestration
├── requirement.pdf                     # Project requirements document
│
├── scrabble-backend/                   # Spring Boot REST API
│   ├── Dockerfile                      # Backend container configuration
│   ├── pom.xml                        # Maven dependencies and build config
│   ├── mvnw, mvnw.cmd                 # Maven wrapper scripts
│   ├── src/main/java/com/govtech/scrabble/
│   │   ├── ScrabbleApplication.java    # Spring Boot main class
│   │   ├── config/                     # Configuration classes
│   │   │   ├── ScrabbleProperties.java # Application properties binding
│   │   │   └── WebConfig.java         # CORS and web configuration
│   │   ├── controller/                 # REST API controllers (interfaces)
│   │   │   ├── BoardAnalyzerController.java
│   │   │   ├── ConfigController.java
│   │   │   ├── ScrabbleController.java
│   │   │   ├── ScrambleController.java
│   │   │   ├── WordFinderStatusController.java
│   │   │   └── impl/                  # Controller implementations
│   │   │       ├── BoardAnalyzerControllerImpl.java
│   │   │       ├── ConfigControllerImpl.java
│   │   │       ├── ScrabbleControllerImpl.java
│   │   │       ├── ScrambleControllerImpl.java
│   │   │       └── WordFinderStatusControllerImpl.java
│   │   ├── dto/                       # Data Transfer Objects
│   │   │   ├── BoardAnalyzerRequest.java
│   │   │   ├── BoardAnalyzerResponse.java
│   │   │   ├── CalculateScoreResponse.java
│   │   │   ├── ScoreRequest.java
│   │   │   ├── ScoreResponse.java
│   │   │   ├── ScrambleCheckResponse.java
│   │   │   ├── ScrambleResponse.java
│   │   │   ├── WordFinderRequest.java
│   │   │   └── WordFinderResponse.java
│   │   ├── entity/                    # JPA database entities
│   │   │   └── Score.java
│   │   ├── repository/                # Data access layer
│   │   │   └── ScoreRepository.java
│   │   ├── service/                   # Business logic interfaces
│   │   │   ├── BoardAnalyzerService.java
│   │   │   ├── EnglishDictionaryService.java
│   │   │   ├── ScrabbleService.java
│   │   │   ├── WordFinderService.java
│   │   │   ├── WordScrambleService.java
│   │   │   └── impl/                  # Service implementations
│   │   │       ├── BoardAnalyzerServiceImpl.java
│   │   │       ├── EnglishDictionaryServiceImpl.java
│   │   │       ├── ScrabbleServiceImpl.java
│   │   │       ├── WordFinderServiceImpl.java
│   │   │       └── WordScrambleServiceImpl.java
│   │   └── util/                      # Utility classes
│   │       └── ScrabbleScoreUtil.java
│   ├── src/main/resources/
│   │   └── application.yml            # Application configuration
│   ├── src/test/java/                 # Unit and integration tests
│   │   └── com/govtech/scrabble/
│   │       ├── controller/
│   │       │   └── ScrabbleControllerTest.java
│   │       └── service/
│   │           ├── ScrabbleServiceTest.java
│   │           └── WordScrambleServiceTest.java
│   └── target/                        # Maven build output
│
├── scrabble-frontend/                 # React Single Page Application
│   ├── Dockerfile                     # Frontend container configuration
│   ├── nginx.conf                     # Nginx configuration for production
│   ├── package.json                   # npm dependencies and scripts
│   ├── package-lock.json              # Dependency lock file
│   ├── public/
│   │   └── index.html                 # HTML entry point
│   ├── src/
│   │   ├── App.js                     # Main React component with routing
│   │   ├── App.css                    # Global styles
│   │   ├── index.js                   # React DOM entry point
│   │   ├── index.css                  # Base CSS styles
│   │   ├── components/                # Reusable React components
│   │   │   ├── ActionButtons.js       # Score calculator action buttons
│   │   │   ├── ActionButtons.css
│   │   │   ├── EnhancedTileGrid.js    # Advanced tile grid component
│   │   │   ├── Navigation/
│   │   │   │   ├── SideNav.js         # Side navigation menu
│   │   │   │   └── SideNav.css
│   │   │   ├── ScoreDisplay.js        # Score display component
│   │   │   ├── ScoreDisplay.css
│   │   │   ├── ScrambleGame.js        # Word scramble game component
│   │   │   ├── ScrambleGame.css
│   │   │   ├── SpecialTiles/
│   │   │   │   ├── EnhancedTileInput.js     # Special tile input component
│   │   │   │   ├── SpecialTileSelector.js  # Special tile selector
│   │   │   │   └── SpecialTileSelector.css
│   │   │   ├── TileGrid.js            # Basic tile grid component
│   │   │   ├── TileGrid.css
│   │   │   ├── TopScoresModal.js      # High scores modal dialog
│   │   │   ├── TopScoresModal.css
│   │   │   ├── WordFinder.js          # Word finder component
│   │   │   ├── WordFinder.css
│   │   │   └── __tests__/             # Component unit tests
│   │   │       ├── ActionButtons.test.js
│   │   │       ├── ScoreDisplay.test.js
│   │   │       └── TileGrid.test.js
│   │   ├── pages/                     # Page-level components
│   │   │   ├── BoardAnalyzerPage.js   # 2D board analyzer interface
│   │   │   ├── BoardAnalyzerPage.css
│   │   │   ├── Layout.css             # Common layout styles
│   │   │   ├── ScoreCalculator.js     # Score calculator page
│   │   │   ├── WordFinderPage.js      # Word finder interface
│   │   │   └── WordScramblePage.js    # Word scramble game page
│   │   └── services/                  # API service layer
│   │       ├── configService.js       # Configuration API calls
│   │       ├── scrabbleService.js     # Score calculator API calls
│   │       ├── scrambleService.js     # Word scramble API calls
│   │       ├── wordFinderService.js   # Word finder API calls
│   │       └── __tests__/             # Service unit tests
│   │           ├── scrabbleService.test.js
│   │           └── scrambleService.test.js
│   ├── build/                         # Production build output
│   └── node_modules/                  # npm dependencies

```

## Testing

### Backend Tests
```bash
cd scrabble-backend
./mvnw test                   # Run all tests
./mvnw test -Dtest=ScrabbleServiceTest  # Run specific test class
./mvnw jacoco:report         # Generate coverage report (if configured)
```

**Current Test Classes:**
- `ScrabbleControllerTest` - REST API endpoint testing
- `ScrabbleServiceTest` - Business logic for score calculation
- `WordScrambleServiceTest` - Word scramble game logic

### Frontend Tests
```bash
cd scrabble-frontend
npm test                     # Run tests in watch mode
npm test -- --coverage      # Run tests with coverage report
npm test -- --watchAll=false  # Run tests once (CI mode)
```

**Current Test Files:**
- `ActionButtons.test.js` - Score calculator buttons
- `ScoreDisplay.test.js` - Score display component
- `TileGrid.test.js` - Tile grid functionality
- `scrabbleService.test.js` - API service layer
- `scrambleService.test.js` - Scramble game service

### Test Coverage Areas
- **Backend**: Service layer business logic, REST API endpoints, input validation, error handling
- **Frontend**: Component rendering, user interactions, API integration, state management
- **Integration**: End-to-end API communication between frontend and backend

## Dictionary Validation System

The application uses LanguageTool for offline dictionary validation:

### Dictionary Implementation
- **LanguageTool Engine**: Professional-grade offline dictionary validation
- **High Performance**: Local processing with sub-millisecond response times
- **No External Dependencies**: Works without internet connectivity  
- **American English**: Uses AmericanEnglish language model
- **Advanced Detection**: Spelling errors and grammar rule matching

### Current Configuration
The application is configured with the following settings in `application.yml`:

```yaml
scrabble:
  # Note: External dictionary API configuration exists but is unused
  # The system actually uses LanguageTool for all validation
  dictionary:
    validation:
      enabled: true
      api:
        url: https://api.dictionaryapi.dev/api/v2/entries/en/
        timeout: 5000
        retry-attempts: 2
  
  # Word scramble game settings
  scramble:
    enabled: true
    word-length:
      min: 4
      max: 10
    difficulty-levels:
      easy: { min: 4, max: 5 }
      medium: { min: 6, max: 7 }
      hard: { min: 8, max: 10 }
  
  # Feature toggles
  word-finder:
    enabled: true
  board-analyzer:
    enabled: true
  
  # Tile count configuration
  tiles:
    score-calculator:
      tile-count: 10
    word-finder:
      board-tile-count: 15
      hand-tile-count: 7
    board-analyzer:
      board-size: 15
  
  # Special tiles configuration
  special-tiles:
    score-calculator:
      enabled: true
    word-finder:
      enabled: true
    board-analyzer:
      enabled: true
  
  # Letter scoring display
  letter-scoring:
    score-calculator:
      enabled: true
    word-finder:
      enabled: true
    board-analyzer:
      enabled: true
```

### Word Validation Behavior
- **Local Processing**: All validation happens offline using LanguageTool
- **Real-time Feedback**: Instant validation with zero network latency
- **Rule-based Detection**: Uses spelling and grammar rules for accuracy
- **Error Handling**: Robust validation with detailed error information
- **Performance Optimized**: No API calls or network dependencies
- **Always Available**: Works in any environment without connectivity requirements

## Architecture Overview

### Design Principles
- **Clean Architecture**: Separation of concerns with clear layering
- **Interface-based Design**: Testable and modular service implementations
- **Constructor Injection**: Spring dependency injection best practices
- **Configuration-driven**: Feature toggles and runtime configuration
- **Performance-focused**: Local processing and optimized algorithms

### Key Configuration Features
- **Feature Toggles**: Enable/disable game modes at runtime
- **Tile Configurations**: Customizable board sizes and tile counts
- **Special Tiles**: Configurable bonus tile systems
- **Difficulty Levels**: Adjustable word scramble complexity

## Performance & Features

- **Lightning-Fast Validation**: LanguageTool provides sub-millisecond offline word validation
- **Real-time Processing**: Instant scoring and validation with optimized algorithms
- **Zero Network Dependencies**: All validation happens locally for consistent performance
- **Mobile Responsive**: Touch-friendly interface optimized for all device sizes
- **Accessibility**: Full keyboard navigation and screen reader support
- **Modern UI**: Clean interface with proper contrast and intuitive layout

## Production Deployment

### Database Migration
- Replace H2 with production database (PostgreSQL recommended)
- Configure connection pooling and optimization
- Set up database migrations and backups

### Security & Monitoring
- Implement authentication and authorization
- Add rate limiting and input validation
- Configure CORS for production domains
- Set up comprehensive logging and monitoring
- Enable HTTPS and security headers

### Build & Deployment
```bash
# Production builds
cd scrabble-backend && ./mvnw clean package
cd scrabble-frontend && npm run build

# Environment configuration
export SPRING_PROFILES_ACTIVE=production
export REACT_APP_API_URL=https://your-api-domain.com
```

## Contributing

We welcome contributions! Please follow these guidelines:

1. **Fork & Branch**: Create a feature branch from `main`
2. **Code Standards**: Follow existing code style and conventions
3. **Testing**: Add tests for new features and ensure all tests pass
4. **Documentation**: Update README and code comments as needed
5. **Pull Request**: Submit PR with clear description of changes

### Development Standards
- Follow SOLID principles and clean code practices
- Write comprehensive tests (unit and integration)
- Ensure production-ready code with proper error handling
- Follow security best practices
- Maintain backward compatibility

## License & Acknowledgments

**Educational Project**: Created for technical assessment and learning purposes.

**Third-party Libraries**: Special thanks to the open-source community:
- Spring Boot and Spring ecosystem
- React and React ecosystem  
- LanguageTool for offline dictionary capabilities
- All other dependencies listed in pom.xml and package.json

---

*Built with ❤️ for the Scrabble gaming community*