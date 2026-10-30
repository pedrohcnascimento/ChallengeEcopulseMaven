package br.com.EcoPulse.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public final class AuthDtos {
    private AuthDtos() { }

    public record RegisterRequest(
            String externalId,
            @NotBlank(message = "Username público é obrigatório")
            @Size(min = 2, max = 100)
            String username,
            @NotBlank(message = "Login é obrigatório")
            @Size(min = 3, max = 80)
            String login,
            @NotBlank(message = "Senha é obrigatória")
            @Size(min = 6, max = 100, message = "A senha deve ter ao menos 6 caracteres")
            String password,
            @NotBlank(message = "Nome completo é obrigatório")
            @Size(min = 2, max = 150)
            String name,
            LocalDate birthDate,
            @NotBlank(message = "CPF é obrigatório")
            String cpf,
            String nationality,
            @NotBlank(message = "E-mail é obrigatório")
            @Email(message = "Formato de e-mail inválido")
            String email,
            Long familyId
    ) { }

    public record LoginRequest(
            @NotBlank(message = "Login é obrigatório")
            String login,
            @NotBlank(message = "Senha é obrigatória")
            String password
    ) { }

    public record AuthResponse(
            String message,
            UserDtos.UserDetailResponse user
    ) { }
}
