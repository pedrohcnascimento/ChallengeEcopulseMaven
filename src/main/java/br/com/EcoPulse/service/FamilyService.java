package br.com.EcoPulse.service;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.Family;
import br.com.EcoPulse.domain.User;
import br.com.EcoPulse.exception.DomainValidationException;
import br.com.EcoPulse.exception.ResourceNotFoundException;
import br.com.EcoPulse.repository.dao.FamilyDao;
import br.com.EcoPulse.repository.dao.UserDao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FamilyService {
    private final FamilyDao familyDao;
    private final UserDao userDao;

    public FamilyService(FamilyDao familyDao, UserDao userDao) {
        this.familyDao = familyDao;
        this.userDao = userDao;
    }

    public Family create(Family family) {
        if (family == null) throw new DomainValidationException("Família obrigatória");
        family.validate();
        return familyDao.create(family);
    }

    public List<Family> getAll() {
        return familyDao.findAll();
    }

    public Family getRequiredById(Long id) {
        if (id == null || id <= 0) throw new DomainValidationException("ID da família inválido");
        return familyDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Família não encontrada: " + id));
    }

    public List<User> getMembers(Long familyId) {
        getRequiredById(familyId);
        return userDao.findByFamilyId(familyId);
    }

    public Family update(Long id, String name, String relationshipDescription) {
        Family existing = getRequiredById(id);
        existing.setName(name);
        existing.setRelationshipDescription(relationshipDescription);
        existing.validate();
        return familyDao.update(existing);
    }

    public void delete(Long id) {
        getRequiredById(id);
        ConnectionFactory.executeInTransaction(() -> {
            userDao.clearFamilyReferences(id);
            familyDao.deleteById(id);
        });
    }
}
