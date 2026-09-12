package br.com.EcoPulse.domain;

import java.time.Instant;

public class Avatar {
    private Long id;
    private Long userId;
    private String name;
    private String visualConfig; // JSON string
    private String personalityType;
    private Integer level;
    private Long experiencePoints;
    private Instant lastInteraction;
    private Instant createdAt;
    private Instant updatedAt;

    public Avatar() {
    }

    public Avatar(Long id, Long userId, String name, String visualConfig, String personalityType, Integer level, Long experiencePoints, Instant lastInteraction, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.visualConfig = visualConfig;
        this.personalityType = personalityType;
        this.level = level;
        this.experiencePoints = experiencePoints;
        this.lastInteraction = lastInteraction;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getVisualConfig() { return visualConfig; }
    public void setVisualConfig(String visualConfig) { this.visualConfig = visualConfig; }

    public String getPersonalityType() { return personalityType; }
    public void setPersonalityType(String personalityType) { this.personalityType = personalityType; }

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }

    public Long getExperiencePoints() { return experiencePoints; }
    public void setExperiencePoints(Long experiencePoints) { this.experiencePoints = experiencePoints; }

    public Instant getLastInteraction() { return lastInteraction; }
    public void setLastInteraction(Instant lastInteraction) { this.lastInteraction = lastInteraction; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
