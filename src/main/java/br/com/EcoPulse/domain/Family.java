package br.com.EcoPulse.domain;

import br.com.EcoPulse.exception.DomainValidationException;

public class Family {
    private Long id;
    private String name;
    private String relationshipDescription;

    public Family() {
    }

    public Family(Long id, String name, String relationshipDescription) {
        this.id = id;
        this.name = name;
        this.relationshipDescription = relationshipDescription;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRelationshipDescription() { return relationshipDescription; }
    public void setRelationshipDescription(String relationshipDescription) { this.relationshipDescription = relationshipDescription; }

    public void validate() {
        if (name == null || name.isBlank()) throw new DomainValidationException("Nome da família é obrigatório");
        this.name = name.trim();
        if (relationshipDescription != null) this.relationshipDescription = relationshipDescription.trim();
    }
}
