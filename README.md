# CS3534-team-assessment

Team assessment for Distributed Systems &amp; Security.

---

## Instructions

Each folder contains a Makefile, which has commands to run the server and the client, as well as commands to clean up the workspace.

---

## D3-D1

- [x]  Create a client-server.
- [x]  A user can create a client who types messages on the command line.
- [x]  When the client is aborted with `Ctrl-C` then the conversation stops.
- [x]  Your task is to create a simple `Messenger` server, where clients can exchange messages via a server.
- [x]  Start the assessment with a simple client – server application based on a socket communication, where the client sends messages to the server and the server is printing these messages
(regard this as a kind of log of activities and as a debugging feature on the server).

## C3-C1

- [x]  Develop a simple command language for clients to register with the server. This will require a check to see if a message is a keyword of a command language, using `java.util.Scanner`.
- [x]  A user of the client application can type messages on the command line, which is sent by the server **to all** the clients.

## B3-B1

- [x]  The server can handle
  - [x]  Socket communication between two clients.
  - [x]  User group communication
- [x]  Users must be able to `CREATE`, `JOIN`, `LEAVE` + `REMOVE` groups.
- [x]  The `SEND` command must be able to send to a specific group or to send to a specific user.

## A5-A1

- [x]  There should be **specific topics** of interest, that users want to sign up to.
- [x]  Each user can subscribe or unsubscribe from a topic.

## Running the program

**Cloning the repository**
- Make sure you have Git installed on your device or have access through it on your web browser. 
- Open the source code you want to run and press `< > Code`. 
- Copy the URL for the code.
- Open your IDE, the Visual Studio Code IDE  is recommended.
- Write the `git clone` command followed by the URL you have copied from github, this clones the repository onto the local machine.

**Downloading the extension packs**

- You may have to install some extensions onto your IDE, for this specific program you will need the code runner for Java, Debugger for Java code and Makefile extensions(if on MacOS/Linux)

**Run the code**

- Go into the `RunMessenger` file and press `run` 
- Then go into `RunClient` file and press run
- You will then see prompts in your terminal, you can follow these prompts and view your messages in the terminal 
- You can add another client but simply going into `RunClient` file again and pressing the Run button.

**Exit**

- To exit the server and the client programs, you use `CTRL+C`.
