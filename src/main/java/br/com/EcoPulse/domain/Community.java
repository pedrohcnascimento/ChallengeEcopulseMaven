package br.com.EcoPulse.domain;

import br.com.EcoPulse.exception.DomainValidationException;
import java.time.Instant;

public class Community {
    private Long id;
    private String name;
    private String description;
    private String communityType;
    private Instant createdAt;

    public Community() {
    }

    public Community(Long id, String name, String description, Instant createdAt) {
        this(id, name, description, "GERAL", createdAt);
    }

    public Community(Long id, String name, String description, String communityType, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.communityType = communityType;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCommunityType() { return communityType; }
    public void setCommunityType(String communityType) { this.communityType = communityType; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public void validate() {
        if (name == null || name.isBlank()) throw new DomainValidationException("Nome da comunidade é obrigatório");
        if (communityType == null || communityType.isBlank()) throw new DomainValidationException("Tipo da comunidade é obrigatório");
        this.name = name.trim();
        this.communityType = communityType.trim();
    }
}
