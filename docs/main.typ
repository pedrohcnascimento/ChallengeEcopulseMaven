#import "report-theme.typ": report-accent, report-theme

#show: report-theme.with(
  title: "Documentação Técnica — Challenge EcoPulse",
  author: "Equipe EcoPulse",
  rhythm: "report",
  running-header: true,
)

#page(margin: (top: 30%, x: 2.2cm), numbering: none, header: none)[
  #set par(first-line-indent: 0em)
  #align(center)[
    #text(size: 26pt, weight: "bold", fill: report-accent)[Documentação Técnica]
    #v(0.5em)
    #text(size: 20pt, weight: "bold")[Challenge EcoPulse]
    #v(0.8em)
    #text(size: 14pt, fill: luma(80))[Domain Driven Design using Java]
    #v(2em)
    #line(length: 40%, stroke: 0.5pt + luma(160))
    #v(2em)
    #text(size: 12pt)[
      Autoria: Equipe EcoPulse \
      Data: #datetime.today().display("[day]/[month]/[year]") \
      Versão: 1.0
    ]
  ]
]

#page(numbering: none, header: none)[
  #outline(title: [Sumário], indent: 1.5em)
]

#counter(page).update(1)

= 1. Objetivo e escopo

O Challenge EcoPulse é uma aplicação Java de console criada para apoiar uma experiência de incentivo a hábitos sustentáveis. O sistema organiza usuários, seus avatares e missões ambientais em um domínio orientado a objetos, com persistência em Oracle Database por meio de JDBC.

O propósito principal é permitir que um usuário seja cadastrado, tenha um avatar associado e participe de uma dinâmica de evolução baseada em experiência. O sistema também oferece o cadastro e a administração de missões, incluindo ativação, desativação, atualização e controle de pontuação.

O escopo desta entrega contempla o fluxo de console para usuários, avatares e missões; as regras de negócio dessas entidades; a conexão Oracle; a criação inicial do schema; os DAOs com operações CRUD; as exceções específicas; e os testes automatizados das regras de domínio. O projeto não implementa autenticação, autorização, interface web, aplicativo mobile, processamento assíncrono ou integração com serviços externos.

A solução está estruturada segundo uma separação de responsabilidades. As entidades concentram o comportamento do domínio, os services orquestram regras e casos de uso, os DAOs executam a persistência JDBC e a interface de console coleta entradas e apresenta resultados.

= 2. Funcionalidades implementadas

== 2.1 Gerenciamento de usuários

O módulo de usuários permite criar, listar, pesquisar por nome, consultar por identificador, atualizar perfil, exibir resumo e excluir registros. O `UserService` normaliza nome e e-mail, valida o formato mínimo do cadastro e delega as operações de persistência ao `UserDao`.

A entidade `User` possui regras para produzir o nome de exibição, verificar se o perfil está completo, validar o e-mail e atualizar os dados editáveis do perfil.

== 2.2 Gerenciamento de avatares

O módulo de avatares permite criar um avatar vinculado a um usuário existente, listar avatares, adicionar experiência, registrar interação e excluir registros. A criação valida a existência do usuário proprietário antes de executar o `INSERT`, evitando violação de chave estrangeira.

A entidade `Avatar` calcula evolução, experiência necessária para o próximo nível, nome de exibição e atualização da última interação. A experiência positiva pode elevar automaticamente o nível do avatar.

== 2.3 Gerenciamento de missões

O módulo de missões permite criar, listar todas, listar somente as ativas, atualizar detalhes, ativar ou desativar e excluir missões. A entidade `Mission` controla disponibilidade, pontuação, título de exibição, ativação, desativação e atualização dos dados editáveis.

== 2.4 Persistência e inicialização

A `ConnectionFactory` centraliza a conexão Oracle, carrega explicitamente o driver JDBC e cria as tabelas ausentes na primeira inicialização. As tabelas utilizam o prefixo obrigatório `T_CHLNG_`.

Os DAOs `UserDao`, `AvatarDao` e `MissionDao` são a fonte da lógica JDBC dos três agregados principais. Os repositories específicos permanecem como fachadas de compatibilidade e o `GenericRepository` está disponível para futuras entidades simples.

== 2.5 Tratamento de erros

A pasta `exception` contém exceções específicas para validação de domínio, recurso inexistente e falhas de persistência. A interface interativa captura essas falhas e apresenta mensagens orientadas ao usuário, evitando o encerramento abrupto em erros previsíveis.

Ao selecionar a opção `0` no menu principal, o sistema remove as linhas das tabelas na ordem correta das dependências, mas preserva o schema e as tabelas para a próxima execução.

#pagebreak()

= 3. Arquitetura da solução

A arquitetura usa camadas com responsabilidades definidas.

#table(
  columns: (3.2cm, 8.8cm),
  inset: 6pt,
  fill: (x, y) => if y == 0 { report-accent.lighten(70%) },
  [*Camada*], [*Responsabilidade*],
  [Domínio], [Entidades `User`, `Avatar`, `Mission` e demais classes de negócio.],
  [Service], [Validação, normalização, orquestração e aplicação das regras de uso.],
  [DAO], [Execução de Create, Read, Update e Delete com JDBC Oracle.],
  [Repository], [Fachadas específicas e suporte a repositório genérico.],
  [Controller], [Ponto de entrada das operações utilizadas pela interface.],
  [Interface], [Menus, leitura de entradas e apresentação de mensagens.],
  [Configuração], [Conexão, criação do schema e limpeza dos registros.],
  [Testes], [Testes JUnit das regras de domínio e teste manual do CRUD.],
)

= 4. Diagrama do banco de dados

O banco é composto por tabelas de usuários, atividades, avatares, missões, recompensas, desafios e associações. As chaves primárias são identificadores numéricos gerados pelo Oracle. As chaves estrangeiras preservam as relações entre usuário, avatar, missão e recompensa.

A tabela `T_CHLNG_USERS` é a principal referência dos registros de atividades, avatares, sequências, missões do usuário e recompensas do usuário. `T_CHLNG_MISSIONS` pode referenciar uma recompensa. As tabelas de associação registram participações e vínculos entre os elementos do domínio.

#figure(
  image("diagrama-er.png", width: 100%),
  caption: [Modelo entidade-relacionamento do schema Oracle.]
)

#pagebreak()

= 5. Diagrama de classes

O diagrama apresenta as entidades com seus atributos e regras de negócio, os services que as orquestram, os DAOs responsáveis pela persistência e a `ConnectionFactory` utilizada pelos DAOs. A dependência entre `AvatarService` e `UserDao` representa a validação do proprietário antes do cadastro de um avatar.

#figure(
  image("diagrama-classes.png", width: 100%),
  caption: [Diagrama de classes da estrutura atual da aplicação.]
)

#pagebreak()

= 6. Ferramentas e versões necessárias

A execução recomendada utiliza IntelliJ IDEA por sua integração com projetos Maven, mas o projeto também pode ser importado em Eclipse, NetBeans ou VS Code com suporte a Java e Maven.

#table(
  columns: (5cm, 7cm),
  inset: 6pt,
  fill: (x, y) => if y == 0 { report-accent.lighten(70%) },
  [*Ferramenta ou componente*], [*Versão ou requisito*],
  [Java Development Kit], [JDK 21 ou superior.],
  [Maven], [Versão compatível com Java 21 e acesso ao Maven Central.],
  [IntelliJ IDEA], [Versão atual com suporte a Maven; Eclipse, NetBeans e VS Code também são alternativas.],
  [Oracle Database], [Instância ativa com serviço `XEPDB1` na porta `1521`.],
  [Driver JDBC], [`com.oracle.database.jdbc:ojdbc8:21.1.0.0`.],
  [JUnit], [JUnit Jupiter 5.10.2, executado pelo Maven Surefire.],
)

= 7. Procedimentos para importar e executar

== 7.1 Importação no IntelliJ IDEA

Abra a pasta raiz do projeto, que contém `pom.xml`. Não abra somente a pasta `src`. No painel Maven, selecione *Reload All Maven Projects*. O IntelliJ deverá reconhecer automaticamente `src/main/java` como código de produção e `src/test/java` como código de teste.

Confirme se a dependência `ojdbc8` foi resolvida no classpath. Se o projeto for aberto em uma IDE diferente, importe-o como um projeto Maven existente a partir do arquivo `pom.xml`.

== 7.2 Configuração Oracle

Antes da execução do menu ou do teste manual, inicie o Oracle Database e confirme a disponibilidade do serviço `XEPDB1`. A configuração está em `ConnectionFactory.java`:

```text
URL: jdbc:oracle:thin:@localhost:1521/XEPDB1
Usuário: ecopulse
Senha: ecopulse123
```

Na primeira conexão, a aplicação tenta criar as tabelas ausentes. O usuário Oracle precisa possuir permissões para criar e manipular as tabelas do schema.

== 7.3 Compilação

Na raiz do projeto, execute:

```bash
mvn clean compile
```

== 7.4 Execução do menu interativo

O menu executável está em:

```text
src/main/java/br/com/EcoPulse/interfaces/Exibition.java
```

Sua classe principal é `br.com.EcoPulse.interfaces.Exibition`. Para iniciar com Maven:

```bash
mvn compile exec:java \
  -Dexec.mainClass=br.com.EcoPulse.interfaces.Exibition
```

Também é possível abrir `Exibition.java` na IDE e executar o função `main`. O menu principal oferece gerenciamento de usuários, avatares, missões e a opção de saída.

== 7.5 Execução dos testes automatizados

Os testes automatizados estão em `src/test/java/br/com/EcoPulse/test`. Eles verificam regras de negócio, encapsulamento, estados, evolução, validações e lançamento de exceções. Não precisam de Oracle porque não executam operações JDBC.

Execute a suíte completa com:

```bash
mvn clean test
```

Para executar uma classe individual:

```bash
mvn -Dtest=UserTest test
mvn -Dtest=AvatarTest test
mvn -Dtest=MissionTest test
```

Os relatórios são gerados em `target/surefire-reports/`.

== 7.6 Execução do teste manual de CRUD

O teste manual exigido na entrega está em:

```text
src/test/java/br/com/EcoPulse/test/UserCrudTest.java
```

Ele possui `main` e simula criação, leitura, atualização e exclusão de um usuário. Como está em `src/test/java`, execute com o classpath de testes:

```bash
mvn test-compile exec:java \
  -Dexec.mainClass=br.com.EcoPulse.test.UserCrudTest \
  -Dexec.classpathScope=test
```

Esse teste exige Oracle ativo. A saída esperada confirma `CREATE`, `READ`, `UPDATE`, `DELETE` e a mensagem de sucesso do CRUD.

= 8. Limites e observações

A aplicação é uma solução de console para fins acadêmicos e de demonstração de arquitetura Java. Ela não substitui um sistema de produção com autenticação, controle de acesso, gestão segura de segredos, migrações versionadas, observabilidade ou API externa.

As credenciais Oracle estão inseridas em código porque esse é um requisito da entrega. Em um ambiente real, elas deveriam ser obtidas por variáveis de ambiente ou um gerenciador de segredos. A limpeza automática dos registros ocorre somente quando a opção `0` é escolhida no menu principal; ela não deve ser acionada acidentalmente em um banco com dados que precisem ser preservados.

= 9. Referências

[1] Maven Project, documentação oficial de build e gerenciamento de dependências: https://maven.apache.org/ "Apache Maven Project"

[2] Oracle, documentação do JDBC: https://docs.oracle.com/en/database/oracle/oracle-database/21/jjdbc/ "Oracle Database JDBC Developer's Guide"

[3] JUnit, documentação oficial do JUnit 5: https://junit.org/junit5/docs/current/user-guide/ "JUnit 5 User Guide"
