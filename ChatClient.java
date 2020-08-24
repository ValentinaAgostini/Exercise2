package it.rm.pagopa.ex2;

import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The ChatClient program manages connection and streaming for new client
 * Thread manages stream out (writing)
 * ChatClientThread manages stream in (reading)
 * @author V. Agostini
 * @version 1.0
 * @since 23/08/2020 
 *
 */
public class ChatClient implements Runnable {

    private Socket socket = null;
    private Thread thread = null;
    private DataInputStream console = null;
    private DataOutputStream streamOut = null;
    private Scanner scan;
    private ChatClientThread client = null;
    private String userName;
    private int serverPort = 10000;
    private String serverName = "localhost";
    private AtomicBoolean running = new AtomicBoolean(false);
	private long numReceivedMessages;

    /**
     * This method sets userName for the client
     * @param userName
     */
    void setUserName(String userName) {
        this.userName = userName; 
   }

    
    /**
     * This method returns userName
     * @return userName
     */
    String getUserName() {
        return this.userName;
    }
    
    /**
     * This method returns streamOut
     * @return streamOut
     */
    public DataOutputStream getStreamOut() {
		return streamOut;
	}
    
    /**
     * This method returns numReceivedMessages
     * @return numReceivedMessages
     */
    public long getNumReceivedMessages() {
		return numReceivedMessages;
	}
    
    /**
     * @return client
     */
    public ChatClientThread getClientThread() {
		return client;
	}


    /**
     * Deafult constructor
     * Asks for username
     * Establish the connection with ChatServer and starts
     * @param serverName
     * @param serverPort
     * @throws Exception 
     */
    public ChatClient() throws Exception {
      
        try {
        	//request for a userName
            Scanner in = new Scanner(System.in);
            System.out.println("Hello! Please type your nickname: ");
            setUserName(in.nextLine());
            
            //Try to connect to ChatServer on port 10000
            System.out.println("Establishing connection. Please wait ...");
            socket = new Socket(serverName, serverPort);
            System.out.println("Connected as : "+getUserName());
            
            //Connection ok. Start writing.
            start();
        } catch (UnknownHostException uhe) {
            System.out.println("Host unknown: " + uhe.getMessage());
            throw uhe;
        } catch (IOException ioe) {
            System.out.println("Unexpected exception: " + ioe.getMessage());
            throw ioe;
        }
    }

    /**
     * Constructor with userName as input
     * Establish the connection with ChatServer
     * @param serverName
     * @param serverPort
     * @throws Exception 
     */
    public ChatClient(String userName) throws Exception {
      
        try {
        	//Set userName
        	setUserName(userName);
 
            //Try to connect to ChatServer on port 10000
            System.out.println("Establishing connection. Please wait ...");
            socket = new Socket(serverName, serverPort);
            System.out.println("Connected as : "+getUserName());
            
            //Connection ok. Start writing.
            start();
        } catch (UnknownHostException uhe) {
            System.out.println("Host unknown: " + uhe.getMessage());
            throw uhe;
        } catch (IOException ioe) {
            System.out.println("Unexpected exception: " + ioe.getMessage());
            throw ioe;
        }
    }

    
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
    	running.set(true);
        while (thread != null && running.get()) {
            try {
            	//writes on stream out
                streamOut.writeUTF(getUserName()  +" says :"+scan.nextLine());
                streamOut.flush();
            } catch (IOException ioe) {
                System.out.println("Sending error: " + ioe.getMessage());
                try {
					stop();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
    }

    /**
     * This method prints message received on stream in
     * @param msg
     * @throws IOException 
     */
    public void handle(String msg) throws IOException {
        if (msg.equals("bye")) {
            System.out.println("bye. you are now disconnected");
            stop();
        } else {
        	this.numReceivedMessages++;
            System.out.println(msg);
        }
    }

    /**
     * This method starts a new Thread to manage the socket
     * @throws IOException
     */
    public void start() throws IOException {
        scan  = new Scanner(System.in);
        streamOut = new DataOutputStream(socket.getOutputStream());
        if (client == null) {
        	// instantiates new threads to manage socket in (for listening) and out (for writing)
            client = new ChatClientThread(this, socket);
            thread = new Thread(this);
            thread.start();
        }
    }

    /**
     * stops the client thread
     * closes stream and socket
     * @throws IOException 
     */
    public void stop() throws IOException {
        
        try {
            if (console != null) {
                console.close();
            }
            if (streamOut != null) {
                streamOut.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ioe) {
            System.out.println("Error closing ...");
        }
        if (thread != null) {
        	running.set(false);
        	Thread.currentThread().interrupt();
        	thread = null;
         }
        if (client!=null){
            client.close();
        }
    }

    /**
     * main method 
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        new ChatClient();
    }

}
