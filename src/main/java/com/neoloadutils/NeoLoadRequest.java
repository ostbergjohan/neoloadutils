package com.neoloadutils;

import java.util.List;
import java.util.Map;

public class NeoLoadRequest {
    private String name;
    private String scenario;
    private String pacing;
    private int users;
    private int duration;
    private String userpathname;
    private List<Transaction> transactions;
    // New field to accept an array of file mappings
    private List<FileData> files;

    // ✅ No-argument constructor
    public NeoLoadRequest() {}

    // ✅ Getters and Setters (MUST be present for Jackson)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getScenario() { return scenario; }
    public void setScenario(String scenario) { this.scenario = scenario; }

    public String getPacing() { return pacing; }
    public void setPacing(String pacing) { this.pacing = pacing; }

    public int getUsers() { return users; }
    public void setUsers(int users) { this.users = users; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getUserpathname() { return userpathname; }
    public void setUserpathname(String userpathname) { this.userpathname = userpathname; }

    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }

    // Getter and Setter for the new "files" field
    public List<FileData> getFiles() { return files; }
    public void setFiles(List<FileData> files) { this.files = files; }

    // Existing inner class Transaction
    public static class Transaction {
        private String name;
        private String url;
        private String assertion;
        private Object body;  // Changed to Object to handle multiple types (String, JSON, etc.)
        private String method;
        private Map<String, String> headers;
        private List<Extractor> extractors;

        public Transaction() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }

        public Object getBody() { return body; }
        public void setBody(Object body) { this.body = body; }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }

        public String getAssertion() { return assertion; }
        public void setAssertion(String assertion) { this.assertion = assertion; }

        public Map<String, String> getHeaders() { return headers; }
        public void setHeaders(Map<String, String> headers) { this.headers = headers; }

        public List<Extractor> getExtractors() { return extractors; }
        public void setExtractors(List<Extractor> extractors) { this.extractors = extractors; }

        // Updated inner class Extractor to accept jsonpath, xpath, and regexp
        public static class Extractor {
            private String name;
            private String jsonpath;
            private String xpath;
            private String regexp;

            public Extractor() {}

            public Extractor(String name, String jsonpath, String xpath, String regexp) {
                this.name = name;
                this.jsonpath = jsonpath;
                this.xpath = xpath;
                this.regexp = regexp;
            }

            public String getName() { return name; }
            public void setName(String name) { this.name = name; }

            public String getJsonpath() { return jsonpath; }
            public void setJsonpath(String jsonpath) { this.jsonpath = jsonpath; }

            public String getXpath() { return xpath; }
            public void setXpath(String xpath) { this.xpath = xpath; }

            public String getRegexp() { return regexp; }
            public void setRegexp(String regexp) { this.regexp = regexp; }
        }
    }

    // New inner class to represent each file mapping
    public static class FileData {
        private String name;
        private List<String> column_names;
        private String path;

        public FileData() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public List<String> getColumn_names() { return column_names; }
        public void setColumn_names(List<String> column_names) { this.column_names = column_names; }

        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
    }
}
