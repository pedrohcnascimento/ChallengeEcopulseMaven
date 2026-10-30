package br.com.EcoPulse.service;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.User;
import br.com.EcoPulse.exception.BusinessConflictException;
import br.com.EcoPulse.exception.DomainValidationException;
import br.com.EcoPulse.exception.ResourceNotFoundException;
import br.com.EcoPulse.repository.dao.FamilyDao;
import br.com.EcoPulse.repository.dao.UserDao;
import br.com.EcoPulse.security.PasswordHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/** Regras de negócio e orquestração (BO) do agregado User. */
@Service
public class UserService {
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private final UserDao userDao;
    private final FamilyDao familyDao;
    private final PasswordHasher passwordHasher;

    public UserService() {
        this(new UserDao(), new FamilyDao(), new PasswordHasher());
    }

    public UserService(UserDao userDao) {
        this(userDao, new FamilyDao(), new PasswordHasher());
    }

    @Autowired
    public UserService(UserDao userDao, FamilyDao familyDao, PasswordHasher passwordHasher) {
        this.userDao = userDao;
        this.familyDao = familyDao;
        this.passwordHasher = passwordHasher;
    }

    public User create(User user, String rawPassword) {
        if (rawPassword == null || rawPassword.length() < 6) {
            throw new DomainValidationException("A senha deve possuir ao menos 6 caracteres");
        }
        user.setPasswordHash(passwordHasher.hash(rawPassword));
        return create(user);
    }

    public User create(User user) {
        validateForCreate(user);
        normalize(user);

        if (userDao.findByLogin(user.getLogin()).isPresent()) {
            throw new BusinessConflictException("Já existe um usuário com o login informado: " + user.getLogin());
        }
        if (userDao.findByEmail(user.getEmail()).isPresent()) {
            throw new BusinessConflictException("Já existe um usuário com o e-mail informado: " + user.getEmail());
        }
        if (userDao.findByCpf(user.getCpf()).isPresent()) {
            throw new BusinessConflictException("Já existe um usuário com o CPF informado");
        }
        validateFamilyExists(user.getFamilyId());

        Instant now = Instant.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return userDao.create(user);
    }

    public List<User> getAll() {
        return userDao.findAll();
    }

    public List<User> searchByUsername(String username) {
        if (username == null || username.isBlank()) throw new DomainValidationException("Termo de pesquisa obrigatório");
        return userDao.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        if (id == null || id <= 0) throw new DomainValidationException("ID inválido");
        return userDao.findById(id);
    }

    public User getRequiredById(Long id) {
        return findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
    }

    public List<User> findByFamilyId(Long familyId) {
        if (familyId == null || familyId <= 0) throw new DomainValidationException("ID da família inválido");
        return userDao.findByFamilyId(familyId);
    }

    public User updateProfile(Long id, String username, String email) {
        User user = getRequiredById(id);
        String normalizedEmail = email == null ? null : email.trim().toLowerCase();
        userDao.findByEmail(normalizedEmail == null ? "" : normalizedEmail)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> { throw new BusinessConflictException("E-mail já está em uso por outro usuário"); });
        user.updateProfile(username, email);
        return userDao.update(user);
    }

    public User updateProfile(Long id, String externalId, String username, String name, String email, LocalDate birthDate, String nationality, Long familyId) {
        User user = getRequiredById(id);
        validateFamilyExists(familyId);
        if (email == null || !EMAIL.matcher(email.trim()).matches()) {
            throw new DomainValidationException("E-mail inválido");
        }
        String normalizedEmail = email.trim().toLowerCase();
        userDao.findByEmail(normalizedEmail)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> { throw new BusinessConflictException("E-mail já está em uso por outro usuário"); });

        user.setExternalId(externalId == null || externalId.isBlank() ? null : externalId.trim());
        user.updateProfile(username, name, normalizedEmail, birthDate, nationality, familyId);
        return userDao.update(user);
    }

    public String getProfileSummary(Long id) {
        User user = getRequiredById(id);
        return "Username: " + user.getDisplayName()
                + " | Login: " + user.getLogin()
                + " | E-mail: " + user.getEmail()
                + " | Perfil completo: " + (user.hasCompleteProfile() ? "sim" : "não");
    }

    public User update(User user) {
        if (user == null || user.getId() == null) throw new DomainValidationException("ID obrigatório para atualização");
        User existing = getRequiredById(user.getId());
        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            user.setPasswordHash(existing.getPasswordHash());
        }
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(existing.getCreatedAt());
        }
        validateForCreate(user);
        normalize(user);
        validateFamilyExists(user.getFamilyId());

        userDao.findByLogin(user.getLogin())
                .filter(other -> !other.getId().equals(user.getId()))
                .ifPresent(other -> { throw new BusinessConflictException("Login já utilizado por outro usuário"); });
        userDao.findByEmail(user.getEmail())
                .filter(other -> !other.getId().equals(user.getId()))
                .ifPresent(other -> { throw new BusinessConflictException("E-mail já utilizado por outro usuário"); });
        userDao.findByCpf(user.getCpf())
                .filter(other -> !other.getId().equals(user.getId()))
                .ifPresent(other -> { throw new BusinessConflictException("CPF já utilizado por outro usuário"); });

        user.setUpdatedAt(Instant.now());
        return userDao.update(user);
    }

    public boolean delete(Long id) {
        if (id == null || id <= 0) throw new DomainValidationException("ID inválido");
        getRequiredById(id);
        return ConnectionFactory.executeInTransaction(() -> userDao.deleteById(id));
    }

    private void validateFamilyExists(Long familyId) {
        if (familyId != null) {
            if (familyId <= 0) throw new DomainValidationException("ID da família inválido");
            if (familyDao.findById(familyId).isEmpty()) {
                throw new ResourceNotFoundException("Família não encontrada: " + familyId);
            }
        }
    }

    private void validateForCreate(User user) {
        if (user == null) throw new DomainValidationException("Usuário obrigatório");
        if (user.getUsername() == null || user.getUsername().trim().length() < 2) {
            throw new DomainValidationException("Username público deve ter ao menos 2 caracteres");
        }
        if (user.getLogin() == null || user.getLogin().trim().length() < 3) {
            throw new DomainValidationException("Login deve ter ao menos 3 caracteres");
        }
        if (user.getName() == null || user.getName().trim().length() < 2) {
            throw new DomainValidationException("Nome completo deve ter ao menos 2 caracteres");
        }
        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            throw new DomainValidationException("Hash de senha obrigatório");
        }
        if (user.getEmail() == null || !EMAIL.matcher(user.getEmail().trim()).matches()) {
            throw new DomainValidationException("E-mail inválido");
        }
        if (!user.isValidCpf()) {
            throw new DomainValidationException("CPF inválido (informe 11 dígitos numéricos válidos)");
        }
        if (user.getBirthDate() != null && user.getBirthDate().isAfter(LocalDate.now())) {
            throw new DomainValidationException("Data de nascimento não pode ser futura");
        }
    }

    private void normalize(User user) {
        user.setUsername(user.getUsername().trim());
        user.setLogin(user.getLogin().trim().toLowerCase());
        user.setName(user.getName().trim());
        user.setEmail(user.getEmail().trim().toLowerCase());
        user.setCpf(User.normalizeCpf(user.getCpf()));
        if (user.getExternalId() != null) {
            user.setExternalId(user.getExternalId().isBlank() ? null : user.getExternalId().trim());
        }
        if (user.getNationality() != null) {
            user.setNationality(user.getNationality().isBlank() ? null : user.getNationality().trim());
        }
    }
}
