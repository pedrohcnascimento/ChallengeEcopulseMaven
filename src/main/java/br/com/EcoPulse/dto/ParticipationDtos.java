package br.com.EcoPulse.dto;

import br.com.EcoPulse.domain.UserMission;
import br.com.EcoPulse.domain.UserReward;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public final class ParticipationDtos {
    private ParticipationDtos() { }

    public record AssignMissionRequest(
            @NotNull(message = "userId é obrigatório")
            Long userId,
            @NotNull(message = "missionId é obrigatório")
            Long missionId
    ) { }

    public record ProgressUpdateRequest(
            @NotNull(message = "Progresso é obrigatório")
            @Min(0)
            @Max(100)
            Integer progress
    ) { }

    public record UserMissionResponse(
            Long id,
            Long userId,
            Long missionId,
            String status,
            Integer progress,
            Instant completedAt,
            Instant assignedAt
    ) {
        public static UserMissionResponse from(UserMission um) {
            return new UserMissionResponse(
                    um.getId(), um.getUserId(), um.getMissionId(),
                    um.getStatus(), um.getProgress(), um.getCompletedAt(), um.getAssignedAt());
        }
    }

    public record AcquireRewardRequest(
            @NotNull(message = "userId é obrigatório")
            Long userId,
            @NotNull(message = "rewardId é obrigatório")
            Long rewardId,
            Long sourceMissionId
    ) { }

    public record UserRewardResponse(
            Long id,
            Long userId,
            Long rewardId,
            Long sourceMissionId,
            Instant acquiredAt,
            Boolean isClaimed
    ) {
        public static UserRewardResponse from(UserReward ur) {
            return new UserRewardResponse(
                    ur.getId(), ur.getUserId(), ur.getRewardId(),
                    ur.getSourceMissionId(), ur.getAcquiredAt(), ur.getIsClaimed());
        }
    }
}
