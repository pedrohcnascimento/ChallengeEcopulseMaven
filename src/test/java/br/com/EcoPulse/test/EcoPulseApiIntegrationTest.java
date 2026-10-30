package br.com.EcoPulse.test;

import br.com.EcoPulse.config.ConnectionFactory;
import br.com.EcoPulse.domain.ConversationHistory;
import br.com.EcoPulse.domain.Mission;
import br.com.EcoPulse.repository.dao.ConversationHistoryDao;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EcoPulseApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConversationHistoryDao conversationHistoryDao;

    @BeforeEach
    void setUp() {
        ConnectionFactory.initializeDatabase();
        ConnectionFactory.clearData();
    }

    @Test
    void deveResponderHealthCheckECorsPreflight() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));

        mockMvc.perform(options("/api/v1/missions").header("Origin", "http://localhost:5173"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().exists("Access-Control-Allow-Methods"));
    }

    @Test
    void deveRegistrarAutenticarProtegerDadosSensiveisEAssociarFamilia() throws Exception {
        MvcResult familyRes = mockMvc.perform(post("/api/v1/families")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Família Verde",
                                "relationshipDescription", "Pais e filhos"))))
                .andExpect(status().isCreated())
                .andReturn();
        long familyId = readId(familyRes);

        Map<String, Object> registerPayload = Map.of(
                "externalId", "ext-100",
                "username", "EcoHero",
                "login", "pedro.eco",
                "password", "Segredo123!",
                "name", "Pedro Nascimento",
                "birthDate", "2003-04-12",
                "cpf", "01234567890",
                "nationality", "Brasileira",
                "email", "pedro@ecopulse.com",
                "familyId", familyId
        );

        MvcResult regRes = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerPayload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.username").value("EcoHero"))
                .andExpect(jsonPath("$.user.login").value("pedro.eco"))
                .andExpect(jsonPath("$.user.cpf").value("01234567890"))
                .andExpect(jsonPath("$.user.passwordHash").doesNotExist())
                .andReturn();
        long userId = objectMapper.readTree(regRes.getResponse().getContentAsString()).get("user").get("id").asLong();

        // Duplicidade de login retorna 409 Conflict
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerPayload)))
                .andExpect(status().isConflict());

        // Login válido
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("login", "pedro.eco", "password", "Segredo123!"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.passwordHash").doesNotExist());

        // Senha incorreta -> 401
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("login", "pedro.eco", "password", "errada"))))
                .andExpect(status().isUnauthorized());

        // Consulta pública de usuários não expõe CPF nem data de nascimento
        mockMvc.perform(get("/api/v1/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("EcoHero"))
                .andExpect(jsonPath("$.cpf").doesNotExist())
                .andExpect(jsonPath("$.birthDate").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        // Consulta privada de terceiros bloqueada (401)
        mockMvc.perform(get("/api/v1/users/" + userId + "/profile").header("X-User-Id", userId + 999))
                .andExpect(status().isUnauthorized());

        // Membros da família
        mockMvc.perform(get("/api/v1/families/" + familyId + "/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userId));
    }

    @Test
    void deveExigirRecompensaPreviaReverterMissaoInvalidaEConcluirMissaoConcedendoRecompensas() throws Exception {
        long userId = createSampleUser("ana.login", "09876543210", "ana@ecopulse.com");

        // 1. Rejeita missão sem recompensas (400 Bad Request)
        mockMvc.perform(post("/api/v1/missions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", "Missão Sem Reward",
                                "type", "RECICLAGEM",
                                "startDate", Instant.now().minus(1, ChronoUnit.HOURS).toString(),
                                "rewardIds", List.of()))))
                .andExpect(status().isBadRequest());

        // 2. Cria uma recompensa válida
        MvcResult r1 = mockMvc.perform(post("/api/v1/rewards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Selo Guardião Verde",
                                "description", "Medalha sustentável",
                                "type", "MEDALHA",
                                "value", 150,
                                "details", "{\"raridade\":\"OURO\"}"))))
                .andExpect(status().isCreated())
                .andReturn();
        long reward1Id = readId(r1);

        // 3. Se uma das recompensas não existir (ex: 99999), reverte toda a criação da missão!
        mockMvc.perform(post("/api/v1/missions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", "Missão Que Deve Reverter",
                                "type", "RECICLAGEM",
                                "startDate", Instant.now().minus(1, ChronoUnit.HOURS).toString(),
                                "rewardIds", List.of(reward1Id, 99999L)))))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v1/missions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        // 4. Cria segunda recompensa e cria missão com ambas
        MvcResult r2 = mockMvc.perform(post("/api/v1/rewards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Pacote XP Eco",
                                "description", "Bônus de pontos",
                                "type", "PONTOS",
                                "value", 300))))
                .andExpect(status().isCreated())
                .andReturn();
        long reward2Id = readId(r2);

        MvcResult missionRes = mockMvc.perform(post("/api/v1/missions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", "Desafio Zero Plástico",
                                "description", "Reduzir descartáveis na semana",
                                "type", "CONSUMO_CONSCIENTE",
                                "rules", "Usar ecobag e garrafa reutilizável",
                                "startDate", Instant.now().minus(1, ChronoUnit.HOURS).toString(),
                                "status", Mission.STATUS_ATIVA,
                                "rewardIds", List.of(reward1Id, reward2Id)))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rewards.length()").value(2))
                .andReturn();
        long missionId = readId(missionRes);

        // Remove 1 recompensa -> permitido pois restará 1; tentar remover a última -> bloqueado (400)!
        mockMvc.perform(delete("/api/v1/missions/" + missionId + "/rewards/" + reward2Id))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/v1/missions/" + missionId + "/rewards/" + reward1Id))
                .andExpect(status().isBadRequest());

        // Readiciona a segunda recompensa
        mockMvc.perform(post("/api/v1/missions/" + missionId + "/rewards/" + reward2Id))
                .andExpect(status().isCreated());

        // 5. Atribui missão ao usuário, atualiza progresso e conclui -> concede as 2 recompensas!
        MvcResult partRes = mockMvc.perform(post("/api/v1/participations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("userId", userId, "missionId", missionId))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"))
                .andReturn();
        long participationId = readId(partRes);

        mockMvc.perform(patch("/api/v1/participations/" + participationId + "/progress")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("progress", 50))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progress").value(50));

        mockMvc.perform(post("/api/v1/participations/" + participationId + "/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONCLUIDA"))
                .andExpect(jsonPath("$.progress").value(100));

        MvcResult userRewardsRes = mockMvc.perform(get("/api/v1/user-rewards?userId=" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andReturn();
        long firstUserRewardId = objectMapper.readTree(userRewardsRes.getResponse().getContentAsString()).get(0).get("id").asLong();

        mockMvc.perform(patch("/api/v1/user-rewards/" + firstUserRewardId + "/claim"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isClaimed").value(true));
    }

    @Test
    void deveEvoluirAvatarAtualizarStreakGerenciarComunidadeEPublicacoes() throws Exception {
        long userId = createSampleUser("lucas.eco", "12345678901", "lucas@ecopulse.com");

        // Avatar personalização e evolução
        MvcResult avatarRes = mockMvc.perform(post("/api/v1/avatars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "userId", userId,
                                "name", "Souly Guardião",
                                "visualConfig", "{\"folha\":\"dourada\"}",
                                "personalityType", "MOTIVADOR"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.level").value(1))
                .andReturn();
        long avatarId = readId(avatarRes);

        mockMvc.perform(patch("/api/v1/avatars/" + avatarId + "/experience")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("points", 4500))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.level").value(5))
                .andExpect(jsonPath("$.evolved").value(true));

        // Activity + Streak em dias consecutivos
        Instant yesterday = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant today = Instant.now();

        mockMvc.perform(post("/api/v1/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "userId", userId,
                                "type", "COMPOSTAGEM",
                                "timestamp", yesterday.toString(),
                                "metadata", "{\"kg\":1.2}"))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "userId", userId,
                                "type", "COMPOSTAGEM",
                                "timestamp", today.toString(),
                                "metadata", "{\"kg\":0.8}"))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/streaks/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].currentCount").value(2))
                .andExpect(jsonPath("$[0].highestCount").value(2));

        // Comunidade + membros + missão associada
        MvcResult commRes = mockMvc.perform(post("/api/v1/communities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Bairro Sustentável",
                                "description", "Mutirões ecológicos locais",
                                "communityType", "BAIRRO"))))
                .andExpect(status().isCreated())
                .andReturn();
        long communityId = readId(commRes);

        mockMvc.perform(post("/api/v1/communities/" + communityId + "/members/" + userId))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/v1/communities/" + communityId + "/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        // Post + PostCounter (1:1 inicializado e atualizado por eventos)
        MvcResult postRes = mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "userId", userId,
                                "content", "Comecei minha composteira doméstica hoje!",
                                "status", "PUBLICADO"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.counters.likeCount").value(0))
                .andReturn();
        long postId = readId(postRes);

        mockMvc.perform(post("/api/v1/posts/" + postId + "/like"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likeCount").value(1));
        mockMvc.perform(post("/api/v1/posts/" + postId + "/comment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentCount").value(1));
        mockMvc.perform(post("/api/v1/posts/" + postId + "/share"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shareCount").value(1));

        // ConversationHistory persistida no banco (sem endpoint conversacional)
        ConversationHistory savedHistory = conversationHistoryDao.create(
                new ConversationHistory(null, userId, "Registro estrutural inicial", Instant.now(), "SYSTEM"));
        assertNotNull(savedHistory.getId());
        assertEquals(1, conversationHistoryDao.findByUserId(userId).size());
    }

    private long createSampleUser(String login, String cpf, String email) throws Exception {
        MvcResult res = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "User_" + login,
                                "login", login,
                                "password", "SenhaForte123",
                                "name", "Usuário Teste",
                                "cpf", cpf,
                                "email", email))))
                .andExpect(status().isCreated())
                .andReturn();
        return readId(res);
    }

    private long readId(MvcResult result) throws Exception {
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("id").asLong();
    }
}
