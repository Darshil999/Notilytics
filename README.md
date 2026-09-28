# NotiLytics

**A Reactive News Analytics Web Application**

NotiLytics is a comprehensive news search and analysis platform built with Play Framework that integrates with the News API to provide real-time news retrieval, sentiment analysis, readability metrics, and source profiling capabilities.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [Running Tests](#running-tests)
- [API Endpoints](#api-endpoints)
- [Key Components](#key-components)
- [Test Coverage](#test-coverage)
- [Team](#team)
- [Course Information](#course-information)

---

## Features

### Core Functionality
- **Real-time News Search**: Search for news articles using keywords with customizable sorting options
- **Multiple Search History**: Session-based storage of up to 10 search queries with 10 articles each
- **Reactive Programming**: Asynchronous API calls using CompletableFuture for optimal performance

### Analytics Features

1. **News Sources Display** (Task A - Muhammed Zayed Abdul Nasser)
   - Comprehensive list of all available news sources
   - Source filtering and categorization
   - Direct links to source profiles

2. **Source Website Profile** (Task B - Darshil Ketankumar Kalyani)
   - Detailed information about individual news sources
   - List of articles from specific sources
   - Source metadata and description

3. **Word Statistics** (Task C - Priya Dharshini Krishnan)
   - Word frequency analysis across search results
   - Paginated display of top 20 most frequent words
   - Visualization of word distribution

4. **Article Sentiment Analysis** (Task D - Finn Kleckner)
   - Positive, negative, and neutral word detection
   - Percentage-based sentiment scoring
   - Visual emoticon representation (happy, sad, neutral)
   - Real-time analysis of article descriptions

5. **Description Readability** (Task E - Wei Huang)
   - Flesch-Kincaid Grade Level calculation
   - Flesch Reading Ease score
   - Average Grade Level and Reading Ease across search results
   - Visual indicators for text complexity

---

## Tech Stack

### Backend
- **Play Framework 2.9.x** (Java)
- **Java 17**
- **Scala 2.13.17** (for Play templates)
- **Guice** (Dependency Injection)

### Testing
- **JUnit 4.13.2**
- **Mockito 5.11.0**
- **AssertJ 3.25.3**
- **JaCoCo** (Code Coverage)

### External APIs
- **News API** - Real-time news data retrieval
- **Play WS** - Asynchronous HTTP client

### Frontend
- **Scala HTML Templates**
- **CSS** (Custom styling)
- **JavaScript** (Client-side interactions)

---

## Project Structure

```
NotiLytics/
├── app/
│   ├── controllers/
│   │   └── NotiLyticsController.java     # Main application controller
│   ├── models/
│   │   ├── Article.java                   # Article data model
│   │   ├── Source.java                    # Source data model
│   │   ├── SearchQuery.java               # Search query model
│   │   ├── NewsAPIService.java            # News API integration
│   │   └── SourcesAPIService.java         # Sources API integration
│   ├── utils/
│   │   ├── ReadabilityCalculator.java     # Flesch-Kincaid calculations
│   │   ├── SentimentAnalysis.java         # Sentiment analysis utilities
│   │   └── SentimentTuple.java            # Sentiment data structure
│   └── views/
│       ├── main.scala.html                # Base template
│       ├── index.scala.html               # Homepage with search
│       ├── wordstats.scala.html           # Word statistics display
│       ├── sources.scala.html             # All sources listing
│       └── sourceProfile.scala.html       # Individual source profile
├── conf/
│   ├── application.conf                   # Application configuration
│   ├── routes                             # URL routing definitions
│   └── logback.xml                        # Logging configuration
├── test/
│   ├── controllers/
│   │   └── NotiLyticsControllerTest.java
│   ├── models/
│   │   ├── ArticleTest.java
│   │   ├── SourceTest.java
│   │   ├── SearchQueryTest.java
│   │   ├── NewsAPIServiceTest.java
│   │   └── SourcesAPIServiceTest.java
│   ├── utils/
│   │   ├── ReadabilityCalculatorTest.java
│   │   └── SentimentAnalysisTest.java
│   └── views/
│       └── IndexViewTest.java
├── public/
│   ├── stylesheets/
│   ├── javascripts/
│   └── images/
├── project/
│   ├── build.properties
│   └── plugins.sbt
└── build.sbt                              # Build configuration
```

---

## Prerequisites

Before running this project, ensure you have the following installed:

- **Java Development Kit (JDK) 17** or higher
- **sbt (Scala Build Tool)** 1.9.x or higher
- **Git** (for version control)
- **News API Key** (free tier available at https://newsapi.org/)

---

## Installation

### Step 1: Clone the Repository

```bash
git clone <repository-url>
cd soen6441-project-fall2025
```

### Step 2: Checkout the Final Branch

```bash
git checkout final-merge
```

### Step 3: Configure API Key

Edit `conf/application.conf` and add your News API key:

```conf
newsapi.key = "your-api-key-here"
newsapi.uri = "https://newsapi.org/v2/everything?"
```

### Step 4: Install Dependencies

```bash
sbt clean compile
```

---

## Running the Application

### Start the Development Server

```bash
sbt run
```

The application will be available at: **http://localhost:9000**

### Production Mode

```bash
sbt dist
unzip target/universal/notilytics-1.0-snapshot.zip
cd notilytics-1.0-snapshot
bin/notilytics -Dplay.http.secret.key="your-secret-key"
```

---

## Running Tests

### Run All Tests

```bash
sbt test
```

### Generate Test Coverage Report

```bash
sbt jacoco
```

The coverage report will be generated at:
```
target/scala-2.13/jacoco/report/html/index.html
```

### Run Specific Test Suites

```bash
# Controller tests only
sbt "testOnly controllers.*"

# Model tests only
sbt "testOnly models.*"

# Utility tests only
sbt "testOnly utils.*"
```

### Expected Test Results

- **Total Tests**: 184 passing tests
- **Target Coverage**: 100% for individual components
- **Overall Coverage**: High coverage across all critical paths

---

## API Endpoints

| Method | Endpoint | Description | Parameters |
|--------|----------|-------------|------------|
| GET | `/` | Homepage with search form | - |
| GET | `/search` | Search for news articles | `searchTerm`, `sortBy` |
| GET | `/wordstats/:query` | Word frequency statistics | `query` (search term) |
| GET | `/source/:id` | Source profile page | `id` (source ID) |
| GET | `/sources` | List all news sources | - |
| GET | `/assets/*file` | Static assets | `file` (asset path) |

---

## Key Components

### Controllers

**NotiLyticsController**
- Manages all HTTP request handling
- Session-based search history management (max 10 queries)
- Integrates with NewsAPIService and SourcesAPIService
- Implements asynchronous request processing

### Models

**Article**
- Represents a news article with title, description, URL, source, etc.
- Includes readability metrics (FK Grade Level, Reading Ease)
- Provides sentiment analysis (positive%, negative%, neutral%)

**NewsAPIService**
- Handles communication with News API
- Implements caching for API responses
- Processes and transforms API data into Article objects

**SourcesAPIService**
- Manages news source data retrieval
- Provides source metadata and categorization

### Utilities

**ReadabilityCalculator**
- Implements Flesch-Kincaid Grade Level calculation
- Calculates Flesch Reading Ease score
- Provides text complexity analysis (syllable counting, word analysis)

**SentimentAnalysis**
- Analyzes text for positive, negative, and neutral sentiment
- Returns percentage-based sentiment scores
- Provides visual emoticon representations

---

## Test Coverage

The project maintains high test coverage with comprehensive unit tests:

### Coverage by Package

- **Controllers**: Full coverage of all HTTP endpoints and request handling
- **Models**: Complete testing of data models and API services
- **Utils**: 100% coverage for ReadabilityCalculator
- **Views**: Template rendering validation

### Excluded from Coverage

Per `build.sbt` configuration, the following are excluded from coverage metrics:
- Router-generated classes
- Reverse routing classes
- JavaScript controllers
- Generated reference classes
- SentimentTuple (data class)
- View results classes

---

## Team

### SOEN 6441 - Advanced Programming Practices
**Fall 2025 - Concordia University**

| Team Member | Team Responsibilities | Individual Task |
|-------------|----------------------|-----------------|
| **Muhammed Zayed Abdul Nasser** | • Team testing coordination<br>• JavaDoc documentation | **Task A: News Sources**<br>List all available news sources with filtering |
| **Wei Huang** | • UI design and implementation<br>• Timezone conversion to EDT<br>• Added hyperlinks to sources<br>• Search history display<br>• README documentation | **Task E: Description Readability**<br>Flesch-Kincaid metrics calculation and testing |
| **Darshil Ketankumar Kalyani** | • Team testing coordination | **Task B: Source Website Profile**<br>Detailed source information pages |
| **Finn Kleckner** | • Added `newsapi.key` and endpoint in `application.conf`<br>• Implemented HTTP requests to fetch ≤10 articles<br>• Parsed JSON response → `Article` objects | **Task D: Article Sentiment**<br>Sentiment analysis implementation and testing |
| **Priya Dharshini Krishnan** | • Retrieved `searchTerm` and `sortBy` from form data<br>• Called `NewsApiService.search()`<br>• Sent data to `results.scala.html` | **Task C: Word Stats**<br>Word frequency analysis across search results |

---

## Course Information

- **Course**: SOEN 6441 - Advanced Programming Practices
- **Semester**: Fall 2025
- **Institution**: Concordia University
- **Project**: NotiLytics - News Analytics Platform

### Learning Objectives Demonstrated

1. **Reactive Programming**: Asynchronous operations using CompletableFuture
2. **Dependency Injection**: Guice framework integration
3. **Test-Driven Development**: Comprehensive unit testing with JUnit and Mockito
4. **MVC Architecture**: Clean separation of concerns using Play Framework
5. **API Integration**: RESTful API consumption and data transformation
6. **Session Management**: User-specific data isolation
7. **Code Quality**: High test coverage and maintainable code structure

---

## Usage Examples

### Basic Search

1. Navigate to `http://localhost:9000`
2. Enter a search term (e.g., "technology")
3. Select sorting option (relevancy, popularity, or date)
4. Click "Search"
5. View results with readability and sentiment metrics

### View Word Statistics

1. After performing a search, click "View Word Statistics"
2. See the top 20 most frequent words from your search results
3. Words are displayed with their frequency counts

### Explore News Sources

1. Click "All Sources" in the navigation
2. Browse available news sources
3. Click on any source to view its profile and recent articles

### Check Readability

Each article displays:
- **FK Grade Level**: Educational level required to understand the text
- **Reading Ease**: How easy the text is to read (0-100 scale)

### Analyze Sentiment

Each article shows:
- Percentage of positive words
- Percentage of negative words
- Percentage of neutral words
- Visual sentiment indicator (happy, neutral, or sad)

---

## Configuration

### Application Settings

Edit `conf/application.conf` to customize:

```conf
# News API Configuration
newsapi.key = "your-api-key"
newsapi.uri = "https://newsapi.org/v2/everything?"

# Play Framework Settings
play.http.secret.key = "your-secret-key"
play.filters.enabled += "play.filters.cors.CORSFilter"
```

### Logging

Configure logging in `conf/logback.xml`:

```xml
<logger name="play" level="INFO" />
<logger name="application" level="DEBUG" />
```

---

## Troubleshooting

### Common Issues

**Issue**: API returns no results
- **Solution**: Check your News API key and ensure you haven't exceeded rate limits

**Issue**: Tests fail with "RuntimeException: boom"
- **Solution**: This is an expected test case for error handling in word statistics

**Issue**: Port 9000 already in use
- **Solution**: Kill existing process or run on different port: `sbt "run 9001"`

**Issue**: Compilation errors
- **Solution**: Ensure Java 17 is being used: `java -version`

---

## License

This project is developed as part of academic coursework at Concordia University. All rights reserved by the team members.

---

## Acknowledgments

- **Play Framework** - Reactive web framework
- **News API** - Real-time news data provider
- **Concordia University** - SOEN 6441 course structure and guidance
- **Teaching Team** - Project requirements and feedback

---

## Contact

For questions or issues related to this project, please contact the team members through Concordia University's official channels.

---

**Last Updated**: November 8, 2025  
**Version**: 1.0-SNAPSHOT  
**Status**: Final Submission
