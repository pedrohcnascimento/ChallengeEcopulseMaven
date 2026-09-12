package br.com.EcoPulse.domain;

import java.time.Instant;

public class User {
    private Long id;
    private String externalId;
    private String username;
    private String email;
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
