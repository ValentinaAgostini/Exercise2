package it.rm.pagopa.ex2;

import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.*;

/**
 * The ChatServerThread class  extends Thread and manage the single socket  
 * @author V. Agostini
 * @version 1.0
 * @since 23/08/2020 
 * 
 */
public class ChatServerThread extends Thread {
	private ChatServer server = null;
	private Socket socket = null;
	private int ID = -1;
	private DataInputStream streamIn = null;
	private DataOutputStream streamOut = null;
    private AtomicBoolean running = new AtomicBoolean(false);

	
	/**
	 * Default Constructor
	 * @param _server
	 * @param _socket
	 */
	public ChatServerThread(ChatServer server, Socket socket) {
		super();
		this.server = server;
		this.socket = socket;
		ID = socket.getPort();
	}
	
	/**
	 * @return running
	 */
	public boolean isRunning() {
        return running.get();
    }
 
	/**
	 * This method sends a message on the stream out
	 * @param msg
	 * @throws IOException
	 */
	public void send(String msg) throws IOException {
		try {
			System.out.println("Server Thread having " + ID + " writing.");
			streamOut.writeUTF(msg);
			streamOut.flush();
		} catch (IOException ioe) {
			System.out.println(ID + " ERROR sending: " + ioe.getMessage());
			server.remove(ID);
			close();
			throw ioe;
		}
	}

	/**
	 * This method returns the ID associated to the Server Thread
	 * @return
	 */
	public int getID() {
		return ID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
        running.set(true);
		System.out.println("Server Thread having " + ID + " is running.");
		 while (running.get()) {
			try {
               // thread is listening for new messages from the client           
			   server.handle(ID, streamIn.readUTF());
			} catch (IOException ioe) {
				// error reading from the socket
				System.out.println(ID + " ERROR reading: " + ioe.getMessage());
				// try to remove the client from the chat
				try {
					server.remove(ID);
					this.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				// stop cycle
				break;
			
			}
		}		
		
	}

	/**
	 * This methods allocates streamin (for reading) and streamout (for writing) starting from the socket
	 * @throws IOException
	 */
	public void open() throws IOException {
        System.out.println("Server Thread having " + ID + " is opening.");
		streamIn = new DataInputStream(new BufferedInputStream(	socket.getInputStream()));
		streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
	}

	/**
	 * This method closes socket and stream
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (socket != null)
			socket.close();
		if (streamIn != null)
			streamIn.close();
		if (streamOut != null)
			streamOut.close();
		Thread.currentThread().interrupt();
	    running.set(false);
	}
}