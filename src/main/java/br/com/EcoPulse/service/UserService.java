package br.com.EcoPulse.service;

import br.com.EcoPulse.domain.User;
import br.com.EcoPulse.repository.dao.UserDao;
import br.com.EcoPulse.exception.DomainValidationException;
import br.com.EcoPulse.exception.ResourceNotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/** Regras de negócio e orquestração do agregado User. */
public class UserService {
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private final UserDao userDao;
    public UserService() { this.userDao = new UserDao(); }
    public UserService(UserDao userDao) { this.userDao = userDao; }

    public User create(User user) {
        validate(user); user.setUsername(user.getUsername().trim()); user.setEmail(user.getEmail().trim().toLowerCase());
        Instant now = Instant.now(); user.setCreatedAt(now); user.setUpdatedAt(now); return userDao.create(user);
    }
    public List<User> getAll() { return userDao.findAll(); }
    public List<User> searchByUsername(String username) {
        if (username == null || username.isBlank()) throw new DomainValidationException("Termo de pesquisa obrigatório");
        return userDao.findByUsername(username);
    }
    public Optional<User> findById(Long id) { if (id == null || id <= 0) throw new DomainValidationException("ID inválido"); return userDao.findById(id); }
    public User updateProfile(Long id, String username, String email) {
        User user = findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
        user.updateProfile(username, email);
        return userDao.update(user);
    }
    public String getProfileSummary(Long id) {
        User user = findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
        return "Nome: " + user.getDisplayName() + " | E-mail: " + user.getEmail() + " | Perfil completo: " + (user.hasCompleteProfile() ? "sim" : "não");
    }
    public User update(User user) {
        if (user.getId() == null) throw new DomainValidationException("ID obrigatório para atualização");
        validate(user); user.setUsername(user.getUsername().trim()); user.setEmail(user.getEmail().trim().toLowerCase()); user.setUpdatedAt(Instant.now()); return userDao.update(user);
    }
    public boolean delete(Long id) { if (id == null || id <= 0) throw new DomainValidationException("ID inválido"); return userDao.deleteById(id); }
    private void validate(User user) {
        if (user == null) throw new DomainValidationException("Usuário obrigatório");
        if (user.getUsername() == null || user.getUsername().isBlank() || user.getUsername().trim().length() < 2) throw new DomainValidationException("Nome deve ter ao menos 2 caracteres");
        if (user.getEmail() == null || !EMAIL.matcher(user.getEmail().trim()).matches()) throw new DomainValidationException("E-mail inválido");
    }
}
