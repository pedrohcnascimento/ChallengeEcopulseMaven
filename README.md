# Challenge EcoPulse / SoulUp — API REST (4º Sprint DDD Java)

## Descrição

O **EcoPulse / SoulUp** é uma plataforma Java voltada ao incentivo de hábitos sustentáveis, missões ecológicas, evolução do avatar personalizável **Souly**, recompensas e engajamento em comunidades.

Nesta entrega do **4º Sprint de Domain Driven Design Using Java**, o sistema evoluiu do console para uma **API REST completa com Spring Boot 3 e JDBC**, consolidando todo o modelo de domínio na convenção relacional **`T_SOUL_`** e preparando o deploy containerizado via **Docker no Render**.

## Tecnologias

- **Java 21**
- **Spring Boot 3.3.5** (Web, Validation, Spring Security Crypto / BCrypt)
- **Springdoc OpenAPI / Swagger UI**
- **Oracle Database** (`ojdbc11`) + **H2 Database** (modo compatibilidade Oracle para testes automatizados e perfil `dev`)
- **JUnit 5 + MockMvc** para testes unitários e de integração
- **Docker** + **Render Blueprint (`render.yaml`)**

## Arquitetura do Projeto

O projeto preserva e expande a estrutura original `br.com.EcoPulse`, aplicando os padrões **DAO**, **BO/Service**, **Connection Factory**, **DTO**, **Resource REST** e **CorsFilter**:

```text
src/
├── main/java/br/com/EcoPulse/
│   ├── EcoPulseApplication.java # Ponto de entrada Spring Boot
│   ├── config/                  # ConnectionFactory (JDBC + Transações), CorsFilter e OpenApiConfig
│   ├── domain/                  # Camada Modelo consolidada (User, Family, Avatar, Mission, Reward, etc.)
│   ├── dto/                     # Contratos REST de entrada e saída (ocultando passwordHash e protegendo CPF)
│   ├── exception/               # Exceções de domínio/negócio e GlobalExceptionHandler REST
│   ├── repository/              # Repositórios e DAOs JDBC concretos (T_SOUL_*)
│   │   └── dao/
│   ├── resource/                # Camada Resource (@RestController /api/v1/*)
│   ├── security/                # Hashing BCrypt de senhas (PasswordHasher)
│   ├── service/                 # Camada BO / Service (validações, invariantes e regras transacionais)
│   ├── controller/              # Controladores de compatibilidade com modo console
│   └── interfaces/              # Interface interativa legada de console
└── test/java/br/com/EcoPulse/test/
    ├── UserTest.java
    ├── AvatarTest.java
    ├── MissionTest.java
    ├── UserCrudTest.java
    └── EcoPulseApiIntegrationTest.java
```

## Modelo Consolidado (`T_SOUL_`)

- **`User` (`T_SOUL_USUARIO`)**: diferencia `username` (nome público) de `login` (identificador único de autenticação), armazena `passwordHash` com BCrypt, `cpf` textual preservando zeros à esquerda, `birthDate`, `nationality`, `externalId` e vínculo opcional `familyId`.
- **`Family` (`T_SOUL_FAMILIA`)**: agrupa usuários e mantém `relationshipDescription`.
- **`Avatar` (`T_SOUL_AVATAR`)**: personalização (`visualConfig`, `personalityType`) e evolução por `level` e `experiencePoints` (sem `lastInteraction` nem chat nesta etapa).
- **`Mission` (`T_SOUL_DESAFIO`)**: unifica `Mission` e a antiga `Challenge`. Utiliza `status` único (`ATIVA` / `INATIVA`), `rules`, `startDate` obrigatória e `endDate` opcional. A premiação é feita exclusivamente via associação N:N com `Reward` (`T_SOUL_DESAFIO_REWARD`), exigindo ao menos uma recompensa previamente cadastrada na criação transacional.
- **`Reward` (`T_SOUL_REWARD`)**: catálogo independente de recompensas (`type`, `value`, `details`).
- **`UserMission` (`T_SOUL_USUARIO_DESAFIO`)** e **`UserReward` (`T_SOUL_USUARIO_REWARD`)**: participação com progresso (`0-100%`) e concessão automática transacional das recompensas ao concluir a missão.
- **`Activity` (`T_SOUL_ACTIVITY`)** e **`Streak` (`T_SOUL_STREAK`)**: registro de atividades sustentáveis com atualização transacional da sequência de dias consecutivos.
- **`Community` (`T_SOUL_COMUNIDADE`)**, **`UserCommunity` (`T_SOUL_USUARIO_COMUN`)** e **`MissionCommunity` (`T_SOUL_DESAFIO_COMUN`)**: comunidades com nome único, membros e missões vinculadas com `score` e `status`.
- **`Post` (`T_SOUL_POSTS`)** e **`PostCounter` (`T_SOUL_CONTADORES`)**: publicações com métricas 1:1 inicializadas na criação e atualizadas apenas por eventos válidos no servidor.
- **`ConversationHistory` (`T_SOUL_HISTORICO_CONVERSA`)**: estrutura persistida pertencente ao usuário, sem exposição de endpoints de chat/IA nesta fase.

## Configuração Segura do Banco de Dados

As credenciais nunca ficam hardcoded no código. Configure via variáveis de ambiente (veja `.env.example`):

| Variável | Descrição | Valor Padrão |
|---|---|---|
| `DB_URL` | URL JDBC do Oracle | `jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL` |
| `DB_USER` | Usuário Oracle | *(obrigatório em produção)* |
| `DB_PASSWORD` | Senha Oracle | *(obrigatório em produção)* |
| `DB_AUTO_INIT` | Criação automática das tabelas `T_SOUL_` ausentes | `true` |
| `PORT` | Porta HTTP do servidor | `8080` |

> **Nota de segurança:** caso credenciais antigas tenham sido versionadas em commits anteriores do repositório, recomenda-se realizar a rotação da senha junto ao banco Oracle da FIAP.

Para executar localmente em modo de desenvolvimento sem acesso à rede Oracle da FIAP, utilize o perfil `dev` (H2 em memória em modo Oracle):

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Documentação interativa **Swagger UI**: `http://localhost:8080/swagger-ui.html`

## Principais Endpoints REST (`/api/v1`)

- **Health Check:** `GET /api/v1/health`
- **Autenticação:** `POST /api/v1/auth/register`, `POST /api/v1/auth/login`, `GET /api/v1/auth/me` (header `X-User-Id`)
- **Usuários:** `GET /api/v1/users`, `POST /api/v1/users`, `GET /api/v1/users/{id}`, `GET /api/v1/users/{id}/profile` (restrito ao titular via `X-User-Id`), `PUT /api/v1/users/{id}`, `DELETE /api/v1/users/{id}`
- **Famílias:** `GET/POST /api/v1/families`, `GET/PUT/DELETE /api/v1/families/{id}`, `GET /api/v1/families/{id}/members`
- **Avatares:** `GET/POST /api/v1/avatars`, `GET /api/v1/avatars/user/{userId}`, `GET/PUT/DELETE /api/v1/avatars/{id}`, `PATCH /api/v1/avatars/{id}/experience`
- **Recompensas:** `GET/POST /api/v1/rewards`, `GET/PUT/DELETE /api/v1/rewards/{id}`
- **Missões:** `GET/POST /api/v1/missions`, `GET/PUT/DELETE /api/v1/missions/{id}`, `PATCH /api/v1/missions/{id}/status`, `GET /api/v1/missions/{id}/rewards`, `POST/DELETE /api/v1/missions/{id}/rewards/{rewardId}`
- **Participações:** `GET/POST /api/v1/participations`, `GET/DELETE /api/v1/participations/{id}`, `PATCH /api/v1/participations/{id}/progress`, `POST /api/v1/participations/{id}/complete`
- **Recompensas do Usuário:** `GET/POST /api/v1/user-rewards`, `GET/DELETE /api/v1/user-rewards/{id}`, `PATCH /api/v1/user-rewards/{id}/claim`
- **Atividades e Streaks:** `GET/POST /api/v1/activities`, `GET/DELETE /api/v1/activities/{id}`, `GET /api/v1/streaks`, `GET /api/v1/streaks/user/{userId}`
- **Comunidades:** `GET/POST /api/v1/communities`, `GET/PUT/DELETE /api/v1/communities/{id}`, `GET /api/v1/communities/{id}/members`, `POST/DELETE /api/v1/communities/{id}/members/{userId}`, `GET/POST /api/v1/communities/{id}/missions`, `PUT/DELETE /api/v1/communities/{id}/missions/{missionId}`
- **Publicações e Engajamento:** `GET/POST /api/v1/posts`, `GET/PUT/DELETE /api/v1/posts/{id}`, `GET /api/v1/posts/{id}/counters`, `POST /api/v1/posts/{id}/like`, `POST /api/v1/posts/{id}/comment`, `POST /api/v1/posts/{id}/share`

## Testes Automatizados

```bash
mvn clean test
```

## Deploy no Render com Docker

O projeto inclui `Dockerfile` multi-stage (Maven + JDK 21 -> JRE 21 Alpine) e `render.yaml` prontos para o Render:

1. Faça `git push` das alterações para o repositório no GitHub.
2. Acesse [dashboard.render.com](https://dashboard.render.com) -> **New +** -> **Blueprint** (ou **Web Service** selecionando **Docker**).
3. Conecte o repositório `ChallengeEcopulseMaven`.
4. Nas variáveis de ambiente (**Environment Variables**), preencha:
   - `DB_URL`: `jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL`
   - `DB_USER`: seu RM da FIAP
   - `DB_PASSWORD`: sua senha do Oracle FIAP
   - `DB_AUTO_INIT`: `true`
5. Finalize o deploy e valide o status em `https://<seu-servico>.onrender.com/api/v1/health` e `https://<seu-servico>.onrender.com/swagger-ui.html`.
