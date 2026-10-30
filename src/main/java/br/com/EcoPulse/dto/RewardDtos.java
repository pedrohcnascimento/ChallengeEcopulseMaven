package br.com.EcoPulse.dto;

import br.com.EcoPulse.domain.Reward;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class RewardDtos {
    private RewardDtos() { }

    public record RewardRequest(
            @NotBlank(message = "Nome da recompensa é obrigatório")
            @Size(min = 2, max = 150)
            String name,
            @Size(max = 2000)
            String description,
            @NotBlank(message = "Tipo da recompensa é obrigatório")
            @Size(max = 80)
            String type,
            @NotNull(message = "Valor da recompensa é obrigatório")
            @Min(value = 0, message = "Valor não pode ser negativo")
            Integer value,
            @Size(max = 2000)
            String details
    ) { }

    public record RewardResponse(
            Long id,
            String name,
            String description,
            String type,
            Integer value,
            String details
    ) {
        public static RewardResponse from(Reward r) {
            return new RewardResponse(r.getId(), r.getName(), r.getDescription(), r.getType(), r.getValue(), r.getDetails());
        }
    }
}
