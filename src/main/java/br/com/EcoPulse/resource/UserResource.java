package br.com.EcoPulse.resource;

import br.com.EcoPulse.domain.User;
import br.com.EcoPulse.dto.UserDtos;
import br.com.EcoPulse.exception.AuthenticationException;
import br.com.EcoPulse.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserResource {
    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDtos.UserDetailResponse> create(@Valid @RequestBody UserDtos.UserCreateRequest request) {
        User user = new User();
        user.setExternalId(request.externalId());
        user.setUsername(request.username());
        user.setLogin(request.login());
        user.setName(request.name());
        user.setBirthDate(request.birthDate());
        user.setCpf(request.cpf());
        user.setNationality(request.nationality());
        user.setEmail(request.email());
        user.setFamilyId(request.familyId());

        User created = userService.create(user, request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserDtos.UserDetailResponse.from(created));
    }

    @GetMapping
    public ResponseEntity<List<UserDtos.UserPublicResponse>> list(
            @RequestParam(name = "username", required = false) String username) {
        List<User> users = (username == null || username.isBlank())
                ? userService.getAll()
                : userService.searchByUsername(username);
        return ResponseEntity.ok(users.stream().map(UserDtos.UserPublicResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDtos.UserPublicResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(UserDtos.UserPublicResponse.from(userService.getRequiredById(id)));
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<UserDtos.UserDetailResponse> findPrivateProfile(
            @PathVariable Long id,
            @RequestHeader(name = "X-User-Id", required = false) Long requesterId) {
        if (requesterId == null || !requesterId.equals(id)) {
            throw new AuthenticationException("Acesso negado aos dados pessoais (CPF/data de nascimento) de outro usuário");
        }
        return ResponseEntity.ok(UserDtos.UserDetailResponse.from(userService.getRequiredById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDtos.UserPublicResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UserDtos.UserUpdateRequest request) {
        User updated = userService.updateProfile(
                id,
                request.externalId(),
                request.username(),
                request.name(),
                request.email(),
                request.birthDate(),
                request.nationality(),
                request.familyId());
        return ResponseEntity.ok(UserDtos.UserPublicResponse.from(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
