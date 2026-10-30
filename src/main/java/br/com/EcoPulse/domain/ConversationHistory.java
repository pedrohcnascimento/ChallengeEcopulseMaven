package br.com.EcoPulse.domain;

import java.time.Instant;

/**
 * Histórico de conversas pertencente a um usuário (T_SOUL_HISTORICO_CONVERSA).
 * Modelado no domínio persistente, sem exposição de endpoints de chat/IA nesta entrega.
 */
public class ConversationHistory {
    private Long id;
    private Long userId;
    private String content;
    private Instant sentAt;
    private String recipient;

    public ConversationHistory() {
    }

    public ConversationHistory(Long id, Long userId, String content, Instant sentAt, String recipient) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.sentAt = sentAt;
        this.recipient = recipient;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Instant getSentAt() { return sentAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
}
