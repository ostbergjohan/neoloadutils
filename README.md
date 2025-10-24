<<<<<<< HEAD
# NeoLoad Utils

[![Docker Hub](https://img.shields.io/badge/Docker%20Hub-johanostberg%2Fneoloadutils-blue)](https://hub.docker.com/repository/docker/johanostberg/neoloadutils)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen)](https://spring.io/projects/spring-boot)

A high-performance helper application designed to provide additional functionality during performance testing with **NeoLoad Test-as-Code**. Also compatible with other performance testing tools like **JMeter** and **K6**.

---


## 🚀 Features

- **Pacing Control** - Manage iteration pacing with high-precision timing
- **Dynamic Date/Time Generation** - Create timestamps with flexible formatting and weekday filtering
- **SQL Query Execution** - Execute queries against multiple database types (MySQL, PostgreSQL, Oracle, SQL Server, SQLite)
- **UUID Generation** - Generate unique identifiers for test data
- **NeoLoad YAML Generator** - Automatically create NeoLoad test configurations
- **HTTP Request Conversion** - Transform raw HTTP requests to structured JSON
- **URL Management** - Store and retrieve test URLs with descriptions
- **Multi-threaded Performance** - Optimized for high-volume test execution

---


## 📡 API Endpoints
=======
# 🛠️ NeoLoad Utils

[![GitHub](https://img.shields.io/badge/GitHub-ostbergjohan%2Fneoloadutils-black)](https://github.com/ostbergjohan/neoloadutils)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-green)](https://spring.io/projects/spring-boot)

A comprehensive helper application providing additional functionality for performance testing with NeoLoad Test-as-Code. Also compatible with other performance testing tools like JMeter and k6.

## 📋 Table of Contents

- [Features](#-features)
- [Quick Start](#-quick-start)
- [API Documentation](#-api-documentation)
- [Core Features](#-core-features)
  - [Pacing Control](#1-pacing-control)
  - [Date/Time Parameters](#2-datetime-parameters)
  - [Database Operations](#3-database-operations)
  - [UUID Generation](#4-uuid-generation)
  - [URL Management](#5-url-management)
  - [HTTP Conversion](#6-http-conversion)
  - [YAML Generation](#7-neoload-yaml-generation)
- [Configuration](#%EF%B8%8F-configuration)
- [Deployment](#-deployment)
- [CI/CD Integration](#-cicd-integration)
- [Complete Examples](#-complete-examples)

## ✨ Features

- 🕐 **Intelligent Pacing Control** - Precise iteration timing for realistic load testing
- 📅 **Dynamic Date Generation** - Generate dates with custom formats and business day logic
- 🗄️ **Multi-Database Support** - Execute SQL queries across MySQL, PostgreSQL, Oracle, SQL Server, and SQLite
- 🔄 **HTTP Request Conversion** - Convert raw HTTP requests to structured JSON
- 📝 **YAML Configuration Generator** - Auto-generate NeoLoad YAML configurations from JSON
- 🆔 **UUID Generation** - Generate unique identifiers for test correlation
- 📊 **OpenAPI Documentation** - Interactive API documentation via Swagger UI

## 🚀 Quick Start

### Run with Docker

```bash
docker run -d \
  -p 8080:8080 \
  --name neoloadutils \
  your-registry/neoloadutils:latest
```

### Run Locally

```bash
mvn spring-boot:run
```

## 📚 API Documentation

Once the application is running, access the interactive API documentation:

- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **Health Check**: `http://localhost:8080/healthcheck`

## 🎯 Core Features

### 1. Pacing Control

Control the timing between iterations in your performance tests with precise pacing.

#### How It Works

1. **Start Pacing**: Call at the beginning of an iteration to get a UUID
2. **End Pacing**: Call at the end with the UUID and desired total time

#### API Endpoints

**Start Pacing**
```http
GET /setpacing
```

**Response:**
```json
{
  "uuid": "2602c87e-b736-4d8c-b7fe-ba62916875fd"
}
```

**End Pacing**
```http
GET /getpacing?guid=2602c87e-b736-4d8c-b7fe-ba62916875fd&totalPacingTimeMillis=1000
```

**Response:**
```json
{
  "uuid": "2602c87e-b736-4d8c-b7fe-ba62916875fd",
  "duration": 450
}
```

#### NeoLoad Example

```yaml
name: pacing_example

variables:
- constant:
    name: pPacing
    value: 1000  # 1 second pacing in milliseconds

sla_profiles:
- name: sla
  thresholds:
    - error-rate warn >= 0.1% fail >= 0.2% per test
>>>>>>> cebad8db68d09aeb5adf440e6badb341a47ee14f

### 1. Pacing Control

<<<<<<< HEAD
Control iteration pacing in your performance tests with microsecond precision.

#### **Start Pacing**

Captures the start timestamp for pacing calculations.

**Endpoint:** `GET /setpacing`

**Example:**
```bash
curl http://localhost:8080/setpacing
```

**Response:**
```json
{
  "uuid": "2602c87e-b736-4d8c-b7fe-ba62916875fd"
}
```

---

#### **End Pacing**

Calculates remaining pacing time and sleeps if necessary to maintain desired pacing.

**Endpoint:** `GET /getpacing`

**Parameters:**
- `guid` (required) - UUID from setpacing call
- `totalPacingTimeMillis` (required) - Total pacing time in milliseconds

**Example:**
```bash
curl "http://localhost:8080/getpacing?guid=2602c87e-b736-4d8c-b7fe-ba62916875fd&totalPacingTimeMillis=1000"
```

**Response:**
```json
{
  "uuid": "2602c87e-b736-4d8c-b7fe-ba62916875fd",
  "duration": 234
}
```

> **⚠️ Platform Considerations:**  
> Be aware of platform timeout limitations. For example, OpenShift routes have a default timeout of 30 seconds. Pacing durations should not exceed platform limits.

> **💡 NeoLoad Trend View:**  
> In NeoLoad Web's trend view, pacing requests should be excluded from analysis as they are utility calls and may skew performance metrics.

---

### 2. Date/Time Parameters

Generate dynamic date/time parameters with flexible formatting and business day filtering.

#### **Standard Date Generation**

**Endpoint:** `POST /getparameter`

**Request Body:**
```json
{
=======
scenarios:
- name: pacing_scenario
  description: Example scenario with pacing
  sla_profile: sla
  populations:
    - name: pop1
      constant_load:
        users: 5
        duration: 2m

user_paths:
- name: example_userpath
  actions:
    steps:
      # Start pacing measurement
      - request:
          url: http://neoloadutils:8080/setpacing
          extractors:
            - name: pUUID
              jsonpath: $.uuid

      # Your actual test transactions
      - transaction:
          name: transaction_1
          description: API call to application under test
          steps:
            - request:
                url: https://api.example.com/endpoint

      # End pacing - waits for remaining time
      - request:
          url: http://neoloadutils:8080/getpacing?guid=${pUUID}&totalPacingTimeMillis=${pPacing}
```

#### Platform Considerations

> ⚠️ **Important**: Be aware of platform timeout limitations. For example, OpenShift routes have a default timeout of 30 seconds (configurable). Ensure your pacing durations don't exceed these limits.

#### Trend View Note

In NeoLoad Web trend views, exclude pacing requests from analysis as they may skew performance metrics.

---

### 2. Date/Time Parameters

Generate dynamic dates with custom formatting and business day logic.

#### API Endpoint

```http
POST /getparameter
Content-Type: application/json
```

#### Standard Date Generation

**Request:**
```json
{
>>>>>>> cebad8db68d09aeb5adf440e6badb341a47ee14f
  "dateformatter": "yyyy-MM-dd HH:mm:ss z",
  "daysToAddOrSubtract": "0"
}
```

**Response:**
```json
{
  "date": "2024-10-15 13:58:10 CEST"
}
```

<<<<<<< HEAD
---

#### **Weekday-Only Date Generation**

Automatically skips weekends to generate business day dates.

**Request Body:**
```json
{
  "dateformatter_weekdays": "yyyy-MM-dd HH:mm:ss z",
  "daysToAddOrSubtract": "5"
}
```

**Response:**
```json
{
  "date": "2024-10-22 13:58:10 CEST"
}
```

---

#### **DateTimeFormatter Patterns**

| Symbol | Description | Example |
|--------|-------------|---------|
| `yyyy` | Year | 2024 |
| `MM` | Month (01-12) | 10 |
| `dd` | Day of month (01-31) | 15 |
| `HH` | Hour (24-hour) | 13 |
| `mm` | Minute | 58 |
| `ss` | Second | 10 |
| `EEEE` | Day of week | Monday |
| `MMMM` | Full month name | October |
| `z` | Time zone | CEST |

For more patterns, see the [Java DateTimeFormatter documentation](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html).

---

### 3. SQL Execution

Execute SQL queries against various databases and retrieve results in JSON format.

#### **Execute SELECT Query**

**Endpoint:** `POST /executeSQL`

**Request Body:**
```json
{
  "query": "SELECT * FROM employees WHERE department = 'Engineering'",
=======
#### Weekday-Only Date Generation

Skips weekends when calculating dates:

**Request:**
```json
{
  "dateformatter_weekdays": "yyyy-MM-dd HH:mm:ss z",
  "daysToAddOrSubtract": "5"
}
```

**Response:**
```json
{
  "date": "2024-10-22 13:58:10 CEST"
}
```

#### Common Date Format Patterns

| Pattern | Description | Example |
|---------|-------------|---------|
| `yyyy` | Year | 2024 |
| `MM` | Month (01-12) | 10 |
| `dd` | Day of month | 15 |
| `HH` | Hour (24-hour) | 13 |
| `mm` | Minute | 58 |
| `ss` | Second | 10 |
| `EEEE` | Day of week | Monday |
| `MMMM` | Full month name | October |
| `z` | Time zone | CEST |

#### NeoLoad Example

```yaml
name: date_parameter_example

sla_profiles:
  - name: sla
    thresholds:
      - error-rate warn >= 0.1% fail >= 0.2% per test

populations: 
  - name: pop1
    user_paths:
      - name: example_userpath
        distribution: 100%

scenarios:
  - name: date_scenario
    description: Example with dynamic dates
    sla_profile: sla
    populations:
      - name: pop1
        constant_load:
          users: 5
          duration: 5m

user_paths:
  - name: example_userpath
    actions:
      steps:
        # Generate dynamic date
        - transaction:
            name: generate_date
            description: Generate timestamp for API call
            steps:
              - request:
                  url: http://neoloadutils:8080/getparameter
                  method: POST
                  extractors:
                    - name: pDate
                      jsonpath: $.date
                  body: |
                    {
                      "dateformatter": "yyyy-MM-dd HH:mm:ss z",
                      "daysToAddOrSubtract": "0"
                    }

        # Use generated date in API call
        - transaction:
            name: api_call_with_date
            description: API call using dynamic date parameter
            steps:
              - request:
                  url: https://api.example.com/data?date=${pDate}
```

---

### 3. Database Operations

Execute SQL queries against multiple database types and retrieve results in JSON format.

#### Supported Databases

- ✅ MySQL
- ✅ PostgreSQL
- ✅ Oracle
- ✅ SQL Server
- ✅ SQLite

#### Execute SELECT Queries

```http
POST /executeSQL
Content-Type: application/json
```

**Request:**
```json
{
  "query": "SELECT id, name, department, salary FROM employees WHERE status = 'active'",
>>>>>>> cebad8db68d09aeb5adf440e6badb341a47ee14f
  "jdbc": "jdbc:mysql://localhost:3306/mydatabase",
  "user": "dbuser",
  "password": "dbpassword",
  "numRows": 10,
  "randomize": true
}
```

<<<<<<< HEAD
**Parameters:**
- `query` (string) - SQL SELECT query to execute
- `jdbc` (string) - JDBC connection URL
- `user` (string) - Database username
- `password` (string) - Database password
- `numRows` (integer) - Number of rows to return (≥ 1)
- `randomize` (boolean) - Randomize result order

**Response:**
```json
{
  "columns": ["id", "name", "department", "email"],
  "values": [
    [1, "Alice", "Engineering", "alice@example.com"],
    [2, "Bob", "Engineering", "bob@example.com"],
    [5, "Eve", "Engineering", "eve@example.com"],
    [7, "Grace", "Engineering", "grace@example.com"],
    [10, "Jack", "Engineering", "jack@example.com"]
=======
**Response:**
```json
{
  "columns": ["id", "name", "department", "salary"],
  "values": [
    [1, "Alice", "HR", 75000],
    [2, "Bob", "IT", 85000],
    [3, "Charlie", "Finance", 90000],
    [4, "Diana", "Marketing", 70000],
    [5, "Eve", "Operations", 80000],
    [6, "Frank", "Sales", 72000],
    [7, "Grace", "Engineering", 95000],
    [8, "Hank", "Logistics", 68000],
    [9, "Ivy", "Legal", 88000],
    [10, "Jack", "Product", 92000]
>>>>>>> cebad8db68d09aeb5adf440e6badb341a47ee14f
  ]
}
```

<<<<<<< HEAD
---

#### **Execute Non-SELECT Queries**

For INSERT, UPDATE, DELETE operations.

**Endpoint:** `POST /executeNonSelectSQL`

**Request Body:**
```json
{
  "query": "UPDATE employees SET status = 'active' WHERE id = 1; DELETE FROM temp_data WHERE created < NOW() - INTERVAL 7 DAY;",
  "jdbc": "jdbc:mysql://localhost:3306/mydatabase",
  "user": "dbuser",
  "password": "dbpassword"
}
```

**Response:**
```json
{
  "status": "success",
  "executed": [
    {"updateCount": 1},
    {"updateCount": 15}
  ],
  "timeTakenSeconds": 0.34
}
```

---

#### **Supported Databases**

| Database | JDBC Driver | Example URL |
|----------|-------------|-------------|
| MySQL | `mysql-connector-j` | `jdbc:mysql://host:3306/db` |
| PostgreSQL | `postgresql` | `jdbc:postgresql://host:5432/db` |
| Oracle | `ojdbc11` | `jdbc:oracle:thin:@host:1521:sid` |
| SQL Server | `mssql-jdbc` | `jdbc:sqlserver://host:1433;databaseName=db` |
| SQLite | `sqlite-jdbc` | `jdbc:sqlite:/path/to/database.db` |
=======
#### Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `query` | String | ✅ Yes | SQL SELECT query to execute |
| `jdbc` | String | ✅ Yes | JDBC connection URL |
| `user` | String | ✅ Yes | Database username |
| `password` | String | ✅ Yes | Database password |
| `numRows` | Integer | ✅ Yes | Number of rows to return (≥ 1) |
| `randomize` | Boolean | ✅ Yes | Randomize row order |

#### Execute Non-SELECT Queries

For INSERT, UPDATE, DELETE operations:

```http
POST /executeNonSelectSQL
Content-Type: application/json
```

**Request:**
```json
{
  "query": "INSERT INTO test_runs (name, status, duration) VALUES ('Load Test 1', 'completed', 300); UPDATE test_runs SET reviewed = true WHERE status = 'completed'",
  "jdbc": "jdbc:postgresql://localhost:5432/testdb",
  "user": "admin",
  "password": "securepass"
}
```

**Response:**
```json
{
  "executed": [
    {"updateCount": 1},
    {"updateCount": 5}
  ],
  "status": "success",
  "timeTakenSeconds": 0.15
}
```

#### JDBC Connection Strings

**MySQL:**
```
jdbc:mysql://hostname:3306/database?useSSL=false
```

**PostgreSQL:**
```
jdbc:postgresql://hostname:5432/database
```

**Oracle:**
```
jdbc:oracle:thin:@hostname:1521:sid
```

**SQL Server:**
```
jdbc:sqlserver://hostname:1433;databaseName=database
```

**SQLite:**
```
jdbc:sqlite:/path/to/database.db
```
>>>>>>> cebad8db68d09aeb5adf440e6badb341a47ee14f

---

### 4. UUID Generation

<<<<<<< HEAD
Generate RFC 4122 compliant UUIDs for test data.

**Endpoint:** `GET /getuuid`

**Example:**
```bash
curl http://localhost:8080/getuuid
=======
Generate unique identifiers for test correlation and tracking.

#### API Endpoint

```http
GET /getuuid
>>>>>>> cebad8db68d09aeb5adf440e6badb341a47ee14f
```

**Response:**
```json
{
  "uuid": "a003cac8-49a3-498d-9559-ca27a307a56b"
}
```

<<<<<<< HEAD
---

### 5. URL Management

Store and retrieve test URLs with descriptions.

#### **Add URL**

**Endpoint:** `GET /addurl`

**Parameters:**
- `url` (string) - URL to store
- `explanation` (string) - Description/explanation

**Example:**
```bash
curl "http://localhost:8080/addurl?url=https://api.example.com/users&explanation=User%20API%20endpoint"
```

**Response:**
```
URL added successfully!
```

---

#### **List URLs**

**Endpoint:** `GET /geturl`

Returns an HTML page displaying all stored URLs sorted by explanation.

**Example:**
```bash
curl http://localhost:8080/geturl
```
=======
#### Use Cases

- Correlate requests across transactions
- Generate unique test data identifiers
- Track individual test iterations
- Create unique session IDs

---

### 5. URL Management

Store and retrieve URLs with explanations for easy reference.

#### Add URL

```http
GET /addurl?url=https://api.example.com/endpoint&explanation=Production API Endpoint
```

**Response:**
```
URL added successfully!
```

#### Get URLs

```http
GET /geturl
```

Returns an HTML page with all stored URLs in a sortable table format.
>>>>>>> cebad8db68d09aeb5adf440e6badb341a47ee14f

---

### 6. HTTP Conversion

<<<<<<< HEAD
Convert raw HTTP requests to structured JSON format.

**Endpoint:** `POST /convertHTTP`

**Request Body:**
```
POST /api/users HTTP/1.1
Host: api.example.com
Content-Type: application/json
Authorization: Bearer xyz123

=======
Convert raw HTTP requests into structured JSON format for easier processing.

#### API Endpoint

```http
POST /convertHTTP
Content-Type: text/plain
```

**Request Body:**
```
POST /api/v1/users HTTP/1.1
Host: api.example.com
Content-Type: application/json
Authorization: Bearer token123

>>>>>>> cebad8db68d09aeb5adf440e6badb341a47ee14f
{
  "name": "John Doe",
  "email": "john@example.com"
}
```
<<<<<<< HEAD
=======

**Response:**
```json
{
  "name": "transaction name",
  "url": "https://api.example.com/api/v1/users",
  "method": "POST",
  "body": "{\n  \"name\": \"John Doe\",\n  \"email\": \"john@example.com\"\n}",
  "headers": {
    "Host": "api.example.com",
    "Content-Type": "application/json",
    "Authorization": "Bearer token123"
  }
}
```

---

### 7. NeoLoad YAML Generation

Generate complete NeoLoad YAML configuration files from JSON payloads.

#### API Endpoint

```http
POST /NeoLoadYamlGenerator
Content-Type: application/json
```

#### Request Example
>>>>>>> cebad8db68d09aeb5adf440e6badb341a47ee14f

**Response:**
```json
{
<<<<<<< HEAD
  "name": "transaction name",
  "url": "https://api.example.com/api/users",
  "method": "POST",
  "body": "{\n  \"name\": \"John Doe\",\n  \"email\": \"john@example.com\"\n}",
  "headers": {
    "Host": "api.example.com",
    "Content-Type": "application/json",
    "Authorization": "Bearer xyz123"
  }
}
```

---

### 7. NeoLoad YAML Generation

Generate NeoLoad test configuration files from JSON input.

**Endpoint:** `POST /NeoLoadYamlGenerator`

**Request Body:**
```json
{
  "name": "User API Test",
  "scenario": "LoadTest",
  "pacing": "1000",
  "users": 10,
  "duration": 5,
  "userpathname": "user_api_path",
  "transactions": [
    {
      "name": "Get Users",
      "url": "https://api.example.com/users",
      "method": "GET",
      "headers": {
        "Authorization": "Bearer token123"
      },
      "assertion": ".*\"status\":\"success\".*"
    },
    {
      "name": "Create User",
      "url": "https://api.example.com/users",
      "method": "POST",
      "body": "{\"name\":\"Test User\"}",
      "headers": {
        "Content-Type": "application/json"
      },
      "extractors": [
        {
          "name": "userId",
          "jsonpath": "$.id"
        }
      ]
=======
  "name": "API Load Test",
  "scenario": "production_load",
  "pacing": "2000",
  "users": 50,
  "duration": 10,
  "userpathname": "api_userpath",
  "transactions": [
    {
      "name": "Login",
      "url": "https://api.example.com/auth/login",
      "method": "POST",
      "body": "{\"username\": \"testuser\", \"password\": \"pass123\"}",
      "headers": {
        "Content-Type": "application/json"
      },
      "extractors": [
        {
          "name": "authToken",
          "jsonpath": "$.token"
        }
      ],
      "assertion": ".*success.*"
    },
    {
      "name": "Get User Data",
      "url": "https://api.example.com/user/profile",
      "method": "GET",
      "headers": {
        "Authorization": "Bearer ${authToken}"
      },
      "assertion": ".*200.*"
    }
  ],
  "files": [
    {
      "name": "userdata",
      "column_names": ["username", "password"],
      "path": "test-data/users.csv"
>>>>>>> cebad8db68d09aeb5adf440e6badb341a47ee14f
    }
  ]
}
```

<<<<<<< HEAD
**Response:** YAML file download

---

## 📚 Examples

### Example 1: NeoLoad Pacing Implementation

```yaml
name: pacing_example

variables:
  - constant:
      name: pPacing
      value: 1000  # 1 second pacing

sla_profiles:
  - name: sla
    thresholds:
      - error-rate warn >= 0.1% fail >= 0.2% per test

populations:
  - name: pop1
    user_paths:
      - name: example_userpath
        distribution: 100%

scenarios:
  - name: LoadTest
    description: Example scenario with pacing
    sla_profile: sla
    populations:
      - name: pop1
        constant_load:
          users: 5
          duration: 2m

user_paths:
  - name: example_userpath
    actions:
      steps:
        # Start pacing measurement
        - request:
            url: http://localhost:8080/setpacing
            extractors:
              - name: pUUID
                jsonpath: $.uuid

        # Your actual test transaction
        - transaction:
            name: API Call
            description: Test API endpoint
            steps:
              - request:
                  url: https://api.example.com/data
                  method: GET

        # End pacing - waits for remaining time
        - request:
            url: http://localhost:8080/getpacing?guid=${pUUID}&totalPacingTimeMillis=${pPacing}
=======
#### Response (YAML)

```yaml
#NeoLoadYamlGenerator - 2024-10-15 14:30:00
name: API Load Test

variables:
- constant:
    name: pPacing
    value: 2000
- file:
    name: userdata
    is_first_line_column_names: true
    start_from_line: 1
    delimiter: ','
    path: test-data/users.csv
    change_policy: each_iteration
    scope: global
    order: any
    out_of_value: cycle

sla_profiles:
- name: sla
  thresholds:
  - error-rate warn >= 0.1% fail >= 0.2% per test

populations:
- name: pop_api_userpath
  user_paths:
  - name: api_userpath
    distribution: 100%

scenarios:
- name: production_load
  description: update
  sla_profile: sla
  populations:
  - name: pop_api_userpath
    constant_load:
      users: 50
      duration: 10m
- name: production_load_debug
  description: Debug scenario with 1 iteration
  sla_profile: sla
  populations:
  - name: pop_api_userpath
    constant_load:
      users: 1
      duration: 1 iteration

user_paths:
- name: api_userpath
  actions:
    steps:
    - request:
        url: http://neoloadutils:8080/setpacing
        extractors:
        - name: pUUID
          jsonpath: $.uuid
    - transaction:
        name: Login
        description: generated
        sla_profile: sla
        steps:
        - request:
            url: https://api.example.com/auth/login
            method: POST
            body: '{"username": "testuser", "password": "pass123"}'
            headers:
            - Content-Type: application/json
            extractors:
            - name: authToken
              jsonpath: $.token
              extract_once: 'true'
            assertions:
            - contains: .*success.*
              regexp: true
    - transaction:
        name: Get User Data
        description: generated
        steps:
        - request:
            url: https://api.example.com/user/profile
            method: GET
            headers:
            - Authorization: Bearer ${authToken}
            assertions:
            - contains: .*200.*
              regexp: true
    - request:
        url: http://neoloadutils:8080/getpacing?guid=${pUUID}&totalPacingTimeMillis=${pPacing}
>>>>>>> cebad8db68d09aeb5adf440e6badb341a47ee14f
```

---

<<<<<<< HEAD
### Example 2: Dynamic Date Parameters

```yaml
name: date_parameter_example

user_paths:
  - name: date_test_path
    actions:
      steps:
        # Generate date parameter
        - transaction:
            name: Generate Date
            description: Creates dynamic date parameter
            steps:
              - request:
                  url: http://localhost:8080/getparameter
                  method: POST
                  body: |-
                    {
                      "dateformatter": "yyyy-MM-dd",
                      "daysToAddOrSubtract": "-7"
                    }
                  extractors:
                    - name: pDate
                      jsonpath: $.date

        # Use date in API call
        - transaction:
            name: Query with Date
            description: API call with dynamic date
            steps:
              - request:
                  url: https://api.example.com/reports?date=${pDate}
                  method: GET
=======
## ⚙️ Configuration

### Application Properties

Create `src/main/resources/application.properties`:

```properties
# Server configuration
server.port=8080

# Logging
logging.level.root=INFO
logging.level.com.neoloadutils=DEBUG

# Spring configuration
spring.main.allow-bean-definition-overriding=false
>>>>>>> cebad8db68d09aeb5adf440e6badb341a47ee14f
```

---

<<<<<<< HEAD
### Example 3: SQL Data Retrieval

```yaml
name: sql_data_example

user_paths:
  - name: sql_test_path
    actions:
      steps:
        # Fetch test data from database
        - transaction:
            name: Get Test Data
            description: Retrieve employee data
            steps:
              - request:
                  url: http://localhost:8080/executeSQL
                  method: POST
                  body: |-
                    {
                      "query": "SELECT id, email FROM users WHERE status = 'active'",
                      "jdbc": "jdbc:mysql://db.example.com:3306/testdb",
                      "user": "testuser",
                      "password": "testpass",
                      "numRows": 100,
                      "randomize": true
                    }
                  extractors:
                    - name: userId
                      jsonpath: $.values[0][0]
                    - name: userEmail
                      jsonpath: $.values[0][1]

        # Use extracted data in test
        - transaction:
            name: Test User Profile
            description: Access user profile with DB data
            steps:
              - request:
                  url: https://api.example.com/users/${userId}
                  method: GET
```

---

### Docker Compose

```yaml
version: '3.8'

services:
  neoloadutils:
    image: johanostberg/neoloadutils:latest
    container_name: neoloadutils
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=production
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/healthcheck"]
      interval: 30s
      timeout: 10s
      retries: 3
```

---

## 🛠️ Installation

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

### Build from Source

```bash
# Clone the repository
git clone https://github.com/ostbergjohan/neoloadutils.git
cd neoloadutils

# Build with Maven
mvn clean install

# Run the application
java -jar target/NeoLoadUtils-0.0.1-SNAPSHOT.jar
```

---


## ⚙️ Configuration

### Application Properties

Create `application.properties` or `application.yml`:

```yaml
# Server Configuration
server:
  port: 8080

# Logging
logging:
  level:
    com.neoloadutils: INFO
    org.springframework: WARN

# Spring Boot
spring:
  application:
    name: NeoLoadUtils
=======
## 🐳 Deployment

### Docker

#### Build Image

```bash
docker build -t neoloadutils:latest .
```

#### Run Container

```bash
docker run -d \
  -p 8080:8080 \
  --name neoloadutils \
  --restart unless-stopped \
  neoloadutils:latest
```

### Kubernetes

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: neoloadutils
  labels:
    app: neoloadutils
spec:
  replicas: 2
  selector:
    matchLabels:
      app: neoloadutils
  template:
    metadata:
      labels:
        app: neoloadutils
    spec:
      containers:
      - name: neoloadutils
        image: neoloadutils:latest
        ports:
        - containerPort: 8080
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /healthcheck
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /healthcheck
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: neoloadutils
spec:
  selector:
    app: neoloadutils
  ports:
  - protocol: TCP
    port: 8080
    targetPort: 8080
  type: ClusterIP
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: neoloadutils
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  rules:
  - host: neoloadutils.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: neoloadutils
            port:
              number: 8080
```

### OpenShift

```yaml
apiVersion: apps.openshift.io/v1
kind: DeploymentConfig
metadata:
  name: neoloadutils
spec:
  replicas: 2
  selector:
    app: neoloadutils
  template:
    metadata:
      labels:
        app: neoloadutils
    spec:
      containers:
      - name: neoloadutils
        image: neoloadutils:latest
        ports:
        - containerPort: 8080
        livenessProbe:
          httpGet:
            path: /healthcheck
            port: 8080
          initialDelaySeconds: 30
        readinessProbe:
          httpGet:
            path: /healthcheck
            port: 8080
          initialDelaySeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: neoloadutils
spec:
  selector:
    app: neoloadutils
  ports:
  - port: 8080
    targetPort: 8080
---
apiVersion: route.openshift.io/v1
kind: Route
metadata:
  name: neoloadutils
  annotations:
    haproxy.router.openshift.io/timeout: 60s  # Adjust timeout for pacing
spec:
  to:
    kind: Service
    name: neoloadutils
  port:
    targetPort: 8080
  tls:
    termination: edge
>>>>>>> cebad8db68d09aeb5adf440e6badb341a47ee14f
```

---

<<<<<<< HEAD
### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Server port | 8080 |
| `SPRING_PROFILES_ACTIVE` | Active profile | default |
| `LOG_LEVEL` | Logging level | INFO |

---


## 🔒 Security Considerations

⚠️ **Important Security Notes:**

1. **SQL Injection Risk** - The `/executeSQL` endpoint executes raw SQL. Use only in test environments.
2. **Database Credentials** - Credentials are passed in request bodies. Use HTTPS in production.
3. **Authentication** - No built-in authentication. Implement API gateway or Spring Security if needed.
4. **Rate Limiting** - Consider implementing rate limiting for production deployments.

---

**Made with ❤️ for Performance Testing**
=======
## 🔄 CI/CD Integration

### Jenkins Pipeline

```groovy
pipeline {
    agent any
    
    environment {
        NEOLOADUTILS_URL = 'http://neoloadutils:8080'
    }
    
    stages {
        stage('Setup Test Data') {
            steps {
                script {
                    // Generate dynamic date
                    def dateResponse = sh(
                        script: """
                            curl -s -X POST ${NEOLOADUTILS_URL}/getparameter \
                            -H 'Content-Type: application/json' \
                            -d '{"dateformatter": "yyyy-MM-dd", "daysToAddOrSubtract": "0"}'
                        """,
                        returnStdout: true
                    ).trim()
                    
                    def date = readJSON text: dateResponse
                    env.TEST_DATE = date.date
                    
                    echo "Using test date: ${env.TEST_DATE}"
                }
            }
        }
        
        stage('Generate NeoLoad Config') {
            steps {
                script {
                    def payload = readFile('neoload-config.json')
                    
                    sh """
                        curl -X POST ${NEOLOADUTILS_URL}/NeoLoadYamlGenerator \
                        -H 'Content-Type: application/json' \
                        -d '${payload}' \
                        -o neoload-test.yaml
                    """
                }
            }
        }
        
        stage('Run Performance Test') {
            steps {
                sh 'neoload run --as-code neoload-test.yaml'
            }
        }
    }
}
```

### GitLab CI

```yaml
stages:
  - prepare
  - test

generate_config:
  stage: prepare
  script:
    - |
      curl -X POST http://neoloadutils:8080/NeoLoadYamlGenerator \
        -H 'Content-Type: application/json' \
        -d @neoload-config.json \
        -o neoload-test.yaml
  artifacts:
    paths:
      - neoload-test.yaml

performance_test:
  stage: test
  dependencies:
    - generate_config
  script:
    - neoload run --as-code neoload-test.yaml
```

---

## 📖 Complete Examples

### Example 1: Load Test with Database Data

```yaml
name: db_data_load_test

sla_profiles:
- name: sla
  thresholds:
    - error-rate warn >= 0.1% fail >= 0.2% per test

populations:
- name: pop1
  user_paths:
    - name: db_userpath
      distribution: 100%

scenarios:
- name: db_scenario
  sla_profile: sla
  populations:
    - name: pop1
      constant_load:
        users: 10
        duration: 5m

user_paths:
- name: db_userpath
  actions:
    steps:
      # Fetch test data from database
      - transaction:
          name: get_test_data
          steps:
            - request:
                url: http://neoloadutils:8080/executeSQL
                method: POST
                extractors:
                  - name: userId
                    jsonpath: $.values[0][0]
                  - name: userName
                    jsonpath: $.values[0][1]
                body: |
                  {
                    "query": "SELECT id, name FROM users WHERE active = 1",
                    "jdbc": "jdbc:mysql://db.example.com:3306/testdb",
                    "user": "testuser",
                    "password": "testpass",
                    "numRows": 1,
                    "randomize": true
                  }
      
      # Use database data in API call
      - transaction:
          name: api_call
          steps:
            - request:
                url: https://api.example.com/user/${userId}
                headers:
                  - X-User-Name: ${userName}
```

### Example 2: Complete Test with All Features

```yaml
name: comprehensive_test

variables:
- constant:
    name: pPacing
    value: 3000

sla_profiles:
- name: sla
  thresholds:
    - error-rate warn >= 0.1% fail >= 0.2% per test
    - avg-resp-time warn >= 2s fail >= 5s per test

populations:
- name: pop1
  user_paths:
    - name: full_userpath
      distribution: 100%

scenarios:
- name: full_scenario
  sla_profile: sla
  populations:
    - name: pop1
      rampup_load:
        min_users: 1
        max_users: 50
        increment_users: 10
        increment_every: 30s
        duration: 10m

user_paths:
- name: full_userpath
  actions:
    steps:
      # Start pacing
      - request:
          url: http://neoloadutils:8080/setpacing
          extractors:
            - name: pUUID
              jsonpath: $.uuid
      
      # Generate UUID
      - request:
          url: http://neoloadutils:8080/getuuid
          extractors:
            - name: sessionId
              jsonpath: $.uuid
      
      # Generate date
      - transaction:
          name: generate_date
          steps:
            - request:
                url: http://neoloadutils:8080/getparameter
                method: POST
                extractors:
                  - name: testDate
                    jsonpath: $.date
                body: |
                  {
                    "dateformatter_weekdays": "yyyy-MM-dd",
                    "daysToAddOrSubtract": "1"
                  }
      
      # Get database data
      - transaction:
          name: get_credentials
          steps:
            - request:
                url: http://neoloadutils:8080/executeSQL
                method: POST
                extractors:
                  - name: username
                    jsonpath: $.values[0][0]
                  - name: password
                    jsonpath: $.values[0][1]
                body: |
                  {
                    "query": "SELECT username, password FROM test_users",
                    "jdbc": "jdbc:postgresql://db:5432/testdb",
                    "user": "admin",
                    "password": "admin123",
                    "numRows": 1,
                    "randomize": true
                  }
      
      # Main test transactions
      - transaction:
          name: login
          steps:
            - request:
                url: https://api.example.com/login
                method: POST
                body: '{"username": "${username}", "password": "${password}", "sessionId": "${sessionId}"}'
                extractors:
                  - name: authToken
                    jsonpath: $.token
      
      - transaction:
          name: get_data
          steps:
            - request:
                url: https://api.example.com/data?date=${testDate}
                headers:
                  - Authorization: Bearer ${authToken}
                  - X-Session-ID: ${sessionId}
      
      # End pacing
      - request:
          url: http://neoloadutils:8080/getpacing?guid=${pUUID}&totalPacingTimeMillis=${pPacing}
```

---

## 🏥 Health Check

Verify the service is running:

```bash
curl http://localhost:8080/healthcheck
```

**Response:**
```json
{
  "status": "ok",
  "service": "API Health Check"
}
```

---

## 📖 Additional Resources

- 📚 [Interactive API Documentation (Swagger UI)](http://localhost:8080/swagger-ui/index.html)
- 📄 [OpenAPI JSON Specification](http://localhost:8080/v3/api-docs)
- 🐙 [GitHub Repository](https://github.com/ostbergjohan/neoloadutils)
- 📖 [NeoLoad Documentation](https://www.tricentis.com/products/performance-testing-neoload)
- 🔧 [NeoLoad CLI](https://github.com/Neotys-Labs/neoload-cli)
- 📚 [Java DateTimeFormatter](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html)

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

---

## 📄 License

This project is licensed under the Apache License 2.0.

---

## 🔖 Version Information

- **Version**: 1.0
- **Java Version**: 21
- **Spring Boot**: 3.3.4
- **OpenAPI**: 3.0

---

Made with ❤️ for performance engineers
>>>>>>> cebad8db68d09aeb5adf440e6badb341a47ee14f
