package br.com.EcoPulse.domain;

import java.time.Instant;

public class Mission {
    private Long id;
    private String title;
    private String description;
    private String type;
    private Integer rewardPoints;
    private Long rewardItemId;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;

    public Mission() {
    }

    public Mission(Long id, String title, String description, String type, Integer rewardPoints, Long rewardItemId, Boolean isActive, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.rewardPoints = rewardPoints;
        this.rewardItemId = rewardItemId;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getRewardPoints() { return rewardPoints; }
    public void setRewardPoints(Integer rewardPoints) { this.rewardPoints = rewardPoints; }

    public Long getRewardItemId() { return rewardItemId; }
    public void setRewardItemId(Long rewardItemId) { this.rewardItemId = rewardItemId; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
