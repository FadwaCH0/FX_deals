# FX_deals

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Tests](#tests)
- [Code Coverage](#code-coverage)
- [API Documentation](#api-documentation)
- [Performance Testing](#performance-testing)
- [Project Structure](#project-structure)
- [Validation Rules](#validation-rules)
- [Error Handling](#error-handling)
- [Future Improvements](#future-improvements)

---

## 🎯 Overview

This project is a REST API developed as part of a data warehouse for Bloomberg, enabling the analysis and persistence of foreign exchange (FX) transactions. The application ensures data integrity by preventing duplicates and rigorously validating each transaction.

### Main Objectives

- ✅ Accept and validate FX transaction details
- ✅ Prevent duplicate imports
- ✅ Persist all valid transactions in a database
- ✅ Provide robust error handling
  
## 📦 Test Artifacts Available
This repository includes comprehensive test documentation and collections:
   - 📊 Excel Test Cases: POST_Deals_Test_Scenarios.xlsx - Detailed test scenarios
   -  🔬 Postman Collection: FX_Deals_API_Tests_postman_collection.json - Ready-to-use API tests
---

## 🏗️ Architecture

### Technology Stack

- **Backend Framework**: Spring Boot 3.2.1
- **Language**: Java 17.0.0.1
- **Database**: PostgreSQL 
- **Build Tool**: Maven
- **Unit Tests**: JUnit 5 + Mockito
- **Integration Tests**: Spring Boot Test + MockMvc
- **API Tests**: REST Assured / Postman 
- **Code Coverage**: JaCoCo 
- **Performance Tests**: K6

### Layered Architecture

```
┌─────────────────────────────────────┐
│         Controller Layer            │
│    (DealController.java)            │
│  - Request validation               │
│  - HTTP response handling           │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         Service Layer               │
│    (DealService.java)               │
│  - Business logic                   │
│  - Duplicate detection              │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Repository Layer               │
│    (DealRepository.java)            │
│  - Data access                      │
│  - CRUD operations                  │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         Database                    │
│    (PostgreSQL)                     │
└─────────────────────────────────────┘
```

---

## 📦 Prerequisites

- **JDK**: 17 or higher
- **Maven**: 3.8+
- **Database**: PostgreSQL 14+ or MySQL 8+
- **K6**: For performance testing 
- **Postman**: For API testing 

---

## 🚀 Installation

### 1. Clone the repository

```bash
git clone https://github.com/FadwaCH0/FX_deals.git

cd fx-deals
```

### 2. Configure the database

#### Option A: PostgreSQL

```bash
# Create the database
createdb fxdb

# Or via psql
psql -U postgres
CREATE DATABASE fxdb;
```

### 3. Configure application.yml

Modify `src/main/resources/application.yml`:

**For PostgreSQL:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fxdeals
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### 4. Install dependencies

```bash
mvn clean install
```

---

## ⚙️ Configuration

---

## 🎮 Running the Application

### Development Mode

```bash
# With Maven
mvn spring-boot:run

# With a specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The application will be accessible at: `http://localhost:8080`

### Verify the application is running

```bash
curl http://localhost:8080/deals
```

---

## 🧪 Tests

The project includes three types of tests to ensure complete coverage:

### 1. Unit Tests (DealControllerTest.java)

Controller layer tests with mocks:

```bash
mvn test -Dtest=DealControllerTest
```

**Scenarios covered:**
- ✅ Valid deal → 201 Created
- ✅ Deal with empty fields → 400 Bad Request
- ✅ Deal with amount at 0 → 400 Bad Request
- ✅ Duplicate deal → 409 Conflict

### 2. Integration Tests (DealControllerIntegrationTest.java)

Tests with Spring context and database:

```bash
mvn test -Dtest=DealControllerIntegrationTest
```

**Scenarios covered:**
- ✅ Successful insertion into DB
- ✅ Duplicate detection with real DB
- ✅ DB constraint validation
- ✅ Rollback on error

### 3. REST Assured API Tests (DealControllerAPITest.java)

End-to-end REST API tests:

```bash
mvn test -Dtest=DealControllerAPITest
```

**Scenarios covered:**
- ✅ POST /deals with valid data → 201
- ✅ POST /deals with invalid data → 400
- ✅ POST /deals with existing dealId → 409
- ✅ Validation of all required fields

### Run All Tests

```bash
# All tests
mvn clean test

# Tests with detailed report
mvn clean test -DfailIfNoTests=false
```

---

## 📊 Code Coverage

### Generate JaCoCo Report

```bash
# Run tests and generate report
mvn clean test jacoco:report

# HTML report available at:
# target/site/jacoco/index.html
```

<img width="1627" height="441" alt="image" src="https://github.com/user-attachments/assets/757248f7-e9a5-4102-8b8c-dd18fc12a7f0" />

#### Simplified Coverage Table
View by package with key metrics
- Total: 96% (5 instructions missed out of 125)
- Branches: 100% (complete condition coverage)
- Methods: 21/21 (all tested)

**Important Note:**
Why 96% instead of 100%?
- The missing 4% = FxDealsApplication.java class (Spring Boot main)
- Business logic is at 100% (controller, service, entity)

### Current Coverage Report

| Package | Coverage |
|---------|----------|
| `fxdeals.controller` | 100% |
| `fxdeals.service` | 100% |
| `fxdeals.entity` | Excluded |

### JaCoCo Exclusions

The following elements are excluded from coverage (justified):

- **JPA Entities** (`Deal.java`): Simple POJOs with getters/setters
- **Configuration Classes**: Spring bootstrap code
- **Exception handlers**: Generic error handling

---

## 📚 API Documentation

### Main Endpoint: POST /deals

**Description**: Create a new FX transaction

**URL**: `http://localhost:8080/deals`

**Method**: `POST`

**Headers**:
```
Content-Type: application/json
```

### Data Model

```json
{
  "dealId": "string (unique, required)",
  "fromCurrency": "string (required)",
  "toCurrency": "string (required)",
  "amount": "number (> 0, required)",
  "timestamp": "ISO 8601 (optional, auto-generated)"
}
```

### Request Examples

#### ✅ Success - 201 Created

```bash
curl -X POST http://localhost:8080/deals \
  -H "Content-Type: application/json" \
  -d '{
    "dealId": "D001",
    "fromCurrency": "USD",
    "toCurrency": "EUR",
    "amount": 500.70
  }'
```

**Response:**
```
HTTP/1.1 201 Created
Content-Type: text/plain

Deal saved successfully
```

#### ❌ Failure - 400 Bad Request (Validation)

```bash
curl -X POST http://localhost:8080/deals \
  -H "Content-Type: application/json" \
  -d '{
    "dealId": "",
    "fromCurrency": "",
    "toCurrency": "",
    "amount": 0
  }'
```

**Response:**
```json
{
  "dealId": "dealId is required",
  "fromCurrency": "Source currency is required",
  "toCurrency": "Target currency is required",
  "amount": "Transaction amount must be greater than 0"
}
```

#### ❌ Failure - 409 Conflict (Duplicate)

```bash
curl -X POST http://localhost:8080/deals \
  -H "Content-Type: application/json" \
  -d '{
    "dealId": "D001",
    "fromCurrency": "USD",
    "toCurrency": "EUR",
    "amount": 500
  }'
```

**Response:**
```
HTTP/1.1 409 Conflict
Content-Type: text/plain

Deal already exists
```

### HTTP Status Codes

| Code | Meaning | Description |
|------|---------|-------------|
| 201 | Created | Transaction created successfully |
| 400 | Bad Request | Invalid data (validation failed) |
| 409 | Conflict | dealId already exists |
| 500 | Internal Server Error | Unexpected server error |

---

## ⚡ Performance Testing

### K6 Installation

```bash
# macOS
brew install k6

# Linux (Debian/Ubuntu)
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
echo "deb https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
sudo apt-get update
sudo apt-get install k6

# Windows
choco install k6
```

### Run Performance Tests

```bash
# Start the application first
mvn spring-boot:run

# In another terminal, run K6
k6 run deal-api-test.js
```

### K6 Test Configuration

The `deal-api-test.js` file is configured for:
- **10 virtual users** simultaneous
- **Duration**: 30 seconds
- **Endpoint tested**: POST /deals
- **Validation**: Codes 201 (success) or 409 (duplicate)

### Expected Results

```
checks.........................: 100.00% ✓ 300  ✗ 0
data_received..................: 45 kB   1.5 kB/s
data_sent......................: 30 kB   1.0 kB/s
http_req_duration..............: avg=45ms  min=10ms  max=200ms
http_reqs......................: 300     10/s
```

---

## 🗂️ Project Structure

```
fx-deals-api/
│
├── src/
│   ├── main/
│   │   ├── java/fxdeals/
│   │   │   ├── controller/
│   │   │   │   ├── DealController.java              # REST Endpoint
│   │   │   │   └── ValidationExceptionHandler.java  # Error handling
│   │   │   ├── entity/
│   │   │   │   └── Deal.java                        # JPA Entity
│   │   │   ├── repository/
│   │   │   │   └── DealRepository.java              # JPA Interface
│   │   │   ├── service/
│   │   │   │   └── DealService.java                 # Business logic
│   │   │   └── FxDealsApplication.java              # Main class
│   │   └── resources/
│   │       ├── application.properties               # Configuration
│   │       └── application-test.properties          # Test config
│   │
│   └── test/
│       └── java/fxdeals/
│           └── controller/
│               ├── DealControllerTest.java          # Unit tests
│               ├── DealControllerIntegrationTest.java # Integration tests
│               ├── DealControllerAPITest.java       # REST Assured tests
│               └── DealControllerIT.java            # DB tests
│
├── deal-api-test.js                                  # K6 tests
├── FX_Deals_API_Tests_postman_collection.json       # Postman collection
├── pom.xml                                          # Maven configuration
└── README.md                                        # Documentation
```

---

## ✅ Validation Rules

### Required Fields

All the following fields are required:

1. **dealId** (String)
   - ❌ Cannot be empty or null
   - ✅ Must be unique in the database
   - 💡 Example: "D001", "FX-2024-001"

2. **fromCurrency** (String)
   - ❌ Cannot be empty or null
   - ✅ Should respect ISO 4217 (3 letters)
   - 💡 Example: "USD", "EUR", "GBP"

3. **toCurrency** (String)
   - ❌ Cannot be empty or null
   - ✅ Should respect ISO 4217 (3 letters)
   - 💡 Example: "EUR", "JPY", "CHF"

4. **amount** (BigDecimal)
   - ❌ Cannot be null
   - ❌ Must be > 0.00
   - ✅ Supports decimals
   - 💡 Example: 1000.50, 99.99

5. **timestamp** (Instant)
   - ✅ Auto-generated if not provided
   - ❌ Cannot be in the future (@PastOrPresent)
   - 💡 Format: ISO 8601 (2024-01-15T10:30:00Z)

### Business Rules

- **Uniqueness**: A dealId can only be imported once
- **No rollback**: Each valid transaction is persisted immediately
- **Atomicity**: A transaction is either completely accepted or rejected

---

## 🛠️ Error Handling

### Types of Errors Handled

1. **Validation Errors (400)**
   - Missing fields
   - Incorrect format
   - Business constraints not met

2. **Duplicate Errors (409)**
   - dealId already exists in the database

3. **Server Errors (500)**
   - Unanticipated exceptions
   - DB connection issues
   - Technical errors

### Logging

The application uses SLF4J with Logback:

```java
// DEBUG level for development
logging.level.fxdeals=DEBUG

// INFO level for production
logging.level.fxdeals=INFO
```

**Logs generated:**
- ✅ Each deal creation attempt
- ✅ Duplicate detection
- ✅ Validation errors
- ✅ Technical exceptions

---

## 🔄 Postman Tests

### Import the Collection

1. Open Postman
2. File → Import
3. Select `FX_Deals_API_Tests_postman_collection.json`

### Available Scenarios

The Postman collection includes the following tests:

1. **Create Valid Deal** - Successful creation
2. **dealId already exists** - Duplicate test
3. **Missing Required Fields** - Empty fields validation
4. **Amount Zero** - Amount validation
5. **Future Date** - Future date validation
6. **Very Large Amount** - Extreme value test
7. **Missing dealId Only** - Missing field test
8. **Missing Amount Only** - Missing amount test

### Run the Collection

```bash
# Via Newman (Postman CLI)
npm install -g newman
newman run FX_Deals_API_Tests_postman_collection.json
```

---

## 📈 Requirements to Tests Mapping

### Functional Requirements

| Requirement | Unit Tests | Integration Tests | API Tests |
|-------------|-----------|-------------------|-----------|
| Accept valid deal | ✅ DealControllerTest | ✅ DealControllerIntegrationTest | ✅ DealControllerAPITest |
| Validate structure | ✅ whenEmptyDeal_thenReturnsValidationErrors | ✅ whenDealIdEmpty_thenReturnsBadRequest | ✅ whenDealInvalid_thenReturnsBadRequest |
| Prevent duplicates | ✅ whenDealAlreadyExists_thenReturnsConflict | ✅ whenDealAlreadyExists_thenReturnsConflict | ✅ whenDealAlreadyExists_thenReturnsConflict |
| Validate amount > 0 | ✅ whenAmountZero_thenReturnsDecimalMinError | ✅ whenAmountZero_thenReturnsBadRequest | ✅ Included in whenDealInvalid |
| Persist in DB | - | ✅ DealControllerIT.testSaveDeal | ✅ Implicit in all API tests |

### Coverage Tests

```bash
# Generate mapping report
mvn clean test jacoco:report

# The report will show:
# - Code lines covered by test type
# - Conditional branches tested
# - Methods called
```

---
