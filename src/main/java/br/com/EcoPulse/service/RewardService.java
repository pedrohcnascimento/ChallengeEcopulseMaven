package br.com.EcoPulse.service;

import br.com.EcoPulse.domain.Reward;
import br.com.EcoPulse.exception.BusinessConflictException;
import br.com.EcoPulse.exception.DomainValidationException;
import br.com.EcoPulse.exception.ResourceNotFoundException;
import br.com.EcoPulse.repository.dao.MissionRewardDao;
import br.com.EcoPulse.repository.dao.RewardDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RewardService {
    private final RewardDao rewardDao;
    private final MissionRewardDao missionRewardDao;

    public RewardService() {
        this(new RewardDao(), new MissionRewardDao());
    }

    @Autowired
    public RewardService(RewardDao rewardDao, MissionRewardDao missionRewardDao) {
        this.rewardDao = rewardDao;
        this.missionRewardDao = missionRewardDao;
    }

    public Reward create(Reward reward) {
        if (reward == null) throw new DomainValidationException("Recompensa obrigatória");
        reward.validate();
        return rewardDao.create(reward);
    }

    public List<Reward> getAll() {
        return rewardDao.findAll();
    }

    public Reward getRequiredById(Long id) {
        if (id == null || id <= 0) throw new DomainValidationException("ID da recompensa inválido");
        return rewardDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recompensa não encontrada: " + id));
    }

    public Reward update(Long id, Reward updated) {
        Reward existing = getRequiredById(id);
        if (updated == null) throw new DomainValidationException("Dados da recompensa obrigatórios");
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setType(updated.getType());
        existing.setValue(updated.getValue());
        existing.setDetails(updated.getDetails());
        existing.validate();
        return rewardDao.update(existing);
    }

    public void delete(Long id) {
        getRequiredById(id);
        if (missionRewardDao.countByRewardId(id) > 0) {
            throw new BusinessConflictException("Não é possível excluir a recompensa pois ela está associada a uma ou mais missões");
        }
        rewardDao.deleteById(id);
    }
}
