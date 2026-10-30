package br.com.EcoPulse.domain;

import br.com.EcoPulse.exception.DomainValidationException;
import java.time.Instant;
import java.util.Set;

public class UserMission {
    public static final String STATUS_EM_ANDAMENTO = "EM_ANDAMENTO";
    public static final String STATUS_CONCLUIDA = "CONCLUIDA";
    public static final String STATUS_CANCELADA = "CANCELADA";
    public static final Set<String> ALLOWED_STATUSES = Set.of(STATUS_EM_ANDAMENTO, STATUS_CONCLUIDA, STATUS_CANCELADA);

    private Long id;
    private Long userId;
    private Long missionId;
    private String status;
    private Integer progress;
    private Instant completedAt;
    private Instant assignedAt;

    public UserMission() {
    }

    public UserMission(Long id, Long userId, Long missionId, String status, Integer progress, Instant completedAt, Instant assignedAt) {
        this.id = id;
        this.userId = userId;
        this.missionId = missionId;
        this.status = status;
        this.progress = progress;
        this.completedAt = completedAt;
        this.assignedAt = assignedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public Instant getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Instant assignedAt) { this.assignedAt = assignedAt; }

    public void updateProgress(int newProgress) {
        if (newProgress < 0 || newProgress > 100) throw new DomainValidationException("O progresso da missão deve estar entre 0 e 100%");
        if (STATUS_CONCLUIDA.equals(this.status)) throw new DomainValidationException("Não é possível alterar o progresso de uma missão já concluída");
        if (STATUS_CANCELADA.equals(this.status)) throw new DomainValidationException("Não é possível alterar o progresso de uma participação cancelada");
        this.progress = newProgress;
    }

    public void markCompleted(Instant timestamp) {
        if (STATUS_CONCLUIDA.equals(this.status)) throw new DomainValidationException("A missão já foi concluída por este usuário");
        if (STATUS_CANCELADA.equals(this.status)) throw new DomainValidationException("Não é possível concluir uma participação cancelada");
        this.status = STATUS_CONCLUIDA;
        this.progress = 100;
        this.completedAt = timestamp == null ? Instant.now() : timestamp;
    }
}
