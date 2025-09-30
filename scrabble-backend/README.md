# Scrabble Points Calculator Backend

A comprehensive Spring Boot application for calculating Scrabble word scores, finding optimal word combinations, and providing word scramble games.

## Technology Stack

- **Java 21** - Modern Java LTS version with latest language features
- **Spring Boot 3.2.2** - Main application framework for dependency injection, web layer, and configuration
- **Maven** - Build system and dependency management
- **H2 Database** - In-memory database for score persistence during development
- **Docker** - Container deployment platform

## Libraries and Their Purposes

### Core Dependencies

#### Spring Boot Starters
- **spring-boot-starter-web** - Provides embedded Tomcat server, Spring MVC, REST APIs, and JSON processing
- **spring-boot-starter-data-jpa** - Database access layer with JPA/Hibernate for entity mapping and repositories  
- **spring-boot-starter-validation** - Bean validation with Hibernate Validator for request/response validation
- **spring-boot-starter-test** - Testing framework including JUnit 5, Mockito, and Spring Test

#### Database
- **h2** (runtime) - Lightweight in-memory SQL database for development and testing

#### Documentation
- **springdoc-openapi-starter-webmvc-ui v2.3.0** - Generates interactive OpenAPI/Swagger documentation for REST APIs

#### Utilities  
- **commons-text v1.11.0** - Apache Commons library for advanced text processing operations

#### Dictionary and Language Processing
- **language-en v6.3** - LanguageTool English language pack for professional dictionary validation and spell checking
- **caffeine v3.1.8** - High-performance Java caching library for optimizing dictionary lookups and word generation

## Project Architecture

The application follows clean architecture principles with proper separation of concerns:

### Package Structure
```
com.govtech.scrabble/
├── config/           # Configuration classes (@ConfigurationProperties)
├── controller/       # REST API endpoints  
├── dto/             # Data Transfer Objects for API requests/responses
├── entity/          # JPA entities for database mapping
├── repository/      # Spring Data JPA repositories
├── service/         # Business logic layer with interface/implementation pattern
└── util/            # Utility classes for shared functionality
```

### Key Design Patterns
- **Interface Segregation** - All services implement focused interfaces
- **Constructor Injection** - No field injection (@Autowired eliminated) 
- **Factory Pattern** - Centralized scoring utilities in ScrabbleScoreUtil
- **Repository Pattern** - Data access abstraction via Spring Data JPA

## Features

### Core Functionality
1. **Word Score Calculator** - Calculate Scrabble points for individual words with optional special tiles support
2. **Enhanced Score Calculation** - Advanced scoring with special tile bonuses (DL, TL, DW, TW) and detailed bonus reporting
3. **Score Persistence** - Save and retrieve top scores from database
4. **Word Finder** - Find top 10 optimal word combinations from board and hand tiles
5. **Board Analyzer** - Analyze Scrabble board positions for top 10 highest-scoring placements
6. **Word Scramble Game** - Generate and validate scrambled word puzzles with LanguageTool dictionary
7. **Dictionary Validation** - Lightning-fast offline word validation using LanguageTool with intelligent caching

### Special Tiles Support
- **Double Letter (DL)** - 2x letter score multiplier
- **Triple Letter (TL)** - 3x letter score multiplier  
- **Double Word (DW)** - 2x total word score multiplier
- **Triple Word (TW)** - 3x total word score multiplier

## Configuration

The application uses YAML configuration with the following key sections:

- **Scrabble Properties** (`ScrabbleProperties.java`)
  - Board size and tile configuration
  - Special tiles positioning and multipliers
  - Dictionary validation settings
  - Word scramble game parameters
  
- **Feature Toggles** - All feature flags consolidated in ScrabbleProperties
  - Word finder, board analyzer, and scramble feature toggles
  - Centralized configuration management

## API Endpoints

### Core Scoring API
- `GET /api/scrabble/calculate?word={word}` - Basic word score calculation
- `POST /api/scrabble/calculate` - Enhanced score calculation with special tiles
- `POST /api/scrabble/scores` - Save score to database
- `GET /api/scrabble/scores/top` - Get top 10 scores

### Advanced Features API
- `POST /api/scrabble/word-finder` - Find top 10 possible words from tiles
- `POST /api/board-analyzer/analyze` - Analyze board for top 10 optimal placements
- `GET /api/scramble/new?difficulty={level}` - Generate word scramble puzzles

### Configuration API
- `GET /api/config/special-tiles` - Check special tiles feature status

## API Documentation

Interactive API documentation is available at:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs.json`
- **OpenAPI YAML**: `http://localhost:8080/v3/api-docs.yaml`

## Building and Running

### Prerequisites
- Java 21 or higher
- Maven 3.6+ (or use included wrapper)

### Development Commands
```bash
# Build application
./mvnw clean compile

# Run tests  
./mvnw test

# Package as JAR
./mvnw package

# Run application
./mvnw spring-boot:run
```

### Docker Deployment
```bash
# Build Docker image
docker build -t scrabble-backend .

# Run with Docker Compose (includes frontend)
docker-compose up --build
```

## Code Quality Standards

- **Constructor Injection** - All dependencies injected via constructors (no @Autowired)
- **Interface-based Design** - Services implement well-defined interfaces with consistent naming (XxxService/XxxServiceImpl)
- **DRY Principle** - Common scoring logic centralized in ScrabbleScoreUtil with special tiles support
- **Performance Optimization** - Caffeine caching for dictionary operations and LanguageTool rule optimization
- **No Hardcoded Data** - Dictionary validation uses LanguageTool exclusively without fallback word lists  
- **Immutable DTOs** - Request/response objects with proper validation and OpenAPI documentation
- **Comprehensive Testing** - Unit and integration tests with high coverage

## Technical Implementation

### Dictionary Service
The application uses an optimized English dictionary service that:
- **LanguageTool Integration** - Professional-grade spell checking with American English language pack
- **Intelligent Caching** - Caffeine-based caching with 50,000 word capacity and 2-hour expiration
- **Performance Optimization** - Disabled expensive rules for word validation (5 rules optimized)
- **Multi-Strategy Word Generation** - Random, pattern-based, and syllable-based generation approaches
- **No Fallback Dependencies** - Pure LanguageTool validation without hardcoded word lists

### Special Tiles Calculator
Enhanced scoring system that:
- **Backwards Compatible** - Existing GET endpoint unchanged for basic scoring
- **Advanced POST Endpoint** - Supports position-based special tile calculations
- **Detailed Reporting** - Shows base score, total score, and applied bonuses
- **Feature Configuration** - Respects feature flags and graceful degradation

## Development Notes

- Application runs on `localhost:8080` by default
- CORS configured for frontend at `http://localhost:3000`
- H2 console available in development mode
- All REST endpoints follow RESTful conventions
- JSON request/response format with proper HTTP status codes
- Top 10 result limiting enforced across word finder and board analyzer

## Contributing

- Follow established patterns for service interfaces and implementations
- Use constructor injection for all dependencies
- Add comprehensive test coverage for new features  
- Update this documentation when adding new libraries or features
- Ensure all code passes compilation and tests before committing