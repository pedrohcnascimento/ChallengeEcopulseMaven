package br.com.EcoPulse.domain;

public class Reward {
    private Long id;
    private String name;
    private String description;
    private String type;
    private Integer value;

    public Reward() {
    }

    public Reward(Long id, String name, String description, String type, Integer value) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = value;
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
}
