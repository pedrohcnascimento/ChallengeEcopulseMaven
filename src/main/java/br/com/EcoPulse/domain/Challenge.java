package br.com.EcoPulse.domain;

import java.time.Instant;

public class Challenge {
    private Long id;
    private String title;
    private String description;
    private String rules;
    private Instant startDate;
    private Instant endDate;
    private Integer rewardPoints;
    private String status;

    public Challenge() {
    }

    public Challenge(Long id, String title, String description, String rules, Instant startDate, Instant endDate, Integer rewardPoints, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.rules = rules;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rewardPoints = rewardPoints;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRules() { return rules; }
    public void setRules(String rules) { this.rules = rules; }

    public Instant getStartDate() { return startDate; }
    public void setStartDate(Instant startDate) { this.startDate = startDate; }

    public Instant getEndDate() { return endDate; }
    public void setEndDate(Instant endDate) { this.endDate = endDate; }

    public Integer getRewardPoints() { return rewardPoints; }
    public void setRewardPoints(Integer rewardPoints) { this.rewardPoints = rewardPoints; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
