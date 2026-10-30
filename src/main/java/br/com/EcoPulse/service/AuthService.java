package br.com.EcoPulse.service;

import br.com.EcoPulse.domain.User;
import br.com.EcoPulse.dto.AuthDtos;
import br.com.EcoPulse.exception.AuthenticationException;
import br.com.EcoPulse.exception.DomainValidationException;
import br.com.EcoPulse.repository.dao.UserDao;
import br.com.EcoPulse.security.PasswordHasher;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final UserDao userDao;
    private final PasswordHasher passwordHasher;

    public AuthService(UserService userService, UserDao userDao, PasswordHasher passwordHasher) {
        this.userService = userService;
        this.userDao = userDao;
        this.passwordHasher = passwordHasher;
    }

    public User register(AuthDtos.RegisterRequest req) {
        User user = new User();
        user.setExternalId(req.externalId());
        user.setUsername(req.username());
        user.setLogin(req.login());
        user.setName(req.name());
        user.setBirthDate(req.birthDate());
        user.setCpf(req.cpf());
        user.setNationality(req.nationality());
        user.setEmail(req.email());
        user.setFamilyId(req.familyId());
        return userService.create(user, req.password());
    }

    public User login(String login, String password) {
        if (login == null || login.isBlank() || password == null || password.isBlank()) {
            throw new DomainValidationException("Login e senha são obrigatórios");
        }
        User user = userDao.findByLogin(login.trim())
                .orElseThrow(() -> new AuthenticationException("Credenciais inválidas"));
        if (!passwordHasher.matches(password, user.getPasswordHash())) {
            throw new AuthenticationException("Credenciais inválidas");
        }
        return user;
    }

    public User getAuthenticatedProfile(Long userId) {
        if (userId == null || userId <= 0) {
            throw new AuthenticationException("Identificação do usuário autenticado não informada");
        }
        return userService.getRequiredById(userId);
    }
}
