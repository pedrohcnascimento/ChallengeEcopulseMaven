package br.com.EcoPulse.dto;

import br.com.EcoPulse.domain.Mission;
import br.com.EcoPulse.domain.Reward;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public final class MissionDtos {
    private MissionDtos() { }

    public record MissionCreateRequest(
            @NotBlank(message = "Título da missão é obrigatório")
            @Size(min = 2, max = 150)
            String title,
            @Size(max = 2000)
            String description,
            @NotBlank(message = "Tipo da missão é obrigatório")
            @Size(max = 80)
            String type,
            @Size(max = 4000)
            String rules,
            @NotNull(message = "Data de início (startDate) é obrigatória")
            Instant startDate,
            Instant endDate,
            String status,
            @NotEmpty(message = "A missão deve estar vinculada a pelo menos uma recompensa previamente cadastrada")
            List<Long> rewardIds
    ) { }

    public record MissionUpdateRequest(
            @NotBlank(message = "Título da missão é obrigatório")
            @Size(min = 2, max = 150)
            String title,
            @Size(max = 2000)
            String description,
            @NotBlank(message = "Tipo da missão é obrigatório")
            @Size(max = 80)
            String type,
            @Size(max = 4000)
            String rules,
            @NotNull(message = "Data de início (startDate) é obrigatória")
            Instant startDate,
            Instant endDate
    ) { }

    public record MissionStatusRequest(
            @NotBlank(message = "Status é obrigatório (ATIVA ou INATIVA)")
            String status
    ) { }

    public record MissionResponse(
            Long id,
            String title,
            String description,
            String type,
            String rules,
            Instant startDate,
            Instant endDate,
            String status,
            boolean eligibleNow,
            List<RewardDtos.RewardResponse> rewards,
            Instant createdAt,
            Instant updatedAt
    ) {
        public static MissionResponse from(Mission m, List<Reward> rewards) {
            List<RewardDtos.RewardResponse> rewardDtos = rewards == null
                    ? List.of()
                    : rewards.stream().map(RewardDtos.RewardResponse::from).toList();
            return new MissionResponse(
                    m.getId(), m.getTitle(), m.getDescription(), m.getType(), m.getRules(),
                    m.getStartDate(), m.getEndDate(), m.getStatus(), m.isEligible(Instant.now()),
                    rewardDtos, m.getCreatedAt(), m.getUpdatedAt());
        }
    }
}
