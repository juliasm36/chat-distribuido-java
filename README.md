# Chat Distribuído em Java

Projeto acadêmico desenvolvido individualmente na disciplina de **Sistemas Distribuídos**, do curso de Sistemas de Informação da Universidade Feevale, no primeiro semestre de 2026.

O objetivo foi desenvolver uma aplicação de chat utilizando a arquitetura cliente-servidor, permitindo a comunicação entre múltiplos usuários conectados por meio de sockets TCP.

## Tecnologias utilizadas

- Java
- Java Swing
- Sockets TCP
- Threads
- Comunicação cliente-servidor

## Funcionalidades

- **Chat geral:** envio de mensagens para todos os usuários conectados.
- **Mensagens privadas:** comunicação direta com um usuário selecionado.
- **Lista de usuários:** exibição e atualização dos participantes conectados.
- **Identificação por nickname:** identificação dos usuários e verificação de nomes duplicados.
- **Envio de arquivos de texto:** transferência de conteúdo textual entre usuários.
- **Múltiplas conexões:** atendimento simultâneo de diferentes clientes por meio de threads.

## Estrutura do projeto

```text
chat-distribuido-java/
├── ChatServer.java
├── ClientHandler.java
├── ChatClient.java
├── README.md
└── .gitignore
```

### Principais classes

**ChatServer.java**

Responsável por iniciar o servidor, aceitar conexões e gerenciar a lista de usuários conectados.

**ClientHandler.java**

Responsável por processar as solicitações de cada cliente, incluindo mensagens públicas, privadas e envio de arquivos.

**ChatClient.java**

Responsável pela interface gráfica desenvolvida com Java Swing, permitindo visualizar mensagens, selecionar usuários e enviar arquivos.

## Como executar

**Pré-requisito:** Java JDK 11 ou superior instalado.

**1. Compile os arquivos Java**

No terminal, dentro da pasta do projeto, execute:

```bash
javac ChatServer.java ClientHandler.java ChatClient.java
```

**2. Inicie o servidor**

```bash
java ChatServer
```

O servidor será iniciado na porta **8084**.

**3. Abra um segundo terminal e inicie o cliente**

```bash
java ChatClient
```

Informe um nickname para entrar no chat.

**4. Abra outros clientes**

Para simular a comunicação entre usuários, execute `java ChatClient` novamente em outros terminais.

Com diferentes clientes conectados, é possível enviar mensagens públicas, selecionar destinatários para mensagens privadas e transferir arquivos de texto.

Os arquivos recebidos são armazenados na pasta `recebidos`, criada automaticamente pela aplicação.

## Aprendizados

O desenvolvimento deste projeto permitiu aplicar conceitos de:

- Arquitetura cliente-servidor.
- Comunicação em rede utilizando sockets TCP.
- Programação concorrente com threads.
- Gerenciamento de múltiplos usuários conectados.
- Desenvolvimento de interfaces gráficas com Java Swing.
- Manipulação e transferência de conteúdo textual entre aplicações.

---

**Projeto acadêmico individual — Sistemas Distribuídos | Universidade Feevale | 2026/01**
