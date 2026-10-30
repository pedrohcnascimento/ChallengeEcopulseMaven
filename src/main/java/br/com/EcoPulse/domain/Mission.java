package br.com.EcoPulse.domain;

import br.com.EcoPulse.exception.DomainValidationException;
import java.time.Instant;
import java.util.Set;

public class Mission {
    public static final String STATUS_ATIVA = "ATIVA";
    public static final String STATUS_INATIVA = "INATIVA";
    public static final Set<String> ALLOWED_STATUSES = Set.of(STATUS_ATIVA, STATUS_INATIVA);

    private Long id;
    private String title;
    private String description;
    private String type;
    private String rules;
    private Instant startDate;
    private Instant endDate;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;

    public Mission() {
    }

    public Mission(Long id, String title, String description, String type, String rules, Instant startDate, Instant endDate, String status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.rules = rules;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getRules() { return rules; }
    public void setRules(String rules) { this.rules = rules; }

    public Instant getStartDate() { return startDate; }
    public void setStartDate(Instant startDate) { this.startDate = startDate; }

    public Instant getEndDate() { return endDate; }
    public void setEndDate(Instant endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public boolean isAvailable() { return isEligible(Instant.now()); }
    public boolean isEligible(Instant now) {
        Instant ref = now == null ? Instant.now() : now;
        if (!STATUS_ATIVA.equalsIgnoreCase(status) || startDate == null) return false;
        if (ref.isBefore(startDate)) return false;
        return endDate == null || !ref.isAfter(endDate);
    }
    public String getDisplayTitle() { return title == null || title.isBlank() ? "Missão sem título" : title.trim(); }
    public void activate() { status = STATUS_ATIVA; updatedAt = Instant.now(); }
    public void deactivate() { status = STATUS_INATIVA; updatedAt = Instant.now(); }
    public void updateDetails(String title, String description, String type, String rules, Instant startDate, Instant endDate) {
        if (title == null || title.isBlank()) throw new DomainValidationException("Título obrigatório");
        if (type == null || type.isBlank()) throw new DomainValidationException("Tipo da missão é obrigatório");
        if (startDate == null) throw new DomainValidationException("Data de início da missão é obrigatória");
        if (endDate != null && !endDate.isAfter(startDate)) throw new DomainValidationException("A data de fim deve ser posterior à data de início");
        this.title = title.trim();
        this.description = description;
        this.type = type.trim();
        this.rules = rules;
        this.startDate = startDate;
        this.endDate = endDate;
        this.updatedAt = Instant.now();
    }
    public void validateInvariants() {
        if (title == null || title.isBlank()) throw new DomainValidationException("Título da missão é obrigatório");
        if (type == null || type.isBlank()) throw new DomainValidationException("Tipo da missão é obrigatório");
        if (startDate == null) throw new DomainValidationException("Data de início da missão (startDate) é obrigatória");
        if (endDate != null && !endDate.isAfter(startDate)) throw new DomainValidationException("A data de fim (endDate) deve ser posterior à data de início (startDate)");
        if (status == null || !ALLOWED_STATUSES.contains(status.trim().toUpperCase())) {
            throw new DomainValidationException("Status da missão inválido. Valores permitidos: ATIVA, INATIVA");
        }
        this.title = title.trim();
        this.type = type.trim();
        this.status = status.trim().toUpperCase();
    }
}
