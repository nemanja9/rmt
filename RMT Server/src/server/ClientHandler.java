package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ClientHandler extends Thread {

	BufferedReader clientInput = null;
	PrintStream clientOutput = null;
	Socket socket = null;
	String username;

	public ClientHandler(Socket socketForCom) {
		socket = socketForCom;

	}

	public void run() {

		try {
			clientInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			clientOutput = new PrintStream(socket.getOutputStream());

			boolean valid = false;

			do {
				clientOutput.println("Unesite korisnicko ime: ");
				username = clientInput.readLine();
				if (username.contains(" "))
					clientOutput.println("Kor ime ne sme da sadrzi razmak. ");
				else {
					valid = true;
					clientOutput.println(">>> Dobrodosao " + username + "\nZa izlazak unesite ***quit");
				}
			} while (!valid);
			// for (ClientHandler klijent : Server.onlineUsers) {
			// if (klijent != this) {
			// klijent.clientOutput.println(">>> korisnik " + username + " je usao u sobu.
			// ");
			// }
			//
			// }
			clientOutput.println("Za sabiranje unesite 1, za oduzimanje unesite 2");
			int izbor = Integer.parseInt(clientInput.readLine());

			switch (izbor) {
			case 1:
				clientOutput.println("Izabrali ste zbir. Unesite izraz u obliku X+Y: ");
				String input = clientInput.readLine();
				String[] niz = input.split("\\+");

				clientOutput.println("Zbir je: ");

				clientOutput.println(Integer.parseInt(niz[0]) + Integer.parseInt(niz[1]));

				break;
			case 2:
				String input1 = clientInput.readLine();
				String[] niz1 = input1.split("\\-");

				clientOutput.println("Razlika je: ");

				clientOutput.println(Integer.parseInt(niz1[0]) - Integer.parseInt(niz1[1]));

				break;

			default:
				clientOutput.println("Niste uneli ni jednu od opcija. ");
				break;
			}
			String msg;
			while (true) {
				msg = clientInput.readLine();

				if (msg.startsWith("***quit")) {
					break;
				}

				// for (ClientHandler klijent : Server.onlineUsers) {
				// klijent.clientOutput.println(" ( " + username +" ) " + msg);
				// }
			}
//			clientOutput.println(">>> Dovidjenja " + username);
//			for (ClientHandler klijent : Server.onlineUsers) {
//				if (klijent != this) {
//					klijent.clientOutput.println(">>> korisnik " + username + " je napustio sobu. ");
//				}
//			}

			Server.onlineUsers.remove(this);
			socket.close();

		} catch (IOException e) {
			Server.onlineUsers.remove(this);
			for (ClientHandler klijent : Server.onlineUsers) {
				if (klijent != this) {
					klijent.clientOutput.println(">>> korisnik " + username + " je napustio sobu. ");
				}
			}

			e.printStackTrace();
		}
	}

}
