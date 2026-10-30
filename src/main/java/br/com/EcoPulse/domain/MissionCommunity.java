package br.com.EcoPulse.domain;

import br.com.EcoPulse.exception.DomainValidationException;

/** Associação N:N entre Missão e Comunidade (T_SOUL_DESAFIO_COMUN), substituindo GroupChallenge. */
public class MissionCommunity {
    private Long missionId;
    private Long communityId;
    private Integer score;
    private String status;

    public MissionCommunity() {
    }

    public MissionCommunity(Long missionId, Long communityId, Integer score, String status) {
        this.missionId = missionId;
        this.communityId = communityId;
        this.score = score;
        this.status = status;
    }

    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }

    public Long getCommunityId() { return communityId; }
    public void setCommunityId(Long communityId) { this.communityId = communityId; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public void validate() {
        if (missionId == null || missionId <= 0) throw new DomainValidationException("Missão é obrigatória para vínculo com comunidade");
        if (communityId == null || communityId <= 0) throw new DomainValidationException("Comunidade é obrigatória para vínculo com missão");
        if (score == null || score < 0) throw new DomainValidationException("A pontuação da comunidade não pode ser negativa");
        if (status == null || status.isBlank()) throw new DomainValidationException("Status da participação da comunidade é obrigatório");
        this.status = status.trim().toUpperCase();
    }
}
