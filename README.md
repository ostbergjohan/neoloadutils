# NeoLoad Utils: A helper application designed to provide additional functionality during performance testing with NeoLoad Test-as-Code (also compatible with other tools).

## API Calls for Pacing

To create pacing in a NeoLoad when running test-as-code, the following API calls are used:

**At the start of an iteration**:
https://your-endpoint/setpacing

 **At the end of an iteration** (with pacing information):
https://your-endpoint/getpacing?guid=2602c87e-b736-4d8c-b7fe-ba62916875fd&totalPacingTimeMillis=1000

NeoLoad utilities randomize the pacing value between 0.75 and 1.25 for better distribution.

Be aware of platform limitations. For example, in OpenShift route has a default timeout of 30 seconds (which can be modified). This means pacing durations should not exceed this limit and typically don't need adjustment for use in continuous performance testing.
### Trend View Consideration in NeoLoad Web
In the trend view, requests used to create pacing should be ignored, as they are additional calls specifically for pacing and may skew the results.

## Example NeoLoad Project

```yaml
name: pacingexample

# Pacing variable for the script in milliseconds
variables:
- constant:
    name: pPacing
    value: 1000

sla_profiles:
- name: sla
  thresholds:
    # Warning at 0.1% incorrect transactions and fail at 0.2%
    - error-rate warn >= 0.1% fail >= 0.2% per test

populations: 
- name: pop1
  user_paths:
    - name: example_userpath
      distribution: 100%

# Load for the Population
scenarios:
- name: exempelPacing
  description: Example scenario for pacing
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
          url: https://your-endpoint/setpacing
          extractors:
            - name: pUUID
              jsonpath: $.uuid

      # API call to application
      - transaction:
          name: transaction 1
          description: Retrieve case details for employer with customer number
          steps:
            - request:
                url: https://enpoint-to-test/123

      # End pacing measurement and wait for remaining time
      - request:
          url: https://your-endpoint/getpacing?guid=${pUUID}&totalPacingTimeMillis=${pPacing}

```

## Introduction to Date/Time Parameters

In performance testing, it may be necessary to generate dynamic date or timestamp parameters for your test scripts. This can be achieved by using a **DateTimeFormatter** to format the date and add or subtract days as needed. 

### Date Formatter Parameter
- **`dateformatter`**: Specifies the format for the date using `java.time.format.DateTimeFormatter`. 
- **`daysToAddOrSubtract`**: Accepts values like `+10`, `-1`, or `0` to adjust the current date.

### API Call Example:

#### Request:

POST https://your-endpoint/getparameter { "dateformatter": "yyyy-MM-dd HH:mm
z", "daysToAddOrSubtract": "0" }


#### Response:
```json
{
  "date": "2024-10-15 13:58:10 CEST"
}
```

Skip Weekends:
To skip weekends (i.e., ensure the generated date is a weekday), you can modify the request:

#### Request:
```json
POST https://your-endpoint/getparameter
{
  "dateformatter_weekdays": "yyyy-MM-dd HH:mm:ss z",
  "daysToAddOrSubtract": "0"
}
```
#### Response:
```json
{
  "date": "2024-10-15 13:58:10 CEST"
}
```

DateTimeFormatter Examples
Here are the most commonly used symbols for formatting dates with DateTimeFormatter:

yyyy: Year (e.g., 2024)
MM: Month (01 to 12)
dd: Day of the month (01 to 31)
HH: Hour (24-hour clock)
mm: Minute
ss: Second
EEEE: Day of the week (e.g., Monday)
MMMM: Full month name (e.g., October)
For more details on DateTimeFormatter, see the Java Platform SE 8 documentation.


NeoLoad Project Example
Here’s an example of how to incorporate the date/time parameter in a NeoLoad project:
```yaml
name: datumexempel # Project name

sla_profiles:
  - name: sla
    thresholds:
      # Warning at 0.1% incorrect transactions and fail at 0.2%
      - error-rate warn >= 0.1% fail >= 0.2% per test

populations: 
  - name: pop1
    user_paths:
      - name: example_userpath
        distribution: 100%

# Load for the Population
scenarios:
  - name: exempelPacing
    description: Example scenario for pacing
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
        # Transaction to generate the timestamp/date
        - transaction:
            name: XX uuid
            description: Generates timestamp/date
            steps:
              - request:
                  url: https://your-endpoint/getparameter
                  method: POST
                  extractors:
                    - name: pDate
                      jsonpath: $.date
                  body: |
                    {
                      "dateformatter": "yyyy-MM-dd HH:mm:ss z",
                      "daysToAddOrSubtract": "0"
                    }

        # API call using the generated date as a parameter
        - transaction:
            name: transaction get with date
            description: Employer retrieves cases with date
            steps:
              - request:
                  url: https://your-endpoint/${pDate}

```

#### Explanation:
getparameter API is called to generate a date with the specified format and adjustment
(in this case, no days added or subtracted).
The date from the response is extracted using the JSONPath $.date and stored in the pDate variable.
The second transaction uses the generated pDate in the URL to make an API call with the dynamic date parameter.


# Usage of REST-API: /executeSQL

This API method is used to execute an SQL query against a specified database and returns the result in JSON format.
Might potentially be done in the init section, depending on the circumstances or in the end section to verify data.

**HTTP Method**: POST  
**URL**: https://your-endpoint/executeSQL

## Expected Request Body

The request body must be a JSON structure with the following required keys:

- **query**: *String* — The SQL query to execute.
- **jdbc**: *String* — The JDBC URL to the database.
- **user**: *String* — The database username.
- **password**: *String* — The database password.
- **numRows**: *Integer* — The number of rows to return (≥ 1).
- **randomize**: *Boolean* — Whether the rows should be randomized before being returned.

### Example Request Body:

```json
{
  "query": "SELECT * FROM employees",
  "jdbc": "jdbc:mysql://localhost:3306/mydatabase",
  "user": "dbuser",
  "password": "dbpassword",
  "numRows": 10,
  "randomize": true
}
```
Response Format
The method returns the result as a JSON structure containing two keys:

columns: A list of column names in the result.
values: A list of rows, where each row is a list of values.
```json
{
  "columns": ["id", "name", "department"],
  "values": [
    [1, "Alice", "HR"],
    [2, "Bob", "IT"],
    [3, "Charlie", "Finance"],
    [4, "Diana", "Marketing"],
    [5, "Eve", "Operations"],
    [6, "Frank", "Sales"],
    [7, "Grace", "Engineering"],
    [8, "Hank", "Logistics"],
    [9, "Ivy", "Legal"],
    [10, "Jack", "Product"]
  ]
}
```

Supported JDBC Drivers:
MySQL: mysql-connector-java
PostgreSQL: postgresql
SQL Server: mssql-jdbc
Oracle: ojdbc
SQLite: sqlite-jdbc

# Generate a UUID for Use in Test Scripts

**Endpoint**: `https://your-endpoint/getuuid`

**Method**: GET

This API generates a UUID that can be used in your test script.

### Example Response:
```json
{
  "uuid": "a003cac8-49a3-498d-9559-ca27a307a56b"
}
```

## **Running the Application**

### **Docker Hub Image**  
The application is available on Docker Hub:  
[**Docker Hub - NeoLoad Utils**](https://hub.docker.com/repository/docker/johanostberg/neoloadutils)

### **Running with Docker**  
To run the application, use the following command:  
```bash
docker run -d \
  -p 8080:8080 \
  johanostberg/neoloadutils:latest
```
