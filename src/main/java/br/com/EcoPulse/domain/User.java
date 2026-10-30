package br.com.EcoPulse.domain;

import br.com.EcoPulse.exception.DomainValidationException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;
import java.time.LocalDate;

public class User {
    private Long id;
    private String externalId;
    private String username;
    private String login;
    @JsonIgnore
    private String passwordHash;
    private String name;
    private LocalDate birthDate;
    private String cpf;
    private String nationality;
    private String email;
    private Long familyId;
    private Instant createdAt;
    private Instant updatedAt;

    public User() {
    }

    public User(Long id, String externalId, String username, String email, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.externalId = externalId;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public User(
            Long id,
            String externalId,
            String username,
            String login,
            String passwordHash,
            String name,
            LocalDate birthDate,
            String cpf,
            String nationality,
            String email,
            Long familyId,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.externalId = externalId;
        this.username = username;
        this.login = login;
        this.passwordHash = passwordHash;
        this.name = name;
        this.birthDate = birthDate;
        this.cpf = cpf;
        this.nationality = nationality;
        this.email = email;
        this.familyId = familyId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    @JsonIgnore
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Long getFamilyId() { return familyId; }
    public void setFamilyId(Long familyId) { this.familyId = familyId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    /** Retorna o nome pronto para exibição na aplicação. */
    public String getDisplayName() {
        return username == null || username.isBlank() ? "Usuário sem nome" : username.trim();
    }

    /** Indica se os dados mínimos de contato estão preenchidos. */
    public boolean hasCompleteProfile() {
        return username != null && !username.isBlank()
                && login != null && !login.isBlank()
                && name != null && !name.isBlank()
                && isValidEmail()
                && isValidCpf();
    }

    /** Valida o formato básico do e-mail do agregado. */
    public boolean isValidEmail() {
        return email != null && email.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }

    /** Valida o CPF textual preservando zeros à esquerda. */
    public boolean isValidCpf() {
        if (cpf == null) return false;
        String digits = normalizeCpf(cpf);
        return digits.matches("^\\d{11}$") && !digits.chars().allMatch(ch -> ch == digits.charAt(0));
    }

    public static String normalizeCpf(String rawCpf) {
        return rawCpf == null ? null : rawCpf.replaceAll("[.\\-\\s]", "");
    }

    /** Atualiza os dados editáveis do perfil, mantendo os valores normalizados. */
    public void updateProfile(String username, String email) {
        if (username == null || username.isBlank()) throw new DomainValidationException("Nome obrigatório");
        if (email == null || !email.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw new DomainValidationException("E-mail inválido");
        this.username = username.trim();
        this.email = email.trim().toLowerCase();
        this.updatedAt = Instant.now();
    }

    public void updateProfile(String username, String name, String email, LocalDate birthDate, String nationality, Long familyId) {
        updateProfile(username, email);
        if (name == null || name.isBlank()) throw new DomainValidationException("Nome completo é obrigatório");
        this.name = name.trim();
        this.birthDate = birthDate;
        this.nationality = nationality == null ? null : nationality.trim();
        this.familyId = familyId;
    }
}
