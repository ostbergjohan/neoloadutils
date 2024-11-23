package com.neoloadutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SpringBootApplication
@RestController

public class NeoLoadUtilsApplication {

    public static void main(String[] args) {
      SpringApplication.run(NeoLoadUtilsApplication.class, args);
    }
    ColorLogger colorLogger = new ColorLogger();
    HashMap<UUID, Instant> uuidMap = new HashMap<>();

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {

    }

    @PostMapping("/executeSQL")
        public ResponseEntity<String> executeSQL(@RequestBody String QueryRequest) {

        // Parse the JSON string
        JSONObject jsonObject = new JSONObject(QueryRequest);

        // Validate required keys
        String[] requiredKeys = {"query", "jdbc", "user", "password", "numRows", "randomize"};
        for (String key : requiredKeys) {
            if (!jsonObject.has(key)) {
                return logAndRespondError("Error executing SQL query, Missing required key: " + key);
            }
        }
        // Validate the input values
        if (jsonObject.getInt("numRows") < 1) {
            return logAndRespondError("Invalid number of rows. Must be 1 or greater.");
        }
        JSONObject SQLresult;
        try {
            SQLresult = executeSQLQuery(jsonObject.getString("query"), jsonObject.getString("jdbc"), jsonObject.getString("user")
                    , jsonObject.getString("password"), jsonObject.getInt("numRows"), jsonObject.getBoolean("randomize"));
        } catch (SQLException e) {
            return logAndRespondError("Error executing SQL query: " + e);
        }

        // Return the result as a JSON response
        return ResponseEntity.ok(SQLresult.toString(4));
    }

    @GetMapping(value = "healthcheck")
    public ResponseEntity<String> healthcheck() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8");
        headers.add(HttpHeaders.CONTENT_ENCODING, "UTF-8");
        return ResponseEntity.ok()
                .headers(headers)
                .body("{\"status\":\"ok\",\"service\":\"API Health Check\"}");
    }

    @PostMapping(value = "getparameter")
    public ResponseEntity<String> getparameter(@RequestBody String jsonstring){
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8");
        headers.add(HttpHeaders.CONTENT_ENCODING, "UTF-8");
        var responseBodyMapper = new ObjectMapper(); //com.fasterxml.jackson.databind.ObjectMapper;
        Map<String, String> map = new HashMap<>();
        try {

            map = responseBodyMapper.readValue(jsonstring, Map.class);
        } catch (
                IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Invalid JSON", HttpStatus.BAD_REQUEST);
        }
        if (map.containsKey("dateformatter") && map.containsKey("daysToAddOrSubtract")) {
            // Extract parameters
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(map.get("dateformatter"));
            int daysToAddOrSubtract = Integer.parseInt(map.get("daysToAddOrSubtract"));
            // Start from today's date
            LocalDate date = LocalDate.now();
            date = date.plusDays(daysToAddOrSubtract);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body("{\"date\":\"" + date.format(formatter) + "\"}");
        }

        if (map.containsKey("dateformatter_weekdays")&& map.containsKey("daysToAddOrSubtract")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(map.get("dateformatter_weekdays"));
            int daysToAddOrSubtract = Integer.parseInt(map.get("daysToAddOrSubtract"));
            LocalDate date = LocalDate.now();
            int addedDays = 0;
            if (daysToAddOrSubtract > 0) {
                while (addedDays < daysToAddOrSubtract) {
                    date = date.plusDays(1);
                    if (!isWeekend(date)) {
                        addedDays++;
                    }
                }
            } else {
                while (addedDays > daysToAddOrSubtract) {
                    date = date.minusDays(1);
                    if (!isWeekend(date)) {
                        addedDays--;
                    }
                }
            }
            return ResponseEntity.ok()
                    .headers(headers)
                    .body("{\"date\":\"" + date.format(formatter) + "\"}");
        }
        colorLogger.logError("Invalid JSON:" + jsonstring);
        return new ResponseEntity<>("Invalid JSON:" + jsonstring, HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "setpacing")
    public ResponseEntity<String> setpacing() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8");
        headers.add(HttpHeaders.CONTENT_ENCODING, "UTF-8");
        return ResponseEntity.ok()
                .headers(headers)
                .body("{\"uuid\":\"" + saveUUIDWithTimestamp() + "\"}");
    }
    @GetMapping(value = "getuuid")
    public ResponseEntity<String> getuuid() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8");
        headers.add(HttpHeaders.CONTENT_ENCODING, "UTF-8");
        return ResponseEntity.ok()
                .headers(headers)
                .body("{\"uuid\":\"" + UUID.randomUUID() + "\"}");
    }

    @GetMapping(value = "getpacing")
    public ResponseEntity<String> getpacing(@RequestParam UUID guid, @RequestParam String totalPacingTimeMillis) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8");
        headers.add(HttpHeaders.CONTENT_ENCODING, "UTF-8");
        System.out.println("getpacing");

       if (totalPacingTimeMillis.isEmpty()) {
           colorLogger.logError("totalPacingTimeMillis is empty");
            return ResponseEntity.internalServerError()
                    .headers(headers)
                    .body("{\"error\":\"totalPacingTimeMillis is empty\"}");
        }
        if (guid.toString().isEmpty()) {
            colorLogger.logError("guid is empty");
            return ResponseEntity.internalServerError()
                    .headers(headers)
                    .body("{\"error\":\"guid is empty\"}");
        }
        if (totalPacingTimeMillis.isEmpty()) {
            colorLogger.logError("totalPacingTimeMillis is empty");
            return ResponseEntity.internalServerError()
                    .headers(headers)
                    .body("{\"error\":\"String is empty\"}");
        }
        for (char c : totalPacingTimeMillis.toCharArray()) {
            if (!Character.isDigit(c)) {
                colorLogger.logError("Found a non-digit character");
                return ResponseEntity.internalServerError()
                        .headers(headers)
                        .body("{\"error\":\"Found a non-digit character\"}");
            }
        }
        long remainingPacingTimeMillis;
        try {
        Duration timeAlreadySpentMillis = Duration.between( getTimestamp(guid),Instant.now());
        remainingPacingTimeMillis = Long.valueOf(totalPacingTimeMillis) - timeAlreadySpentMillis.toMillis();
        if (remainingPacingTimeMillis < 0) { remainingPacingTimeMillis=0; };
        Random random = new Random();
        long randomValue = (long) (0.75 + (1.25 - 0.75) * random.nextDouble());
        Thread.sleep(remainingPacingTimeMillis*randomValue);
        removeUUID(guid);
        } catch (NumberFormatException e) {
            colorLogger.logError("uuid not found");
            return ResponseEntity.internalServerError()
                    .headers(headers)
                    .body("{\"error\":\"uuid not found\"}");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok()
                .headers(headers)
                .body("{\"uuid\":\"" + guid + "\",\"duration\":"+remainingPacingTimeMillis +"}");
    }
    public Instant getTimestamp(UUID uuid) {
          return uuidMap.get(uuid);
    }
    public void displayAllEntries() {
        uuidMap.forEach((key, value) ->
                System.out.println("UUID: " + key + " | Timestamp: " + value));
    }
    public UUID saveUUIDWithTimestamp() {
        UUID uuid = UUID.randomUUID();
        uuidMap.put(uuid, Instant.now());
        return uuid;
    }
    public boolean removeUUID(UUID uuid) {
        if (uuidMap.containsKey(uuid)) {
            uuidMap.remove(uuid);
            return true;
        } else {
            colorLogger.logError("UUID not found: " + uuid);
            return false;
        }
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    public JSONObject executeSQLQuery(String query, String jdbc, String user, String password, int numRows, boolean randomize) throws SQLException {
        // Create a JSON object to hold the final result
        JSONObject result = new JSONObject();
        // Initialize the columns array
        JSONArray columns = new JSONArray();
        // Initialize the values array to hold all the rows
        JSONArray values = new JSONArray();

        // Determine the database type from the JDBC URL
        String dbType = getDatabaseType(jdbc);

        // Establish connection
        try (Connection conn = DriverManager.getConnection(jdbc, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            // Log connection info
            colorLogger.logInfo("\nData source created: \n" + jdbc);
            colorLogger.logInfo("\nRunning query: \n" + query);
            // Get metadata from result set
            ResultSetMetaData rsmd = rs.getMetaData();
            // Collect column names
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                 columns.put(rsmd.getColumnName(i));  // Add column name to the JSON array
            }
            // Collect all rows into a list
            List<JSONArray> allRows = new ArrayList<>();
            while (rs.next()) {
                JSONArray rowValues = new JSONArray();
                for (int j = 1; j <= rsmd.getColumnCount(); j++) {
                    rowValues.put(rs.getString(j));  // Add each row value to the row JSON array
                }
                allRows.add(rowValues);  // Add the row to the list of all rows
            }
            // If randomize is true, shuffle the rows
            if (randomize) {
                Collections.shuffle(allRows);  // Shuffle the rows randomly
            }
            // Add the required number of rows (numRows) to the result
            int rowCount = 0;
            for (JSONArray row : allRows) {
                if (numRows != 0 && rowCount >= numRows) {
                    break;  // Stop when we've collected enough rows
                }
                values.put(row);  // Add the row to the values array
                rowCount++;
            }

        } catch (SQLException e) {
            colorLogger.logError("Error executing SQL query:\n" + e);
            throw e;  // Re-throw the exception after logging
        }
        // Add columns and values to the result JSON object
        result.put("columns", columns);
        result.put("values", values);
        return result;  // Return the result as JSON
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8");
        headers.add(HttpHeaders.CONTENT_ENCODING, "UTF-8");
        return headers;
    }

    private ResponseEntity<String> logAndRespondError(String errorMessage) {
        colorLogger.logError(errorMessage);
        return ResponseEntity.internalServerError()
                .headers(createHeaders())
                .body("{\"error\":\"" + errorMessage + "\"}");
    }

    private ResponseEntity<String> Respond(String Message) {
        return ResponseEntity.ok()
                .headers(createHeaders())
                .body("{\"message\":\"" + Message + "\"}");
    }

    /**
     * Determines the database type from the JDBC URL.
     */
    private String getDatabaseType(String jdbcUrl) {
        if (jdbcUrl.startsWith("jdbc:mysql:")) {
            return "MySQL";
        } else if (jdbcUrl.startsWith("jdbc:postgresql:")) {
            return "PostgreSQL";
        } else if (jdbcUrl.startsWith("jdbc:sqlserver:")) {
            return "SQLServer";
        } else if (jdbcUrl.startsWith("jdbc:oracle:")) {
            return "Oracle";
        } else if (jdbcUrl.startsWith("jdbc:sqlite:")) {
            return "SQLite";
        } else {
            throw new IllegalArgumentException("Unsupported database type in JDBC URL: " + jdbcUrl);
        }
    }

    public class ColorLogger {
        private static final Logger LOGGER = LoggerFactory.getLogger("");
        public void logDebug(String logging) {
            LOGGER.debug("\u001B[92m" + logging + "\u001B[0m");
        }
        public void logInfo(String logging) {
            LOGGER.info("\u001B[93m" + logging + "\u001B[0m");
        }
        public void logError(String logging) {
            LOGGER.error("\u001B[91m" + logging + "\u001B[0m");
        }
    }


}