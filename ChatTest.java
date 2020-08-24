package it.rm.pagopa.ex2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The ChatTest test class for Chat.
 * 
 * @author V. Agostini
 * @version 1.0
 * @since 23/08/2020
 *
 */

public class ChatTest {

	static ChatServer chatS;
	static ChatClient chatC;
	
	/**
	 * Stops server and client if running
	 * @throws IOException
	 */
	@AfterClass
	public static void stopAll() throws IOException{
		if(chatS != null){
			chatS.stop();
			chatS = null;
		}
		if(chatC != null){
			chatC.stop();
			chatC = null;
		}
	}

	/**
	 * Starts server 
	 * @throws IOException
	 */
	@BeforeClass
	public static void start() throws IOException{
		if(chatS != null)
			chatS.stop();
		chatS = new ChatServer();
		
	}
	
	/**
	 * Test server is running
	 * @throws IOException
	 */
	@Test
	public void testServerRunning() throws IOException {
		assertNotNull(chatS);
	}


	/**
	 * Test server listens on port 10000
	 * @throws IOException
	 */
	@Test
	public void testServerPortListeningOn10000() throws IOException {
		assertEquals(10000, ChatServer.getPort());
	}
	

	
	/**
	 * Test client connects 
	 * @throws Exception 
	 */
	@Test
	public void testClientConnects() throws Exception {
		chatC = new ChatClient("TestClient");
		assertEquals(1, chatS.getClients().size());
		
	}

	/**
	 * Test client writes and receives on the socket 
	 * @throws Exception 
	 */
	@Test
	public void testBroadCast() throws Exception {
		boolean broadcastOk = false;
		chatC.getStreamOut().writeUTF("hello");
		while (!broadcastOk){
			long received = chatC.getNumReceivedMessages();
			if(received > 0){
				assertEquals(1, received);
				broadcastOk = true;
			}
		}
		assertTrue(broadcastOk);
	}
	
	/**
	 * Test client leaves correctly the chat
	 * @throws Exception 
	 */
	@Test
	public void testLeavesChat() throws Exception {
		boolean leaveOk = false;
		chatC.getStreamOut().writeUTF("bye");
		while (!leaveOk){
			if(chatS.getClients().indexOf(chatC)==-1)
				leaveOk = true;
		}
		assertTrue(leaveOk);
	}
}
