package br.com.EcoPulse.domain;

public class GroupChallenge {
    private Long id;
    private Long groupId;
    private Long challengeId;
    private Integer score;
    private String status;

    public GroupChallenge() {
    }

    public GroupChallenge(Long id, Long groupId, Long challengeId, Integer score, String status) {
        this.id = id;
        this.groupId = groupId;
        this.challengeId = challengeId;
        this.score = score;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public Long getChallengeId() { return challengeId; }
    public void setChallengeId(Long challengeId) { this.challengeId = challengeId; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
