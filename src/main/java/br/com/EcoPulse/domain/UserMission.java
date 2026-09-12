package br.com.EcoPulse.domain;

import java.time.Instant;

public class UserMission {
    private Long id;
    private Long userId;
    private Long missionId;
    private String status;
    private Integer progress;
    private Instant completedAt;
    private Instant assignedAt;

    public UserMission() {
    }

    public UserMission(Long id, Long userId, Long missionId, String status, Integer progress, Instant completedAt, Instant assignedAt) {
        this.id = id;
        this.userId = userId;
        this.missionId = missionId;
        this.status = status;
        this.progress = progress;
        this.completedAt = completedAt;
        this.assignedAt = assignedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public Instant getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Instant assignedAt) { this.assignedAt = assignedAt; }
}
