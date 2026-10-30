package br.com.EcoPulse.domain;

import br.com.EcoPulse.exception.DomainValidationException;
import java.time.Instant;
import java.util.Set;

public class Post {
    public static final String STATUS_PUBLICADO = "PUBLICADO";
    public static final String STATUS_RASCUNHO = "RASCUNHO";
    public static final String STATUS_ARQUIVADO = "ARQUIVADO";
    public static final Set<String> ALLOWED_STATUSES = Set.of(STATUS_PUBLICADO, STATUS_RASCUNHO, STATUS_ARQUIVADO);

    private Long id;
    private Long userId;
    private String content;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;

    public Post() {
    }

    public Post(Long id, Long userId, String content, String status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public void validate() {
        if (userId == null || userId <= 0) throw new DomainValidationException("Autor da publicação (userId) é obrigatório");
        if (content == null || content.isBlank()) throw new DomainValidationException("Conteúdo da publicação é obrigatório");
        if (status == null || !ALLOWED_STATUSES.contains(status.trim().toUpperCase())) {
            throw new DomainValidationException("Status de publicação inválido. Valores permitidos: PUBLICADO, RASCUNHO, ARQUIVADO");
        }
        this.content = content.trim();
        this.status = status.trim().toUpperCase();
        if (createdAt != null && updatedAt != null && updatedAt.isBefore(createdAt)) {
            throw new DomainValidationException("updatedAt não pode ser anterior a createdAt");
        }
    }
}
