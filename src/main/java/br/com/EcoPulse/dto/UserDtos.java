package br.com.EcoPulse.dto;

import br.com.EcoPulse.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;

public final class UserDtos {
    private UserDtos() { }

    public record UserCreateRequest(
            String externalId,
            @NotBlank(message = "Username público é obrigatório")
            @Size(min = 2, max = 100)
            String username,
            @NotBlank(message = "Login é obrigatório")
            @Size(min = 3, max = 80)
            String login,
            @NotBlank(message = "Senha é obrigatória")
            @Size(min = 6, max = 100)
            String password,
            @NotBlank(message = "Nome é obrigatório")
            @Size(min = 2, max = 150)
            String name,
            LocalDate birthDate,
            @NotBlank(message = "CPF é obrigatório")
            String cpf,
            String nationality,
            @NotBlank(message = "E-mail é obrigatório")
            @Email(message = "E-mail inválido")
            String email,
            Long familyId
    ) { }

    public record UserUpdateRequest(
            String externalId,
            @NotBlank(message = "Username público é obrigatório")
            @Size(min = 2, max = 100)
            String username,
            @NotBlank(message = "Nome é obrigatório")
            @Size(min = 2, max = 150)
            String name,
            LocalDate birthDate,
            String nationality,
            @NotBlank(message = "E-mail é obrigatório")
            @Email(message = "E-mail inválido")
            String email,
            Long familyId
    ) { }

    /** Representação pública sem dados sensíveis (oculta passwordHash, cpf e birthDate). */
    public record UserPublicResponse(
            Long id,
            String externalId,
            String username,
            String login,
            String name,
            String nationality,
            String email,
            Long familyId,
            Instant createdAt,
            Instant updatedAt
    ) {
        public static UserPublicResponse from(User u) {
            return new UserPublicResponse(
                    u.getId(), u.getExternalId(), u.getUsername(), u.getLogin(),
                    u.getName(), u.getNationality(), u.getEmail(), u.getFamilyId(),
                    u.getCreatedAt(), u.getUpdatedAt());
        }
    }

    /** Representação privada do titular autenticado (nunca inclui passwordHash). */
    public record UserDetailResponse(
            Long id,
            String externalId,
            String username,
            String login,
            String name,
            LocalDate birthDate,
            String cpf,
            String nationality,
            String email,
            Long familyId,
            Instant createdAt,
            Instant updatedAt
    ) {
        public static UserDetailResponse from(User u) {
            return new UserDetailResponse(
                    u.getId(), u.getExternalId(), u.getUsername(), u.getLogin(),
                    u.getName(), u.getBirthDate(), u.getCpf(), u.getNationality(),
                    u.getEmail(), u.getFamilyId(), u.getCreatedAt(), u.getUpdatedAt());
        }
    }
}
