package br.com.EcoPulse.service;

import br.com.EcoPulse.domain.UserReward;
import br.com.EcoPulse.exception.BusinessConflictException;
import br.com.EcoPulse.exception.DomainValidationException;
import br.com.EcoPulse.exception.ResourceNotFoundException;
import br.com.EcoPulse.repository.dao.MissionDao;
import br.com.EcoPulse.repository.dao.RewardDao;
import br.com.EcoPulse.repository.dao.UserDao;
import br.com.EcoPulse.repository.dao.UserRewardDao;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class UserRewardService {
    private final UserRewardDao userRewardDao;
    private final UserDao userDao;
    private final RewardDao rewardDao;
    private final MissionDao missionDao;

    public UserRewardService(
            UserRewardDao userRewardDao,
            UserDao userDao,
            RewardDao rewardDao,
            MissionDao missionDao) {
        this.userRewardDao = userRewardDao;
        this.userDao = userDao;
        this.rewardDao = rewardDao;
        this.missionDao = missionDao;
    }

    public UserReward acquire(Long userId, Long rewardId, Long sourceMissionId) {
        if (userId == null || userId <= 0) throw new DomainValidationException("ID do usuário inválido");
        if (rewardId == null || rewardId <= 0) throw new DomainValidationException("ID da recompensa inválido");
        if (userDao.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("Usuário não encontrado: " + userId);
        }
        if (rewardDao.findById(rewardId).isEmpty()) {
            throw new ResourceNotFoundException("Recompensa não encontrada: " + rewardId);
        }
        if (sourceMissionId != null) {
            if (missionDao.findById(sourceMissionId).isEmpty()) {
                throw new ResourceNotFoundException("Missão de origem não encontrada: " + sourceMissionId);
            }
            if (userRewardDao.existsByUserRewardAndMission(userId, rewardId, sourceMissionId)) {
                throw new BusinessConflictException("Recompensa já concedida ao usuário para esta missão");
            }
        }
        UserReward ur = new UserReward(null, userId, rewardId, sourceMissionId, Instant.now(), false);
        return userRewardDao.create(ur);
    }

    public List<UserReward> getAll() {
        return userRewardDao.findAll();
    }

    public List<UserReward> getByUserId(Long userId) {
        if (userId == null || userId <= 0) throw new DomainValidationException("ID do usuário inválido");
        if (userDao.findById(userId).isEmpty()) throw new ResourceNotFoundException("Usuário não encontrado: " + userId);
        return userRewardDao.findByUserId(userId);
    }

    public UserReward getRequiredById(Long id) {
        if (id == null || id <= 0) throw new DomainValidationException("ID inválido");
        return userRewardDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recompensa do usuário não encontrada: " + id));
    }

    public UserReward claim(Long id) {
        UserReward ur = getRequiredById(id);
        ur.claim();
        return userRewardDao.update(ur);
    }

    public void delete(Long id) {
        getRequiredById(id);
        userRewardDao.deleteById(id);
    }
}
