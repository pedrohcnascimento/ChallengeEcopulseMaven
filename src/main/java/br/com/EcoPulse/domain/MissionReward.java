package br.com.EcoPulse.domain;

public class MissionReward {
    private Long missionId;
    private Long rewardId;

    public MissionReward() {
    }

    public MissionReward(Long missionId, Long rewardId) {
        this.missionId = missionId;
        this.rewardId = rewardId;
    }

    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }

    public Long getRewardId() { return rewardId; }
    public void setRewardId(Long rewardId) { this.rewardId = rewardId; }
}
