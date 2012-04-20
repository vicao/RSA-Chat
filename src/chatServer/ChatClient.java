package chatServer;
import java.math.BigInteger;
import java.net.*;
import java.util.ArrayList;
import java.io.*;

import miniRSA.MiniRSA;

public class ChatClient implements Runnable {  
	private static Socket socket = null;
	private static BigInteger e, c, d, n, srv_e, srv_c;
	private static Thread  writingTd = null;

	ChatClient(Socket s) {
		socket = s;
	}

	public void chat() throws IOException {
			writingTd = new Thread(new ChatClient(socket));
	        writingTd.start();
	        
	        BufferedReader streamIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String fromServer = "";

	        String[] clientPubKey = null;
			fromServer = streamIn.readLine();			
			clientPubKey = fromServer.split("#");
			srv_e = new BigInteger(clientPubKey[0]);
			srv_c = new BigInteger(clientPubKey[1]);	
			
	        fromServer = "";
			while ((fromServer = streamIn.readLine()) != null) {
				if (fromServer.equals(".bye"))
					break;
				MiniRSA.decryptPrint(fromServer, d, n); 
			}
			streamIn.close();
		}
	
	
	@Override
	public void run() {
		try {
			System.out.println("Writing to server...");
			DataOutputStream streamOut = new DataOutputStream(socket.getOutputStream());
			streamOut.writeBytes(e.toString() + "#" + c.toString() + '\n');
			
			String toServer = "";
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			boolean done = false;
			while (!done) {	
				System.out.println("client: type, enter .bye to quit");
				toServer = in.readLine();			
				if (!toServer.equalsIgnoreCase(".bye")) {
					ArrayList<BigInteger> encryptedNumList = MiniRSA.encrypt(toServer, srv_c, srv_e);
					toServer = "";
					for (int i = 0; i < encryptedNumList.size(); i++) {
						toServer += encryptedNumList.get(i).toString();
						toServer += " ";
					}
					System.out.println(" to " + toServer);
					streamOut.writeBytes(toServer + '\n');
				}
				else done = true;
			}
			streamOut.close(); 
		} 
		catch (IOException e) {
			System.err.println("Unable to write to " + socket); }

	}

	public static void main(String[] args) throws IOException {  
		if (args.length != 5) {
			System.out.println("Server Usage: port# p_from p_end q_from q_end");
			return;
		}
		int port = Integer.parseInt(args[0]);		
		BigInteger[] keys = MiniRSA.generateKey(args[1], args[2], args[3], args[4]);
		e = keys[0];
		c = keys[1];
		d = keys[2];
		n = keys[3];

		Socket skt = new Socket(InetAddress.getByName("localhost"), port);
		System.out.println("Accepted by server: " + skt);
		
		ChatClient cc = new ChatClient(skt);
		cc.chat();
		socket.close();
	}

	
		
}




















