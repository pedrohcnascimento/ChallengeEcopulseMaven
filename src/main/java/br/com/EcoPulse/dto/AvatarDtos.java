package br.com.EcoPulse.dto;

import br.com.EcoPulse.domain.Avatar;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public final class AvatarDtos {
    private AvatarDtos() { }

    public record AvatarCreateRequest(
            @NotNull(message = "ID do usuário é obrigatório")
            Long userId,
            @NotBlank(message = "Nome do avatar é obrigatório")
            @Size(min = 2, max = 100)
            String name,
            @Size(max = 4000)
            String visualConfig,
            @Size(max = 100)
            String personalityType
    ) { }

    public record AvatarUpdateRequest(
            @NotBlank(message = "Nome do avatar é obrigatório")
            @Size(min = 2, max = 100)
            String name,
            @Size(max = 4000)
            String visualConfig,
            @Size(max = 100)
            String personalityType
    ) { }

    public record ExperienceRequest(
            @Min(value = 1, message = "Os pontos de experiência devem ser positivos")
            long points
    ) { }

    public record AvatarResponse(
            Long id,
            Long userId,
            String name,
            String visualConfig,
            String personalityType,
            Integer level,
            Long experiencePoints,
            long experienceToNextLevel,
            boolean evolved,
            Instant createdAt,
            Instant updatedAt
    ) {
        public static AvatarResponse from(Avatar a) {
            return new AvatarResponse(
                    a.getId(), a.getUserId(), a.getName(), a.getVisualConfig(),
                    a.getPersonalityType(), a.getLevel(), a.getExperiencePoints(),
                    a.getExperienceToNextLevel(), a.isEvolved(),
                    a.getCreatedAt(), a.getUpdatedAt());
        }
    }
}
