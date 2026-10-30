package br.com.EcoPulse.dto;

import br.com.EcoPulse.domain.Community;
import br.com.EcoPulse.domain.MissionCommunity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public final class CommunityDtos {
    private CommunityDtos() { }

    public record CommunityRequest(
            @NotBlank(message = "Nome da comunidade é obrigatório")
            @Size(min = 2, max = 150)
            String name,
            @Size(max = 2000)
            String description,
            @NotBlank(message = "Tipo da comunidade (communityType) é obrigatório")
            @Size(max = 80)
            String communityType
    ) { }

    public record CommunityResponse(
            Long id,
            String name,
            String description,
            String communityType,
            Instant createdAt
    ) {
        public static CommunityResponse from(Community c) {
            return new CommunityResponse(c.getId(), c.getName(), c.getDescription(), c.getCommunityType(), c.getCreatedAt());
        }
    }

    public record MissionCommunityRequest(
            @NotNull(message = "missionId é obrigatório")
            Long missionId,
            @Min(0)
            Integer score,
            String status
    ) { }

    public record MissionCommunityResponse(
            Long missionId,
            Long communityId,
            Integer score,
            String status
    ) {
        public static MissionCommunityResponse from(MissionCommunity mc) {
            return new MissionCommunityResponse(mc.getMissionId(), mc.getCommunityId(), mc.getScore(), mc.getStatus());
        }
    }
}
