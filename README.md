# Exercise2
Very simple chat server that listens on TCP port 10000 for clients. 
Chat protocol is very simple, clients connect with "telnet" and write single lines of text. 
On each new line of text, the server will broadcast that line to all other connected clients. 
Program should be fully tested too. 

-ChatServer.java : The class ChatServer interface and represents a chat server listening for clients to connect on port 10000. It mantains an array of ClientServerThread to manage single socket opened, associated 1:1 to clients connected.

-ChatServerThread.java: This class manages stream in (read) / stream out (write) for a single socket.

-ChatClient.java: The class ChatClient represents a new client that connects to server. It manages output streaming on the socket opened and contains a reference to ChatClientThread to manage input streaming.

-ChatClientThread.java: This class manages listens for new upcoming messages on the socket for the single client. 

-ChatTest: Test class for Chat program
