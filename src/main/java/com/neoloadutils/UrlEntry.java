package com.neoloadutils;

import java.time.LocalDateTime;

public class UrlEntry {
    private String url;
    private String explanation;
    private LocalDateTime timestamp;

    public UrlEntry() {
    }

    public UrlEntry(String url, String explanation, LocalDateTime timestamp) {
        this.url = url;
        this.explanation = explanation;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
