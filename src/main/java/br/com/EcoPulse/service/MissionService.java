package br.com.EcoPulse.service;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.Mission;
import br.com.EcoPulse.domain.Reward;
import br.com.EcoPulse.exception.BusinessConflictException;
import br.com.EcoPulse.exception.DomainValidationException;
import br.com.EcoPulse.exception.ResourceNotFoundException;
import br.com.EcoPulse.repository.dao.MissionDao;
import br.com.EcoPulse.repository.dao.MissionRewardDao;
import br.com.EcoPulse.repository.dao.RewardDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * BO/Service da entidade unificada Mission (T_SOUL_DESAFIO), garantindo a regra transacional
 * obrigatória de associação com ao menos uma recompensa previamente cadastrada.
 */
@Service
public class MissionService {
    private final MissionDao dao;
    private final RewardDao rewardDao;
    private final MissionRewardDao missionRewardDao;

    public MissionService() {
        this(new MissionDao(), new RewardDao(), new MissionRewardDao());
    }

    @Autowired
    public MissionService(MissionDao dao, RewardDao rewardDao, MissionRewardDao missionRewardDao) {
        this.dao = dao;
        this.rewardDao = rewardDao;
        this.missionRewardDao = missionRewardDao;
    }

    public Mission create(Mission mission, List<Long> rewardIds) {
        if (mission == null) throw new DomainValidationException("Missão obrigatória");
        if (rewardIds == null || rewardIds.isEmpty()) {
            throw new DomainValidationException("A missão deve estar associada a pelo menos uma recompensa previamente cadastrada");
        }
        Set<Long> uniqueRewardIds = new LinkedHashSet<>();
        for (Long rewardId : rewardIds) {
            if (rewardId == null || rewardId <= 0) {
                throw new DomainValidationException("ID de recompensa inválido na lista de associação");
            }
            uniqueRewardIds.add(rewardId);
        }

        if (mission.getStatus() == null || mission.getStatus().isBlank()) {
            mission.setStatus(Mission.STATUS_ATIVA);
        }
        mission.validateInvariants();
        Instant now = Instant.now();
        mission.setCreatedAt(now);
        mission.setUpdatedAt(now);

        return ConnectionFactory.executeInTransaction(() -> {
            for (Long rewardId : uniqueRewardIds) {
                if (rewardDao.findById(rewardId).isEmpty()) {
                    throw new ResourceNotFoundException("Recompensa não encontrada: " + rewardId + ". Cadastre a recompensa antes de criar a missão.");
                }
            }
            Mission created = dao.create(mission);
            for (Long rewardId : uniqueRewardIds) {
                missionRewardDao.addAssociation(created.getId(), rewardId);
            }
            return created;
        });
    }

    public List<Mission> getAll() {
        return dao.findAll(false);
    }

    public List<Mission> getActive() {
        return dao.findAll(true);
    }

    public List<Mission> getEligible() {
        Instant now = Instant.now();
        return dao.findAll(true).stream().filter(m -> m.isEligible(now)).toList();
    }

    public Optional<Mission> findById(Long id) {
        if (id == null || id <= 0) throw new DomainValidationException("ID inválido");
        return dao.findById(id);
    }

    public Mission getRequiredById(Long id) {
        return findById(id).orElseThrow(() -> new ResourceNotFoundException("Missão não encontrada: " + id));
    }

    public List<Reward> getRewardsByMission(Long missionId) {
        getRequiredById(missionId);
        return rewardDao.findByMissionId(missionId);
    }

    public List<Reward> addRewardToMission(Long missionId, Long rewardId) {
        getRequiredById(missionId);
        if (rewardId == null || rewardId <= 0) throw new DomainValidationException("ID da recompensa inválido");
        if (rewardDao.findById(rewardId).isEmpty()) {
            throw new ResourceNotFoundException("Recompensa não encontrada: " + rewardId);
        }
        if (missionRewardDao.existsAssociation(missionId, rewardId)) {
            throw new BusinessConflictException("Esta recompensa já está associada à missão");
        }
        missionRewardDao.addAssociation(missionId, rewardId);
        return rewardDao.findByMissionId(missionId);
    }

    public void removeRewardFromMission(Long missionId, Long rewardId) {
        getRequiredById(missionId);
        if (!missionRewardDao.existsAssociation(missionId, rewardId)) {
            throw new ResourceNotFoundException("Associação entre missão e recompensa não encontrada");
        }
        if (missionRewardDao.countByMissionId(missionId) <= 1) {
            throw new DomainValidationException("Não é permitido remover a única recompensa da missão; toda missão deve possuir ao menos uma recompensa");
        }
        missionRewardDao.removeAssociation(missionId, rewardId);
    }

    public Mission updateDetails(Long id, String title, String description, String type, String rules, Instant startDate, Instant endDate) {
        Mission m = getRequiredById(id);
        m.updateDetails(title, description, type, rules, startDate, endDate);
        return dao.update(m);
    }

    public Mission changeStatus(Long id, String newStatus) {
        Mission m = getRequiredById(id);
        if (newStatus == null || !Mission.ALLOWED_STATUSES.contains(newStatus.trim().toUpperCase())) {
            throw new DomainValidationException("Status inválido. Valores aceitos: ATIVA, INATIVA");
        }
        if (Mission.STATUS_ATIVA.equalsIgnoreCase(newStatus.trim())) m.activate();
        else m.deactivate();
        return dao.update(m);
    }

    public Mission changeStatus(Long id, boolean active) {
        return changeStatus(id, active ? Mission.STATUS_ATIVA : Mission.STATUS_INATIVA);
    }

    public boolean delete(Long id) {
        getRequiredById(id);
        return ConnectionFactory.executeInTransaction(() -> {
            missionRewardDao.deleteAllByMissionId(id);
            return dao.deleteById(id);
        });
    }
}
