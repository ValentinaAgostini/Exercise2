package it.rm.pagopa.ex2;

import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.*;

/**
 * The ChatClientThread class extends Thread and manage the stream in (listens for reading)
 * 
 * @author V. Agostini
 * @version 1.0
 * @since 23/08/2020
 * 
 */
public class ChatClientThread extends Thread {
	private Socket socket = null;
	private ChatClient client = null;
	private DataInputStream streamIn = null;
    private AtomicBoolean running = new AtomicBoolean(false);


	
	/**
	 * @param _client
	 * @param _socket
	 * @throws IOException 
	 */
	public ChatClientThread(ChatClient _client, Socket _socket) throws IOException {
		client = _client;
		socket = _socket;
		open();
		start();
	}

/**
 * opens stream in for reading new messages
 * @throws IOException 
 */
	public void open() throws IOException {
		try {
			streamIn = new DataInputStream(socket.getInputStream());
		} catch (IOException ioe) {
			System.out.println("Error getting input stream: " + ioe);
			client.stop();
			throw ioe;
		}
	}

/**
 * closes  streamin 
 * @throws IOException 
 */
	public void close() throws IOException {
		try {
		  	running.set(false);
        	Thread.currentThread().interrupt();
			if (streamIn != null)
				streamIn.close();
		} catch (IOException ioe) {
			System.out.println("Error closing input stream: " + ioe);
			throw ioe;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		running.set(true);
        while (running.get()) {
			try {
				//listening for new messages
				client.handle(streamIn.readUTF());
			} catch (IOException ioe) {
				try {
					client.stop();
					running.set(false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
