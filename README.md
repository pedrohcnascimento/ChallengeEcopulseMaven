# Challenge EcoPulse

## Descrição

O **EcoPulse** é uma aplicação Java de console voltada ao incentivo de hábitos sustentáveis. O projeto foi desenvolvido para o 3º Sprint de **Domain Driven Design using Java** e organiza o domínio em entidades, serviços, DAOs, repositories, controllers e uma interface interativa.

A aplicação permite cadastrar e consultar usuários, criar avatares vinculados a usuários, evoluir avatares com pontos de experiência, registrar interações e gerenciar missões ambientais. A persistência é realizada em um banco Oracle por JDBC.

## Tecnologias

- Java 21 ou superior.
- Maven.
- Oracle Database.
- Oracle JDBC `ojdbc8` versão `21.1.0.0`.
- JUnit 5 para testes automatizados.

## Estrutura do projeto

O código de produção está em `src/main/java` e os testes estão em `src/test/java`, seguindo a convenção oficial do Maven.

```text
src/
├── main/java/br/com/EcoPulse/
│   ├── config/          # ConnectionFactory e inicialização do schema
│   ├── controller/      # Entrada das operações da aplicação
│   ├── domain/          # Entidades User, Avatar, Mission e demais entidades
│   ├── exception/       # Exceções específicas da aplicação
│   ├── interfaces/      # Menus e aplicação de console
│   ├── repository/      # Repositories específicos e GenericRepository
│   │   └── dao/         # UserDao, AvatarDao e MissionDao
│   └── service/         # Regras de negócio
└── test/java/br/com/EcoPulse/test/
    ├── UserCrudTest.java  # Teste manual com método main
    ├── UserTest.java      # Testes automatizados de User
    ├── AvatarTest.java    # Testes automatizados de Avatar
    └── MissionTest.java   # Testes automatizados de Mission
```

## Banco de dados Oracle

A conexão está centralizada em:

```text
src/main/java/br/com/EcoPulse/config/ConnectionFactory.java
```

Configuração padrão:

```text
URL:      jdbc:oracle:thin:@localhost:1521/XEPDB1
Usuário:  ecopulse
Senha:    ecopulse123
```

Antes de executar a aplicação ou o teste manual, o Oracle Database deve estar ativo, o listener deve estar escutando na porta `1521` e o serviço `XEPDB1` deve existir. A dependência obrigatória está no `pom.xml`:

```xml
<dependency>
    <groupId>com.oracle.database.jdbc</groupId>
    <artifactId>ojdbc8</artifactId>
    <version>21.1.0.0</version>
</dependency>
```

Na primeira execução, a `ConnectionFactory` cria as tabelas com o prefixo obrigatório `T_CHLNG_`, incluindo `T_CHLNG_USERS`, `T_CHLNG_AVATARS`, `T_CHLNG_MISSIONS` e as demais tabelas do domínio.

## Preparação no IntelliJ IDEA

Abra no IntelliJ a pasta raiz que contém o arquivo `pom.xml`. Não abra somente a pasta `src`.

Depois:

1. Abra a janela **Maven**.
2. Clique em **Reload All Maven Projects**.
3. Confirme que a dependência `ojdbc8-21.1.0.0.jar` aparece no classpath.
4. Confirme que `src/main/java` está marcado como **Sources Root**.
5. Confirme que `src/test/java` está marcado como **Test Sources Root**.
6. Use uma configuração de execução com o módulo Maven do projeto.

Se o classpath mostrar apenas `target/classes`, mas não mostrar o arquivo `ojdbc8-21.1.0.0.jar`, recarregue o Maven antes de executar.

## Compilação

Na raiz do projeto, execute:

```bash
mvn clean compile
```

Esse comando compila todas as classes de produção em `src/main/java`.

## Testes automatizados

Os testes automatizados estão em:

```text
src/test/java/br/com/EcoPulse/test/
```

### Executar todos os testes

```bash
mvn clean test
```

A suíte contém:

| Classe | Cobertura |
|---|---|
| `UserTest` | Getters, setters, validação de e-mail, perfil completo, nome de exibição, atualização de perfil e erros de validação. |
| `AvatarTest` | Getters, setters, nome de exibição, evolução, cálculo de experiência, interação e erro para experiência inválida. |
| `MissionTest` | Getters, setters, disponibilidade, pontuação, ativação, desativação, atualização e erros de validação. |

Esses testes exercitam as regras de domínio e não precisam de uma conexão Oracle, pois não executam persistência.

O relatório do Surefire é gerado em:

```text
target/surefire-reports/
```

### Executar somente uma classe de teste

```bash
mvn -Dtest=UserTest test
mvn -Dtest=AvatarTest test
mvn -Dtest=MissionTest test
```

## Teste manual do CRUD

O teste manual exigido pelo enunciado está em:

```text
src/test/java/br/com/EcoPulse/test/UserCrudTest.java
```

Ele possui um método `main` que simula:

1. Create de usuário.
2. Read por ID.
3. Update de usuário.
4. Delete de usuário.

Como ele está em `src/test/java`, execute com o classpath de testes:

```bash
mvn test-compile exec:java \
  -Dexec.mainClass=br.com.EcoPulse.test.UserCrudTest \
  -Dexec.classpathScope=test
```

Esse comando exige o Oracle Database ativo e configurado. A saída esperada é semelhante a:

```text
CREATE: 1
READ: OK
UPDATE: OK
DELETE: OK
CRUD executado com sucesso.
```

Também é possível abrir `UserCrudTest.java` no IntelliJ e executar o método `main` diretamente. Nesse caso, a configuração de execução precisa incluir as dependências Maven, principalmente o `ojdbc8`.

## Menu interativo executável

A classe executável do menu está em:

```text
src/main/java/br/com/EcoPulse/interfaces/Exibition.java
```

O nome da classe principal é:

```text
br.com.EcoPulse.interfaces.Exibition
```

Para iniciar pelo Maven:

```bash
mvn compile exec:java \
  -Dexec.mainClass=br.com.EcoPulse.interfaces.Exibition
```

Para iniciar pelo IntelliJ, abra `Exibition.java` e execute o método `main` pelo botão de execução ao lado da declaração da classe.

### Funcionalidades do menu

O menu principal disponibiliza:

```text
1. Gerenciar Usuários
2. Gerenciar Avatar
3. Ver Missões
0. Sair
```

Em **Gerenciar Usuários**:

- Criar usuário.
- Listar usuários.
- Pesquisar por nome.
- Consultar por ID.
- Atualizar perfil.
- Exibir resumo do perfil.
- Excluir usuário.

Em **Gerenciar Avatar**:

- Criar avatar vinculado a um usuário existente.
- Listar avatares.
- Adicionar experiência.
- Registrar interação.
- Excluir avatar.

Em **Ver Missões**:

- Criar missão.
- Listar todas as missões.
- Listar somente as missões ativas.
- Atualizar detalhes.
- Ativar ou desativar missão.
- Excluir missão.

Ao selecionar `0. Sair` no menu principal, as linhas são removidas das tabelas na ordem correta das chaves estrangeiras. As tabelas e o schema permanecem no banco para a próxima execução.

## Exceções

As exceções específicas ficam em:

```text
src/main/java/br/com/EcoPulse/exception/
```

- `DomainValidationException`: dados inválidos e regras de domínio.
- `ResourceNotFoundException`: entidade não encontrada.
- `PersistenceException`: falha de conexão ou operação JDBC.
- `EcoPulseException`: exceção base das falhas da aplicação.

O menu captura essas falhas e apresenta mensagens ao usuário sem encerrar a aplicação em situações de validação ou recurso inexistente.
