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
    public String getDisplayName() { return name == null || name.isBlank() ? "Avatar sem nome" : name.trim(); }
    public boolean isEvolved() { return level != null && level >= 5; }
    public long getExperienceToNextLevel() { return Math.max(0L, (level == null ? 1L : level) * 1000L - (experiencePoints == null ? 0L : experiencePoints)); }
    public void registerInteraction() { this.lastInteraction = Instant.now(); this.updatedAt = this.lastInteraction; }
    public void addExperience(long points) {
        if (points <= 0) throw new IllegalArgumentException("A experiência deve ser positiva");
        if (experiencePoints == null) experiencePoints = 0L;
        if (level == null || level < 1) level = 1;
        experiencePoints += points;
        level = (int) (experiencePoints / 1000L) + 1;
        updatedAt = Instant.now();
    }
}
