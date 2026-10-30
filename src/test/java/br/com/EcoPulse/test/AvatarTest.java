package br.com.EcoPulse.test;

import br.com.EcoPulse.domain.Avatar;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AvatarTest {
    @Test
    void deveTestarMetodosDePersonalizacaoEEvolucaoSemChat() {
        Avatar avatar = new Avatar();
        avatar.setId(1L);
        avatar.setUserId(10L);
        avatar.setName("  Souly  ");
        avatar.setVisualConfig("{\"cor\":\"verde\"}");
        avatar.setPersonalityType("educativo");
        avatar.setLevel(1);
        avatar.setExperiencePoints(0L);

        assertEquals(1L, avatar.getId());
        assertEquals(10L, avatar.getUserId());
        assertEquals("Souly", avatar.getDisplayName());
        assertEquals("{\"cor\":\"verde\"}", avatar.getVisualConfig());
        assertEquals("educativo", avatar.getPersonalityType());
        assertFalse(avatar.isEvolved());
        assertEquals(1000L, avatar.getExperienceToNextLevel());

        avatar.addExperience(5000L);
        assertEquals(6, avatar.getLevel());
        assertTrue(avatar.isEvolved());
        assertEquals(1000L, avatar.getExperienceToNextLevel());
        assertNotNull(avatar.getUpdatedAt());
    }

    @Test
    void deveLancarErroParaExperienciaInvalida() {
        Avatar avatar = new Avatar();
        avatar.setLevel(1);
        avatar.setExperiencePoints(0L);
        assertThrows(RuntimeException.class, () -> avatar.addExperience(0));
        assertThrows(RuntimeException.class, () -> avatar.addExperience(-1));
    }
}
