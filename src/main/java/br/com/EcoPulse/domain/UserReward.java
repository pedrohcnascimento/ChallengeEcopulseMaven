package br.com.EcoPulse.domain;

import br.com.EcoPulse.exception.DomainValidationException;
import java.time.Instant;

public class UserReward {
    private Long id;
    private Long userId;
    private Long rewardId;
    private Long sourceMissionId;
    private Instant acquiredAt;
    private Boolean isClaimed;

    public UserReward() {
    }

    public UserReward(Long id, Long userId, Long rewardId, Instant acquiredAt, Boolean isClaimed) {
        this(id, userId, rewardId, null, acquiredAt, isClaimed);
    }

    public UserReward(Long id, Long userId, Long rewardId, Long sourceMissionId, Instant acquiredAt, Boolean isClaimed) {
        this.id = id;
        this.userId = userId;
        this.rewardId = rewardId;
        this.sourceMissionId = sourceMissionId;
        this.acquiredAt = acquiredAt;
        this.isClaimed = isClaimed;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getRewardId() { return rewardId; }
    public void setRewardId(Long rewardId) { this.rewardId = rewardId; }

    public Long getSourceMissionId() { return sourceMissionId; }
    public void setSourceMissionId(Long sourceMissionId) { this.sourceMissionId = sourceMissionId; }

    public Instant getAcquiredAt() { return acquiredAt; }
    public void setAcquiredAt(Instant acquiredAt) { this.acquiredAt = acquiredAt; }

    public Boolean getIsClaimed() { return isClaimed; }
    public void setIsClaimed(Boolean isClaimed) { this.isClaimed = isClaimed; }

    public void claim() {
        if (Boolean.TRUE.equals(this.isClaimed)) throw new DomainValidationException("Esta recompensa já foi resgatada");
        this.isClaimed = true;
    }
}
