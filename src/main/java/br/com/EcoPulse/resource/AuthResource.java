package br.com.EcoPulse.resource;

import br.com.EcoPulse.domain.User;
import br.com.EcoPulse.dto.AuthDtos;
import br.com.EcoPulse.dto.UserDtos;
import br.com.EcoPulse.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthResource {
    private final AuthService authService;

    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDtos.AuthResponse> register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        User created = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthDtos.AuthResponse("Usuário registrado com sucesso", UserDtos.UserDetailResponse.from(created)));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDtos.AuthResponse> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        User authenticated = authService.login(request.login(), request.password());
        return ResponseEntity.ok(
                new AuthDtos.AuthResponse("Autenticação realizada com sucesso", UserDtos.UserDetailResponse.from(authenticated)));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDtos.UserDetailResponse> me(
            @RequestHeader(name = "X-User-Id", required = false) Long userId) {
        User user = authService.getAuthenticatedProfile(userId);
        return ResponseEntity.ok(UserDtos.UserDetailResponse.from(user));
    }
}
