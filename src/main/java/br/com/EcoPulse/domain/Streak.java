package br.com.EcoPulse.domain;

import java.time.Instant;

public class Streak {
    private Long id;
    private Long userId;
    private String type;
    private Integer currentCount;
    private Integer highestCount;
    private Instant lastActivityAt;
    private Instant createdAt;
    private Instant updatedAt;

    public Streak() {
    }

    public Streak(Long id, Long userId, String type, Integer currentCount, Integer highestCount, Instant lastActivityAt, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.currentCount = currentCount;
        this.highestCount = highestCount;
        this.lastActivityAt = lastActivityAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getCurrentCount() { return currentCount; }
    public void setCurrentCount(Integer currentCount) { this.currentCount = currentCount; }

    public Integer getHighestCount() { return highestCount; }
    public void setHighestCount(Integer highestCount) { this.highestCount = highestCount; }

    public Instant getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(Instant lastActivityAt) { this.lastActivityAt = lastActivityAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
