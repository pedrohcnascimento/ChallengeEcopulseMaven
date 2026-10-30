package br.com.EcoPulse.service;

import br.com.EcoPulse.domain.Avatar;
import br.com.EcoPulse.exception.DomainValidationException;
import br.com.EcoPulse.exception.ResourceNotFoundException;
import br.com.EcoPulse.repository.dao.AvatarDao;
import br.com.EcoPulse.repository.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class AvatarService {
    private final AvatarDao dao;
    private final UserDao userDao;

    public AvatarService() {
        this(new AvatarDao(), new UserDao());
    }

    @Autowired
    public AvatarService(AvatarDao dao, UserDao userDao) {
        this.dao = dao;
        this.userDao = userDao;
    }

    public Avatar create(Avatar avatar) {
        if (avatar == null || avatar.getUserId() == null) {
            throw new DomainValidationException("Usuário do avatar é obrigatório");
        }
        if (userDao.findById(avatar.getUserId()).isEmpty()) {
            throw new ResourceNotFoundException("Usuário não encontrado: " + avatar.getUserId() + ". Cadastre o usuário antes de criar o avatar.");
        }
        if (avatar.getName() == null || avatar.getName().isBlank()) {
            throw new DomainValidationException("Nome do avatar é obrigatório");
        }
        avatar.setName(avatar.getName().trim());
        avatar.setLevel(1);
        avatar.setExperiencePoints(0L);
        Instant now = Instant.now();
        avatar.setCreatedAt(now);
        avatar.setUpdatedAt(now);
        return dao.create(avatar);
    }

    public List<Avatar> getAll() {
        return dao.findAll();
    }

    public List<Avatar> getByUserId(Long userId) {
        if (userId == null || userId <= 0) throw new DomainValidationException("ID do usuário inválido");
        if (userDao.findById(userId).isEmpty()) throw new ResourceNotFoundException("Usuário não encontrado: " + userId);
        return dao.findByUserId(userId);
    }

    public Optional<Avatar> findById(Long id) {
        if (id == null || id <= 0) throw new DomainValidationException("ID inválido");
        return dao.findById(id);
    }

    public Avatar getRequiredById(Long id) {
        return findById(id).orElseThrow(() -> new ResourceNotFoundException("Avatar não encontrado: " + id));
    }

    public Avatar updateCustomization(Long id, String name, String visualConfig, String personalityType) {
        Avatar avatar = getRequiredById(id);
        if (name == null || name.isBlank()) throw new DomainValidationException("Nome do avatar é obrigatório");
        avatar.setName(name.trim());
        avatar.setVisualConfig(visualConfig);
        avatar.setPersonalityType(personalityType);
        avatar.setUpdatedAt(Instant.now());
        return dao.update(avatar);
    }

    public Avatar addExperience(Long id, long points) {
        Avatar a = getRequiredById(id);
        a.addExperience(points);
        return dao.update(a);
    }

    public boolean delete(Long id) {
        getRequiredById(id);
        return dao.deleteById(id);
    }
}
