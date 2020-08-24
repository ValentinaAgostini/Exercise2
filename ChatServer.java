package it.rm.pagopa.ex2;

import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.*;

/**
 * The ChatServer program implements a chat server listening for clients on port 10000
 * When a client says "bye" the server removes it from the chat
 * @author V. Agostini
 * @version 1.0
 * @since 23/08/2020 
 * 
 */
public class ChatServer implements Runnable {

	ArrayList<ChatServerThread> clients = new ArrayList<ChatServerThread>();
	private ServerSocket server = null;
	private Thread thread = null;
	private static int port = 10000;

	
	/**
	 * @return clients
	 */
	public ArrayList<ChatServerThread> getClients() {
		return clients;
	}

 
	/**
	 * @param clients
	 */
	public void setClients(ArrayList<ChatServerThread> clients) {
		this.clients = clients;
	}
	
	/**
	 * @return port
	 */
	public static int getPort() {
		return port;
	}


	/**
	 * @param port
	 */
	public static void setPort(int port) {
		ChatServer.port = port;
	}


	/**
	 * Default constructor
	 * Starts the server listening on port 10000
	 * @param port
	 * @throws IOException 
	 */
	public ChatServer() throws IOException {
		try {
			System.out.println("Server binding to port " + port + ", wait  ...");
			server = new ServerSocket(port);
			System.out.println("Server started: " + server);
			start();
		
		} catch (IOException ioe) {
			System.out.println("Server can not bind to port " + port + ": " + ioe.getMessage());
			throw ioe;
		}
	}

	
	/* 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (thread != null) {
			try {
				System.out.println("Server waiting for a client ...");
				addThread(server.accept());
			} catch (IOException ioe) {
				System.out.println("Server accept error: " + ioe);
				stop();
			}
		}
	}

	/**
	 * starts a new Thread
	 */
	public void start() {

		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	/**
	 * stops a Thread
	 */
	public void stop() {

		if (thread != null) {
			thread.interrupt();
			thread = null;
		}
	}

	
	/**
	 * This method manage the chat sending input to every client connected
	 * It also manages requests for leaving the chat 
	 * @param ID
	 * @param input
	 * @throws IOException
	 */
	public synchronized void handle(int ID, String input) throws IOException {
		if (input.contains("bye")) {
			// this is a request for leaving
			System.out.println("Server:  client " + ID + " is leaving the chat");
			// say bye to the client
			Iterator<ChatServerThread> iter = clients.iterator(); 
			while (iter.hasNext()) {
				ChatServerThread threadS = iter.next();
				if(threadS.isRunning()){
					if(threadS.getID() == ID){
						threadS.send("bye. you are now disconnected.");
						threadS.close();
						iter.remove();
					} else
						// broadcast message to every client except for the one removed
						threadS.send(input);
				}
			}
		} else{ 
			// broadcast message to every client
			Iterator<ChatServerThread> iter = clients.iterator(); 
			while (iter.hasNext()) {
				ChatServerThread threadS = iter.next();
				if(threadS.isRunning())
					threadS.send(input);
			}
		}
	}

	/**
	 * This method removes the client connected having a specific ThreadID  
	 * @param ID
	 * @throws IOException
	 */
	public synchronized void remove(int ID) throws IOException {
			System.out.println("Removing client thread " + ID );		
			// say bye to the client
			Iterator<ChatServerThread> iter = clients.iterator(); 
			while (iter.hasNext()) {
				ChatServerThread threadS = iter.next();
				if(threadS.getID() == ID){
					if(threadS.isRunning()){
						threadS.send("bye. you are now disconnected.");
						iter.remove();
						threadS.close();
				}
			} 
		}	
	}

	/**
	 * This method associate a new Thread to a new Client
	 * @param socket
	 * @throws IOException, RuntimeException
	 */
	private void addThread(Socket socket) throws IOException {
			// new client connected
			System.out.println("Client n." + clients.size()+1 + " accepted: "+ socket);
			ChatServerThread newChatServerMgr = new ChatServerThread(this, socket);
			clients.add(newChatServerMgr);
			try {
				//open and starts a new Thread and put into the client list
				newChatServerMgr.open();
				newChatServerMgr.start();
			} catch (IOException ioe) {
				System.out.println("Error opening thread: " + ioe);
				throw ioe;
			}
	}

	
	/**
	 * main method 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException {
		new ChatServer();
	}
}
