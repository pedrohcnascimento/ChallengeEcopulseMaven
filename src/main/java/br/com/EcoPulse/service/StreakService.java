package br.com.EcoPulse.service;

import br.com.EcoPulse.domain.Streak;
import br.com.EcoPulse.exception.DomainValidationException;
import br.com.EcoPulse.exception.ResourceNotFoundException;
import br.com.EcoPulse.repository.dao.StreakDao;
import br.com.EcoPulse.repository.dao.UserDao;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class StreakService {
    private final StreakDao streakDao;
    private final UserDao userDao;

    public StreakService(StreakDao streakDao, UserDao userDao) {
        this.streakDao = streakDao;
        this.userDao = userDao;
    }

    public List<Streak> getAll() {
        return streakDao.findAll();
    }

    public Streak getRequiredById(Long id) {
        if (id == null || id <= 0) throw new DomainValidationException("ID de streak inválido");
        return streakDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Streak não encontrado: " + id));
    }

    public List<Streak> getByUserId(Long userId) {
        if (userId == null || userId <= 0) throw new DomainValidationException("ID do usuário inválido");
        if (userDao.findById(userId).isEmpty()) throw new ResourceNotFoundException("Usuário não encontrado: " + userId);
        return streakDao.findByUserId(userId);
    }

    /** Atualiza ou inicializa a sequência de hábitos do usuário no servidor a partir de uma atividade válida. */
    public Streak recordActivityEvent(Long userId, String activityType, Instant activityTimestamp) {
        String normalizedType = activityType.trim().toUpperCase();
        Optional<Streak> existingOpt = streakDao.findByUserAndType(userId, normalizedType);
        Instant now = Instant.now();

        if (existingOpt.isEmpty()) {
            Streak streak = new Streak(null, userId, normalizedType, 0, 0, null, now, now);
            streak.registerActivity(activityTimestamp, Streak.DEFAULT_ZONE);
            return streakDao.create(streak);
        }
        Streak existing = existingOpt.get();
        existing.registerActivity(activityTimestamp, Streak.DEFAULT_ZONE);
        return streakDao.update(existing);
    }
}
