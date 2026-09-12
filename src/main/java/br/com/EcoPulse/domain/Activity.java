package br.com.EcoPulse.domain;

import java.time.Instant;

public class Activity {
    private Long id;
    private Long userId;
    private String type;
    private Instant timestamp;
    private String metadata;

    public Activity() {
    }

    public Activity(Long id, Long userId, String type, Instant timestamp, String metadata) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.timestamp = timestamp;
        this.metadata = metadata;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
}
