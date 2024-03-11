# cs3534-team-assessment

Team assessment for Distributed Systems &amp; Security.

---

## Instructions

Compile each section with `make compile[SECTION_LETTER]`. Example: `make compileD`.

Clean up each directory of the class files with `make clean[SECTION_LETTER]`. Example `make cleanD`.

---

## D3-D1

- [x]  Create a client-server.
- [x]  A user can create a client who types messages on the command line.
- [x]  When the client is aborted with `Ctrl-C` then the conversation stops.
- [x]  Your task is to create a simple `Messenger` server, where clients can exchange messages via a server.
- [x]  Start the assessment with a simple client – server application based on a socket communication, where the client sends messages to the server and the server is printing these messages
(regard this as a kind of log of activities and as a debugging feature on the server).

## C3-C1

- [ ]  Develop a simple command language for clients to register with the server. This will require a check to see if a message is a keyword of a command language, using `java.util.Scanner`.
- [ ]  A user of the client application can type messages on the command line, which is sent by the server **to all** the clients.

## B3-B1

- [ ]  The server can handle
  - [ ]  Socket communication between two clients.
  - [ ]  User group communication
- [ ]  Users must be able to `CREATE`, `JOIN`, `LEAVE` + `REMOVE` groups.
- [ ]  The `SEND` command must be able to send to a specific group or to send to a specific user.

## A5-A1

- [ ]  There should be **specific topics** of interest, that users want to sign up to.
- [ ]  Each user can subscribe or unsubscribe from a topic.
