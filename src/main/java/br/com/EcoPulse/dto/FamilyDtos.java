package br.com.EcoPulse.dto;

import br.com.EcoPulse.domain.Family;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class FamilyDtos {
    private FamilyDtos() { }

    public record FamilyRequest(
            @NotBlank(message = "Nome da família é obrigatório")
            @Size(min = 2, max = 150)
            String name,
            @Size(max = 255)
            String relationshipDescription
    ) { }

    public record FamilyResponse(
            Long id,
            String name,
            String relationshipDescription
    ) {
        public static FamilyResponse from(Family f) {
            return new FamilyResponse(f.getId(), f.getName(), f.getRelationshipDescription());
        }
    }
}
