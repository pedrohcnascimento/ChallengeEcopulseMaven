package br.com.EcoPulse.service;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.Activity;
import br.com.EcoPulse.exception.DomainValidationException;
import br.com.EcoPulse.exception.ResourceNotFoundException;
import br.com.EcoPulse.repository.dao.ActivityDao;
import br.com.EcoPulse.repository.dao.UserDao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {
    private final ActivityDao activityDao;
    private final UserDao userDao;
    private final StreakService streakService;

    public ActivityService(ActivityDao activityDao, UserDao userDao, StreakService streakService) {
        this.activityDao = activityDao;
        this.userDao = userDao;
        this.streakService = streakService;
    }

    /** Registra a atividade e atualiza a respectiva Streak de forma transacional. */
    public Activity register(Activity activity) {
        if (activity == null) throw new DomainValidationException("Atividade obrigatória");
        activity.validate();
        if (userDao.findById(activity.getUserId()).isEmpty()) {
            throw new ResourceNotFoundException("Usuário não encontrado: " + activity.getUserId());
        }
        return ConnectionFactory.executeInTransaction(() -> {
            Activity created = activityDao.create(activity);
            streakService.recordActivityEvent(created.getUserId(), created.getType(), created.getTimestamp());
            return created;
        });
    }

    public List<Activity> getAll() {
        return activityDao.findAll();
    }

    public Activity getRequiredById(Long id) {
        if (id == null || id <= 0) throw new DomainValidationException("ID da atividade inválido");
        return activityDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atividade não encontrada: " + id));
    }

    public List<Activity> getByUserId(Long userId) {
        if (userId == null || userId <= 0) throw new DomainValidationException("ID do usuário inválido");
        if (userDao.findById(userId).isEmpty()) throw new ResourceNotFoundException("Usuário não encontrado: " + userId);
        return activityDao.findByUserId(userId);
    }

    public void delete(Long id) {
        getRequiredById(id);
        activityDao.deleteById(id);
    }
}
