package br.com.EcoPulse.domain;

import br.com.EcoPulse.exception.DomainValidationException;

public class Reward {
    private Long id;
    private String name;
    private String description;
    private String type;
    private Integer value;
    private String details;

    public Reward() {
    }

    public Reward(Long id, String name, String description, String type, Integer value) {
        this(id, name, description, type, value, null);
    }

    public Reward(Long id, String name, String description, String type, Integer value, String details) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = value;
        this.details = details;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getValue() { return value; }
    public void setValue(Integer value) { this.value = value; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public void validate() {
        if (name == null || name.isBlank()) throw new DomainValidationException("Nome da recompensa é obrigatório");
        if (type == null || type.isBlank()) throw new DomainValidationException("Tipo da recompensa é obrigatório");
        if (value == null || value < 0) throw new DomainValidationException("Valor da recompensa não pode ser negativo");
        if (details != null && details.length() > 2000) throw new DomainValidationException("Detalhes da recompensa excedem 2000 caracteres");
        this.name = name.trim();
        this.type = type.trim().toUpperCase();
    }
}
