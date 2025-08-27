# Board de Tarefas - DIO

Este projeto foi criado acompanhando as aulas do [Digital Innovation One](https://dio.me/) para praticar a integração de aplicações Java com bancos de dados relacionais usando SQL.

## Resumo do Projeto

Este projeto consiste em um Board de Tarefas desenvolvido em Java, seguindo as orientações das aulas da Digital Innovation One (DIO). O objetivo principal é demonstrar na prática como integrar uma aplicação Java a um banco de dados relacional utilizando SQL puro e JDBC, sem o uso de frameworks como Spring Boot. O sistema permite o gerenciamento básico de tarefas, explorando conceitos como conexões e operações SQL, organização do código em classes, utilização de ferramentas modernas como JDK 17, Maven, Gradle e Lombok, além do uso do MySQL para armazenamento persistente dos dados. Trata-se de um projeto educacional focado no aprendizado de integração entre Java e banco de dados.

## Tecnologias e ferramentas
- **JDK 17**
- **Maven**: ferramenta de gerenciamento de dependências utilizada para compilar, empacotar e automatizar o ciclo de vida do projeto
- **Gradle**: build flexível e performática usada para compilar, empacotar o projeto.
- **Lombok**: biblioteca que reduz código boilerplate ao gerar automaticamente getters, setters, construtores e outros métodos por meio de anotações.
- **MySQL**: sistema de banco de dados relacional para armazenar e gerenciar as informações das tarefas de forma estruturada e persistente no projeto.

## Como executar este projeto
1. Certifique-se de ter o JDK 17 instalado em sua máquina.
2. Clone este repositório.
3. Configure os parâmetros de conexão com o banco de dados (URL, usuário, senha) no código-fonte, conforme necessário.
4. Crie o banco de dados e as tabelas utilizando os scripts SQL fornecidos ou conforme as instruções das aulas.
5. Utilize o Gradle para compilar o projeto: `gradlew build`
6. Execute a aplicação pela IDE de sua preferência (IntelliJ IDEA, Eclipse, VS Code) ou via terminal, conforme o método principal definido no projeto.
