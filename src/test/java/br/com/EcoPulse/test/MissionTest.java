package br.com.EcoPulse.test;

import br.com.EcoPulse.domain.Mission;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MissionTest {
    @Test
    void deveTestarMetodosDeStatusEPontuacao() {
        Mission mission = new Mission();
        mission.setId(1L); mission.setTitle("  Reciclagem  "); mission.setDescription("Separe resíduos");
        mission.setType("ambiental"); mission.setRewardPoints(100); mission.setIsActive(true);
        assertEquals(1L, mission.getId()); assertEquals("  Reciclagem  ", mission.getTitle());
        assertEquals("Separe resíduos", mission.getDescription()); assertEquals("ambiental", mission.getType());
        assertEquals(100, mission.getRewardPoints()); assertTrue(mission.getIsActive());
        assertEquals("Reciclagem", mission.getDisplayTitle()); assertTrue(mission.isAvailable()); assertTrue(mission.awardsPoints());

        mission.deactivate(); assertFalse(mission.isAvailable());
        mission.activate(); assertTrue(mission.isAvailable());
        mission.updateDetails(" Compostagem ", "Nova descrição", 250);
        assertEquals("Compostagem", mission.getTitle()); assertEquals(250, mission.getRewardPoints());
        assertEquals("Nova descrição", mission.getDescription());
        mission.setRewardItemId(7L); assertEquals(7L, mission.getRewardItemId());
        assertNotNull(mission.getUpdatedAt());
    }

    @Test
    void deveIdentificarMissaoSemPontuacaoELancarErros() {
        Mission mission = new Mission(); mission.setRewardPoints(0); mission.setIsActive(false);
        assertFalse(mission.awardsPoints()); assertFalse(mission.isAvailable());
        assertThrows(IllegalArgumentException.class, () -> mission.updateDetails("", "desc", 10));
        assertThrows(IllegalArgumentException.class, () -> mission.updateDetails("Título", "desc", -1));
    }
}
