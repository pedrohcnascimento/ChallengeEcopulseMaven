package br.com.EcoPulse.service;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.Mission;
import br.com.EcoPulse.domain.Reward;
import br.com.EcoPulse.domain.UserMission;
import br.com.EcoPulse.domain.UserReward;
import br.com.EcoPulse.exception.BusinessConflictException;
import br.com.EcoPulse.exception.DomainValidationException;
import br.com.EcoPulse.exception.ResourceNotFoundException;
import br.com.EcoPulse.repository.dao.RewardDao;
import br.com.EcoPulse.repository.dao.UserDao;
import br.com.EcoPulse.repository.dao.UserMissionDao;
import br.com.EcoPulse.repository.dao.UserRewardDao;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class UserMissionService {
    private final UserMissionDao userMissionDao;
    private final UserDao userDao;
    private final MissionService missionService;
    private final RewardDao rewardDao;
    private final UserRewardDao userRewardDao;

    public UserMissionService(
            UserMissionDao userMissionDao,
            UserDao userDao,
            MissionService missionService,
            RewardDao rewardDao,
            UserRewardDao userRewardDao) {
        this.userMissionDao = userMissionDao;
        this.userDao = userDao;
        this.missionService = missionService;
        this.rewardDao = rewardDao;
        this.userRewardDao = userRewardDao;
    }

    public UserMission assignMission(Long userId, Long missionId) {
        if (userId == null || userId <= 0) throw new DomainValidationException("ID do usuário inválido");
        if (missionId == null || missionId <= 0) throw new DomainValidationException("ID da missão inválido");
        if (userDao.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("Usuário não encontrado: " + userId);
        }
        Mission mission = missionService.getRequiredById(missionId);
        Instant now = Instant.now();
        if (!mission.isEligible(now)) {
            throw new DomainValidationException("A missão não está elegível para participação (verifique status ATIVA e período de vigência)");
        }
        if (userMissionDao.findByUserAndMission(userId, missionId).isPresent()) {
            throw new BusinessConflictException("O usuário já possui uma participação registrada para esta missão");
        }

        UserMission participation = new UserMission(
                null, userId, missionId, UserMission.STATUS_EM_ANDAMENTO, 0, null, now);
        return userMissionDao.create(participation);
    }

    public List<UserMission> getAll() {
        return userMissionDao.findAll();
    }

    public List<UserMission> getByUserId(Long userId) {
        if (userId == null || userId <= 0) throw new DomainValidationException("ID do usuário inválido");
        if (userDao.findById(userId).isEmpty()) throw new ResourceNotFoundException("Usuário não encontrado: " + userId);
        return userMissionDao.findByUserId(userId);
    }

    public UserMission getRequiredById(Long id) {
        if (id == null || id <= 0) throw new DomainValidationException("ID da participação inválido");
        return userMissionDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Participação em missão não encontrada: " + id));
    }

    public UserMission updateProgress(Long id, int progress) {
        if (progress == 100) {
            return completeMission(id);
        }
        UserMission participation = getRequiredById(id);
        Mission mission = missionService.getRequiredById(participation.getMissionId());
        if (!mission.isEligible(Instant.now())) {
            throw new DomainValidationException("A missão não está elegível no momento para atualização de progresso");
        }
        participation.updateProgress(progress);
        return userMissionDao.update(participation);
    }

    /** Conclui a participação e concede transacionalmente todas as recompensas vinculadas à missão. */
    public UserMission completeMission(Long id) {
        return ConnectionFactory.executeInTransaction(() -> {
            UserMission participation = getRequiredById(id);
            Mission mission = missionService.getRequiredById(participation.getMissionId());
            Instant now = Instant.now();
            if (!mission.isEligible(now)) {
                throw new DomainValidationException("Não é possível concluir a missão fora do período elegível ou com status INATIVA");
            }
            participation.markCompleted(now);
            UserMission updated = userMissionDao.update(participation);

            List<Reward> missionRewards = rewardDao.findByMissionId(mission.getId());
            for (Reward reward : missionRewards) {
                if (!userRewardDao.existsByUserRewardAndMission(participation.getUserId(), reward.getId(), mission.getId())) {
                    UserReward granted = new UserReward(
                            null,
                            participation.getUserId(),
                            reward.getId(),
                            mission.getId(),
                            now,
                            false);
                    userRewardDao.create(granted);
                }
            }
            return updated;
        });
    }

    public void delete(Long id) {
        getRequiredById(id);
        userMissionDao.deleteById(id);
    }
}
