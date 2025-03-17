package com.neoloadutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.json.JSONArray;
import org.json.JSONObject;
import com.neoloadutils.NeoLoadRequest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

@SpringBootApplication
@RestController

public class NeoLoadUtilsApplication {

    public static void main(String[] args) {
        SpringApplication.run(NeoLoadUtilsApplication.class, args);
    }
    ColorLogger colorLogger = new ColorLogger();
    HashMap<UUID, Instant> uuidMap = new HashMap<>();
    private List<com.neoloadutils.UrlEntry> urlEntries = new CopyOnWriteArrayList<>();

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {

    }
    // POST mapping to add a new URL entry.
    @GetMapping("/addurl")
    public String addUrl(@RequestParam String url,
                         @RequestParam String explanation) {
        LocalDateTime now = LocalDateTime.now();
        com.neoloadutils.UrlEntry entry = new com.neoloadutils.UrlEntry(url, explanation, now);
        urlEntries.add(entry);
        System.out.println("Added URL: " + url + " at " + now);
        return "URL added successfully!";
    }

    // GET mapping to display all URL entries in a nicely styled HTML page.
    @GetMapping("/geturl")
    public String listUrls() {
        System.out.println("Handling GET / request...");
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang='en'>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>URL List</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }");
        html.append("h1 { text-align: center; color: #333; }");
        html.append("table { width: 80%; margin: 20px auto; border-collapse: collapse; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }");
        html.append("th, td { padding: 12px 15px; border: 1px solid #ddd; text-align: left; }");
        html.append("th { background-color: #4CAF50; color: white; }");
        html.append("tr:nth-child(even) { background-color: #f2f2f2; }");
        html.append("a { color: #2196F3; text-decoration: none; }");
        html.append("a:hover { text-decoration: underline; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<h1>URL List</h1>");
        html.append("<table>");
        html.append("<tr><th>Explanation</th><th>URL</th><th>Timestamp</th></tr>");

        // Create a sorted copy of entries based on the explanation field
        List<com.neoloadutils.UrlEntry> sortedEntries = new ArrayList<>(urlEntries);
        sortedEntries.sort(Comparator.comparing(com.neoloadutils.UrlEntry::getExplanation));

        for (com.neoloadutils.UrlEntry entry : sortedEntries) {
            html.append("<tr>")
                    .append("<td>").append(entry.getExplanation()).append("</td>")
                    .append("<td>")
                    .append("<a href='").append(entry.getUrl()).append("'>")
                    .append(entry.getUrl())
                    .append("</a>")
                    .append("</td>")
                    .append("<td>").append(entry.getTimestamp()).append("</td>")
                    .append("</tr>");
        }
        html.append("</table>");
        html.append("</body></html>");
        return html.toString();
    }
    @CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.POST})
    @PostMapping("/convertHTTP")
    public Map<String, Object> convertHttpFormat(@RequestBody String httpRequest) throws MalformedURLException {
        Map<String, Object> transaction = new HashMap<>();
        Map<String, String> headers = new HashMap<>();

        String[] lines = httpRequest.split("\n");
        String firstLine = lines[0];
        String[] firstLineParts = firstLine.split(" ");

        if (firstLineParts.length < 2) {
            throw new IllegalArgumentException("Invalid HTTP request format");
        }

        String method = firstLineParts[0].trim();
        String rawUrl = firstLineParts[1].trim();
        String hostHeader = extractHeaderValue(lines, "Host");
        String url;

        try {
            // Try parsing the raw URL.
            URL parsedUrl = new URL(rawUrl);
            // If parsing is successful, remove the host by extracting only the file (path + query).
            url = "https://" + hostHeader + parsedUrl.getFile();
        } catch (MalformedURLException e) {
            // If rawUrl is not a full URL, assume it is just a path.
            url = "https://" + hostHeader + rawUrl;
        }

        boolean isBody = false;
        StringBuilder bodyBuilder = new StringBuilder();

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) {
                isBody = true;
                continue;
            }

            if (isBody) {
                bodyBuilder.append(line).append("\n");
            } else {
                String[] headerParts = line.split(": ", 2);
                if (headerParts.length == 2) {
                    headers.put(headerParts[0], headerParts[1]);
                }
            }
        }

        transaction.put("name", "transaction name");
        transaction.put("url", url);
        transaction.put("method", method);
        transaction.put("body", bodyBuilder.toString().trim());
        transaction.put("headers", headers);

        return transaction;
    }


    private String extractHeaderValue(String[] lines, String headerName) {
        for (String line : lines) {
            if (line.toLowerCase().startsWith(headerName.toLowerCase() + ":")) {
                return line.split(": ", 2)[1].trim();
            }
        }
        return "";
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.POST})
    @PostMapping("/NeoLoadYamlGenerator")
    public ResponseEntity<String> NeoLoadYamlGenerator(@RequestBody NeoLoadRequest payload, HttpServletRequest request) {

        if (payload == null) {
            colorLogger.logInfo("Received payload is null!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Parse the JSON payload
        JSONObject jsonObject = new JSONObject(payload);
        colorLogger.logInfo("\n" + jsonObject.toString() + "\n");

        // Extracting top-level values
        String name = jsonObject.getString("name");
        String scenarioInput = jsonObject.getString("scenario");
        String pacing = jsonObject.getString("pacing");
        int users = jsonObject.getInt("users");
        int duration = jsonObject.getInt("duration");
        String userpathname = jsonObject.getString("userpathname");

        // Extracting transactions array
        JSONArray transactions = jsonObject.getJSONArray("transactions");
        String serverUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        Map<String, Object> yamlData = new LinkedHashMap<>();
        yamlData.put("name", name);

        // Variables
        /*
        List<Map<String, Object>> variables = new ArrayList<>();
        Map<String, Object> constantVar = new LinkedHashMap<>();
        constantVar.put("constant", Map.of("name", "pPacing", "value", pacing));
        variables.add(constantVar);

        yamlData.put("variables", variables);
        */

// Variables
        List<Map<String, Object>> variables = new ArrayList<>();
        Map<String, Object> constantVar = new LinkedHashMap<>();
        constantVar.put("constant", Map.of("name", "pPacing", "value", pacing));
        variables.add(constantVar);

// Check if the payload has a "files" array and add each file as a variable entry
        if (jsonObject.has("files")) {
            JSONArray filesArray = jsonObject.getJSONArray("files");
            for (int i = 0; i < filesArray.length(); i++) {
                JSONObject fileObj = filesArray.getJSONObject(i);
                String fileName = fileObj.getString("name");

                // Convert column_names JSONArray to List<String>
                JSONArray colNamesJson = fileObj.getJSONArray("column_names");
                List<String> columnNames = new ArrayList<>();
                for (int j = 0; j < colNamesJson.length(); j++) {
                    columnNames.add(colNamesJson.getString(j));
                }
                String filePath = fileObj.getString("path");

                // Build the file mapping. Here we're setting default values for other file properties.
                Map<String, Object> fileVar = new LinkedHashMap<>();
                fileVar.put("file", Map.of(
                        "name", fileName,
                       // "column_names", columnNames,
                        "is_first_line_column_names", true,         // default value
                        "start_from_line", 1,                          // default value
                        "delimiter", ",",                              // default value
                        "path", filePath,
                        "change_policy", "each_iteration",             // default value
                        "scope", "global",                             // default value
                        "order", "any",                                // default value
                        "out_of_value", "cycle"                        // default value
                ));
                variables.add(fileVar);
            }
        }

        yamlData.put("variables", variables);

        // SLA Profiles
        List<Map<String, Object>> slaProfiles = new ArrayList<>();
        Map<String, Object> sla = new LinkedHashMap<>();
        sla.put("name", "sla");
        sla.put("thresholds", List.of("error-rate warn >= 0.1% fail >= 0.2% per test"));
        slaProfiles.add(sla);
        yamlData.put("sla_profiles", slaProfiles);

        // Populations
        Map<String, Object> population = new LinkedHashMap<>();
        population.put("name", "pop_" + userpathname);
        population.put("user_paths", List.of(Map.of("name", userpathname, "distribution", "100%")));
        yamlData.put("populations", List.of(population));

        // Scenarios
        Map<String, Object> scenario = new LinkedHashMap<>();
        scenario.put("name", scenarioInput);
        scenario.put("description", "update");
        scenario.put("sla_profile", "sla");
        scenario.put("populations", List.of(Map.of("name", "pop_" + userpathname, "constant_load", Map.of("users", users, "duration", duration + "m"))));
        //yamlData.put("scenarios", List.of(scenario));

        Map<String, Object> debugScenario = new LinkedHashMap<>();
        debugScenario.put("name", scenarioInput + "_debug");
        debugScenario.put("description", "Debug scenario with 1 iteration");
        debugScenario.put("sla_profile", "sla");
        debugScenario.put("populations", List.of(Map.of("name", "pop_" + userpathname, "constant_load", Map.of("users", 1, "duration", "1 iteration"))));

        yamlData.put("scenarios", List.of(scenario, debugScenario));
        // User Paths
        Map<String, Object> userPath = new LinkedHashMap<>();
        userPath.put("name", userpathname);
        List<Map<String, Object>> actions = new ArrayList<>();

        // Start pacing request
        if (!"0".equals(pacing)) {
            actions.add(Map.of("request", Map.of(
                    "url", serverUrl + "/setpacing",
                    "extractors", List.of(Map.of("name", "pUUID", "jsonpath", "$.uuid"))
            )));
        }

        JSONArray transactionsArray = jsonObject.getJSONArray("transactions");

        for (int i = 0; i < transactionsArray.length(); i++) {
            JSONObject jsontransaction = transactionsArray.getJSONObject(i);
            String transactioname = jsontransaction.getString("url"); // Extract name

            JSONObject headersJson = jsontransaction.getJSONObject("headers");
            List<Map<String, String>> headersList = new ArrayList<>();

            for (String key : headersJson.keySet()) {
                headersList.add(Map.of(key, headersJson.getString(key)));
            }

            // Extract extractors (corrected for JSONArray)
            List<Map<String, String>> extractorsList = new ArrayList<>();
            if (jsontransaction.has("extractors") && jsontransaction.get("extractors") instanceof JSONArray) {
                JSONArray extractorsJson = jsontransaction.getJSONArray("extractors");
                for (int j = 0; j < extractorsJson.length(); j++) {
                    JSONObject extractor = extractorsJson.getJSONObject(j);
                    String nameExtractor = extractor.getString("name");
                    String extractortype = "";
                    if (extractor.has("jsonpath")) {
                        extractortype = extractor.getString("jsonpath");
                        LinkedHashMap<String, String> extractorMap = new LinkedHashMap<>();
                        extractorMap.put("name", nameExtractor);        // "name" first
                        extractorMap.put("jsonpath", extractortype);
                        extractorMap.put("extract_once", "true");
                        extractorsList.add(extractorMap);
                    } else if (extractor.has("xpath")) {
                        extractortype = extractor.getString("xpath");
                        LinkedHashMap<String, String> extractorMap = new LinkedHashMap<>();
                        extractorMap.put("name", nameExtractor);        // "name" first
                        extractorMap.put("xpath", extractortype);
                        extractorMap.put("extract_once", "true");
                        extractorsList.add(extractorMap);
                    } else if (extractor.has("regexp")) {
                        extractortype = extractor.getString("regexp");
                        LinkedHashMap<String, String> extractorMap = new LinkedHashMap<>();
                        extractorMap.put("name", nameExtractor);        // "name" first
                        extractorMap.put("regexp", extractortype);
                        extractorMap.put("extract_once", "true");
                        extractorsList.add(extractorMap);
                    }

                }
            }

            String body;
            String method;

            if (jsontransaction.has("body")) {
                Object bodyValue = jsontransaction.get("body");

                // Check if it's a JSONObject
                if (bodyValue instanceof JSONObject) {
                    body = (String) bodyValue; // Assign JSONObject
                }
                // Check if it's a JSONArray
                else if (bodyValue instanceof JSONArray) {
                    body = (String) bodyValue; // Assign JSONArray
                }
                // If it's a String
                else if (bodyValue instanceof String) {
                    body = (String) bodyValue; // Assign String
                }
                else {
                    body = "default_body"; // or handle appropriately if body is an unknown type
                 }
            } else {
                body = "default_body"; // Default value if "body" is not present
            }

            if (jsontransaction.has("method") && !jsontransaction.getString("method").isEmpty()) {
                method = jsontransaction.getString("method");
            } else {
                method = "get"; // or handle appropriately
            }

            String assertion = jsontransaction.optString("assertion", ".*");
            if (assertion.length() <= 1) {
                assertion = ".*"; // Set to space if it's empty or length is 1 or less
            }

            Map<String, Object> transaction = new LinkedHashMap<>();

            if (method.equalsIgnoreCase("POST") ||
                    method.equalsIgnoreCase("PUT") ||
                    method.equalsIgnoreCase("PATCH")) {

                if (!extractorsList.isEmpty()) {
                    transaction.put("name", jsontransaction.getString("name"));
                    transaction.put("description", "generated");
                    transaction.put("steps", List.of(Map.of("request", Map.of(
                            "url", jsontransaction.getString("url"),
                            "method", method,
                            "body", body,
                            "headers", headersList, // Use the dynamically extracted headers
                            "extractors", extractorsList,
                            "assertions", List.of(Map.of("contains", assertion, "regexp", true))
                    ))));
                }else{
                    transaction.put("name", jsontransaction.getString("name"));
                    transaction.put("description", "generated");
                    transaction.put("steps", List.of(Map.of("request", Map.of(
                            "url", jsontransaction.getString("url"),
                            "method", method,
                            "body", body,
                            "headers", headersList, // Use the dynamically extracted headers
                            "assertions", List.of(Map.of("contains", assertion, "regexp", true))
                    ))));
                }
            } else {
                if (!extractorsList.isEmpty()) {
                    transaction.put("name", jsontransaction.getString("name"));
                    transaction.put("description", "generated");
                    transaction.put("steps", List.of(Map.of("request", Map.of(
                            "url", jsontransaction.getString("url"),
                            "method", method,
                            "headers", headersList, // Use the dynamically extracted headers
                            "extractors", extractorsList,
                            "assertions", List.of(Map.of("contains", assertion, "regexp", true))
                    ))));
                }else{
                    transaction.put("name", jsontransaction.getString("name"));
                    transaction.put("description", "generated");
                    transaction.put("steps", List.of(Map.of("request", Map.of(
                            "url", jsontransaction.getString("url"),
                            "method", method,
                            "headers", headersList, // Use the dynamically extracted headers
                            "assertions", List.of(Map.of("contains", assertion, "regexp", true))
                    ))));
                }
            }

            // Transaction
               actions.add(Map.of("transaction", transaction));
        }
        // End pacing request
        if (!"0".equals(pacing)) {
            actions.add(Map.of("request", Map.of(
                    "url", serverUrl + "/getpacing?guid=${pUUID}&totalPacingTimeMillis=${pPacing}"
            )));
        }
        userPath.put("actions", Map.of("steps", actions));
        yamlData.put("user_paths", List.of(userPath));

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        String yamlOutput = null;
        try {
            yamlOutput = mapper.writeValueAsString(yamlData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String header = "#NeoLoadYamlGenerator - " + currentDateTime + "\n";
        colorLogger.logInfo("\n" + header);

        // Prepend the header to the yamlOutput
        yamlOutput = header + yamlOutput;

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("application/x-yaml"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"default.yaml\"")
                .body(yamlOutput = yamlOutput.replaceAll("(?m)^---$", "").trim());
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