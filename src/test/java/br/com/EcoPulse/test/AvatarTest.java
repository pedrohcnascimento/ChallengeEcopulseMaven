package br.com.EcoPulse.test;

import br.com.EcoPulse.domain.Avatar;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AvatarTest {
    @Test
    void deveTestarMetodosDeEvolucaoEInteracao() {
        Avatar avatar = new Avatar();
        avatar.setId(1L); avatar.setUserId(10L); avatar.setName("  Eco  "); avatar.setVisualConfig("{}");
        avatar.setPersonalityType("educativo"); avatar.setLevel(1); avatar.setExperiencePoints(0L);
        assertEquals(1L, avatar.getId()); assertEquals(10L, avatar.getUserId()); assertEquals("  Eco  ", avatar.getName());
        assertEquals("{}", avatar.getVisualConfig()); assertEquals("educativo", avatar.getPersonalityType());
        assertEquals(1, avatar.getLevel()); assertEquals(0L, avatar.getExperiencePoints());
        assertEquals("Eco", avatar.getDisplayName()); assertFalse(avatar.isEvolved()); assertEquals(1000L, avatar.getExperienceToNextLevel());

        avatar.addExperience(5000L);
        assertEquals(6, avatar.getLevel());
        assertTrue(avatar.isEvolved());
        assertEquals(1000L, avatar.getExperienceToNextLevel());
        avatar.registerInteraction();
        assertNotNull(avatar.getLastInteraction()); assertNotNull(avatar.getUpdatedAt());
    }

    @Test
    void deveLancarErroParaExperienciaInvalida() {
        Avatar avatar = new Avatar(); avatar.setLevel(1); avatar.setExperiencePoints(0L);
        assertThrows(IllegalArgumentException.class, () -> avatar.addExperience(0));
        assertThrows(IllegalArgumentException.class, () -> avatar.addExperience(-1));
    }
}
