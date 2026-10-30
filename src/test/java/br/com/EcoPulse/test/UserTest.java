package br.com.EcoPulse.test;

import br.com.EcoPulse.domain.User;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    void deveTestarMetodosDePerfilLoginDistintoEEncapsulamento() {
        User user = new User(
                1L, "ext-1", "  MariaEco  ", "maria.login", "$2a$10$hash",
                "Maria da Silva", LocalDate.of(2002, 5, 15), "01234567890",
                "Brasileira", "MARIA@EXAMPLE.COM", 10L, Instant.now(), Instant.now());

        assertEquals(1L, user.getId());
        assertEquals("ext-1", user.getExternalId());
        assertEquals("  MariaEco  ", user.getUsername());
        assertEquals("maria.login", user.getLogin());
        assertNotEquals(user.getUsername().trim(), user.getLogin());
        assertEquals("01234567890", user.getCpf());
        assertTrue(user.getCpf().startsWith("0"));
        assertEquals("MariaEco", user.getDisplayName());
        assertTrue(user.isValidEmail());
        assertTrue(user.isValidCpf());
        assertTrue(user.hasCompleteProfile());

        Instant updatedBefore = user.getUpdatedAt();
        user.updateProfile(" João Eco ", "João Silva", "JOAO@EXAMPLE.COM", LocalDate.of(2000, 1, 10), "Brasileiro", null);
        assertEquals("João Eco", user.getUsername());
        assertEquals("João Silva", user.getName());
        assertEquals("joao@example.com", user.getEmail());
        assertTrue(user.getUpdatedAt().compareTo(updatedBefore) >= 0);
    }

    @Test
    void deveTratarUsuarioSemNomeECpfInvalido() {
        User user = new User();
        assertEquals("Usuário sem nome", user.getDisplayName());
        assertFalse(user.hasCompleteProfile());
        assertFalse(user.isValidEmail());
        user.setCpf("11111111111");
        assertFalse(user.isValidCpf());
    }

    @Test
    void deveLancarErroAoAtualizarPerfilInvalido() {
        User user = new User();
        assertThrows(RuntimeException.class, () -> user.updateProfile("", "ok@example.com"));
        assertThrows(RuntimeException.class, () -> user.updateProfile("Nome", "email-invalido"));
    }
}
