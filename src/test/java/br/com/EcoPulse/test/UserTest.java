package br.com.EcoPulse.test;

import br.com.EcoPulse.domain.User;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    void deveTestarMetodosDePerfilEEncapsulamento() {
        User user = new User(1L, "ext-1", "  Maria Silva  ", "MARIA@EXAMPLE.COM", Instant.now(), Instant.now());
        assertEquals(1L, user.getId());
        assertEquals("ext-1", user.getExternalId());
        assertEquals("  Maria Silva  ", user.getUsername());
        assertEquals("MARIA@EXAMPLE.COM", user.getEmail());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        assertEquals("Maria Silva", user.getDisplayName());
        assertTrue(user.isValidEmail());
        assertTrue(user.hasCompleteProfile());

        Instant updatedBefore = user.getUpdatedAt();
        user.updateProfile(" João Silva ", "JOAO@EXAMPLE.COM");
        assertEquals("João Silva", user.getUsername());
        assertEquals("joao@example.com", user.getEmail());
        assertTrue(user.getUpdatedAt().compareTo(updatedBefore) >= 0);

        user.setId(2L); user.setExternalId("ext-2"); user.setUsername("Ana"); user.setEmail("ana@example.com");
        assertEquals(2L, user.getId()); assertEquals("ext-2", user.getExternalId());
        assertEquals("Ana", user.getUsername()); assertEquals("ana@example.com", user.getEmail());
    }

    @Test
    void deveTratarUsuarioSemNome() {
        User user = new User();
        assertEquals("Usuário sem nome", user.getDisplayName());
        assertFalse(user.hasCompleteProfile());
        assertFalse(user.isValidEmail());
    }

    @Test
    void deveLancarErroAoAtualizarPerfilInvalido() {
        User user = new User();
        assertThrows(IllegalArgumentException.class, () -> user.updateProfile("", "ok@example.com"));
        assertThrows(IllegalArgumentException.class, () -> user.updateProfile("Nome", "email-invalido"));
    }
}
