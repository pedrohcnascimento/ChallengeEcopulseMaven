package br.com.EcoPulse.dto;

import br.com.EcoPulse.domain.Activity;
import br.com.EcoPulse.domain.Streak;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public final class HabitDtos {
    private HabitDtos() { }

    public record ActivityCreateRequest(
            @NotNull(message = "userId é obrigatório")
            Long userId,
            @NotBlank(message = "Tipo de atividade é obrigatório")
            @Size(max = 80)
            String type,
            Instant timestamp,
            @Size(max = 2000)
            String metadata
    ) { }

    public record ActivityResponse(
            Long id,
            Long userId,
            String type,
            Instant timestamp,
            String metadata
    ) {
        public static ActivityResponse from(Activity a) {
            return new ActivityResponse(a.getId(), a.getUserId(), a.getType(), a.getTimestamp(), a.getMetadata());
        }
    }

    public record StreakResponse(
            Long id,
            Long userId,
            String type,
            Integer currentCount,
            Integer highestCount,
            Instant lastActivityAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        public static StreakResponse from(Streak s) {
            return new StreakResponse(
                    s.getId(), s.getUserId(), s.getType(),
                    s.getCurrentCount(), s.getHighestCount(),
                    s.getLastActivityAt(), s.getCreatedAt(), s.getUpdatedAt());
        }
    }
}
