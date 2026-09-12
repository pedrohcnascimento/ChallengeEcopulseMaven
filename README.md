# Challenge EcoPulse

Implementação do 3º Sprint de **Domain Driven Design using Java**, com entidades de domínio, regras de negócio, conexão JDBC e CRUD completo de usuários.

## Estrutura

- `domain`: entidades do domínio com atributos privados, construtores, getters e setters.
- `service/UserService`: regras de negócio, validação, normalização de dados e orquestração do agregado `User`.
- `service/UserDao`: classe concreta de persistência JDBC; não depende de interface DAO.
- `repository`: repositórios legados mantidos para compatibilidade, sem participação no CRUD JDBC atual.
- `config/ConnectionFactory`: fábrica de conexões e inicialização de todo o schema.
- `controller`: ponto de entrada das operações para a interface.
- `test/UserCrudTest`: classe com `main` que executa e valida Create, Read, Update e Delete.

## Métodos específicos de usuário

A entidade `User` possui `getDisplayName`, `hasCompleteProfile`, `isValidEmail` e `updateProfile`. O `UserService` complementa essas regras com pesquisa por nome, atualização de perfil e geração de resumo do perfil. Essas operações estão disponíveis no menu **Gerenciar Usuários**: criar, listar, pesquisar por nome, consultar por ID, atualizar perfil, exibir resumo e excluir.

## Banco de dados

O projeto usa Oracle Database via JDBC com a dependência `ojdbc8` versão `21.1.0.0`. A conexão padrão utiliza `jdbc:oracle:thin:@localhost:1521/XEPDB1`, usuário `ecopulse` e senha `ecopulse123`, definidos na `ConnectionFactory` conforme solicitado no enunciado. Todas as tabelas seguem o padrão obrigatório `T_CHLNG_<NOME_DA_TABELA>`:

`T_CHLNG_USERS`, `T_CHLNG_ACTIVITIES`, `T_CHLNG_AVATARS`, `T_CHLNG_CHALLENGES`, `T_CHLNG_COMMUNITIES`, `T_CHLNG_GROUP_CHALLENGES`, `T_CHLNG_MISSIONS`, `T_CHLNG_REWARDS`, `T_CHLNG_STREAKS`, `T_CHLNG_USER_MISSIONS` e `T_CHLNG_USER_REWARDS`.

A `ConnectionFactory` cria automaticamente as tabelas Oracle e suas chaves estrangeiras na primeira execução, verificando previamente a existência de cada tabela no dicionário `USER_TABLES`.

## Como executar

É necessário ter Java 21 e Maven instalados. Na raiz do projeto:

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass=br.com.EcoPulse.test.UserCrudTest
```

No IntelliJ IDEA, abra o projeto pela raiz que contém o `pom.xml` e selecione **Add as Maven Project** ou **Reload All Maven Projects**. A configuração de execução precisa conter a biblioteca Maven `com.oracle.database.jdbc:ojdbc8:21.1.0.0` no classpath. Se o comando de execução mostrar somente `out/production/Challenge`, sem o arquivo `ojdbc8-21.1.0.0.jar`, o driver não foi incluído e ocorrerá `No suitable driver found`.

Também é possível configurar manualmente em **File > Project Structure > Libraries > + > From Maven**, informando `com.oracle.database.jdbc:ojdbc8:21.1.0.0`, e adicionando a biblioteca ao módulo `Challenge`.

A saída esperada, com uma instância Oracle acessível, confirma `CREATE`, `READ`, `UPDATE`, `DELETE` e `CRUD executado com sucesso.`

Para iniciar o menu interativo:

```bash
mvn exec:java -Dexec.mainClass=br.com.EcoPulse.interfaces.Exibition
```
