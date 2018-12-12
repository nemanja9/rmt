package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server {

	
	public static LinkedList<ClientHandler> onlineUsers = new LinkedList<ClientHandler>();

	public static void main(String[] args) {
		int port = 9000;
		ServerSocket serverSocket = null;
		Socket socketForCom = null;
		
		try {
			serverSocket = new ServerSocket(port);
			while(true) {
				System.out.println("Cekam na konekciju");
				
				socketForCom = serverSocket.accept();
				
				System.out.println("Doslo je do konekvije");
				ClientHandler klijent = new ClientHandler (socketForCom);
				onlineUsers.add(klijent);
				
				klijent.start();
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
