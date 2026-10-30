package br.com.EcoPulse.test;

import br.com.EcoPulse.domain.Mission;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import static org.junit.jupiter.api.Assertions.*;

class MissionTest {
    @Test
    void deveTestarStatusUnicoPeriodoOpcionalEElegibilidade() {
        Instant now = Instant.now();
        Mission mission = new Mission(
                1L,
                "  Reciclagem Doméstica  ",
                "Separe resíduos recicláveis",
                "AMBIENTAL",
                "Separar plástico, papel e vidro por 7 dias",
                now.minus(1, ChronoUnit.DAYS),
                null,
                Mission.STATUS_ATIVA,
                now,
                now);

        assertEquals("Reciclagem Doméstica", mission.getDisplayTitle());
        assertTrue(mission.isEligible(now));

        mission.deactivate();
        assertEquals(Mission.STATUS_INATIVA, mission.getStatus());
        assertFalse(mission.isEligible(now));

        mission.activate();
        assertTrue(mission.isEligible(now));

        // Fora do período futuro -> não elegível mesmo ATIVA
        mission.setStartDate(now.plus(2, ChronoUnit.DAYS));
        assertFalse(mission.isEligible(now));

        // Expirada -> não elegível mesmo ATIVA
        mission.setStartDate(now.minus(5, ChronoUnit.DAYS));
        mission.setEndDate(now.minus(1, ChronoUnit.DAYS));
        assertFalse(mission.isEligible(now));
    }

    @Test
    void deveValidarDatasEAtributosObrigatorios() {
        Instant now = Instant.now();
        Mission mission = new Mission();
        mission.setStatus(Mission.STATUS_ATIVA);
        assertThrows(RuntimeException.class, mission::validateInvariants);

        assertThrows(RuntimeException.class, () -> mission.updateDetails(
                "Compostagem",
                "Descrição",
                "AMBIENTAL",
                "Regras",
                now,
                now.minus(1, ChronoUnit.HOURS)));
    }
}
