package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.LinkedList;

public class ClientHandler extends Thread {

	BufferedReader clientInput = null;
	PrintStream clientOutput = null;
	Socket socket = null;
	String username;

	public static LinkedList<Korisnik> korisnici = new LinkedList<Korisnik>();

	public void dodaj() {
		Korisnik e = new Korisnik("username", "eEe123456789");

		korisnici.add(e);

	}

	private static boolean checkString(String str) {

		char ch;
		boolean velikoSlovo = false;
		boolean maloSlovo = false;
		boolean broj = false;
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i);
			if (Character.isDigit(ch)) {
				broj = true;
			} else if (Character.isUpperCase(ch)) {
				velikoSlovo = true;
			} else if (Character.isLowerCase(ch)) {
				maloSlovo = true;
			}
			if (broj && velikoSlovo && maloSlovo)
				return true;
		}
		return false;
	}

	private String unos () {
		String ulaz = "";
		try {
			ulaz = clientInput.readLine();
			if (ulaz.startsWith("***quit")) {
				Server.onlineUsers.remove(this);
				socket.close();
			}
			return ulaz;
		} catch (Exception e) {
			clientOutput.println("Bacen izuzetak.");
		}
		return null;
	}
	
	public ClientHandler(Socket socketForCom) {
		socket = socketForCom;

	}

	public void run() {

		try {
			dodaj(); // unosimo rucno jednog korisnika radi testiranja
			clientInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			clientOutput = new PrintStream(socket.getOutputStream());

			boolean valid = false;

			do {

				clientOutput.println("Dobrodosli na server za racunanje.");
				clientOutput.println("Za regisraciju novog naloga izaberite opciju 1, ");
				clientOutput.println("Za logovanje na postojeci nalog izaberite opciju 2, ");
				clientOutput.println("Za nastavak bez registracije izaberite opciju 3. ");


				String izborRegStr = unos();
			
				int izborReg = 0;
				try {
					izborReg = Integer.parseInt(izborRegStr);
				} catch (NumberFormatException e1) {

					clientOutput.println("Niste uneli izbor lepo. ");
					Server.onlineUsers.remove(this);
					socket.close();
					
				}

				switch (izborReg) {
				case 1:
					boolean validUser = false;
					boolean postoji = false;

					clientOutput.println("Unesite korisnicko ime za novog korisnika: ");
					while (!validUser) {
						String user = clientInput.readLine();
						if (user == null)
							continue;

						for (int i = 0; i < korisnici.size(); i++) {
							if (korisnici.get(i).getUsername().equals(user)) {
								clientOutput.println(
										"Korisnik sa korisnickim imenom " + user + " postoji. Pokusajte ponovo.");
								postoji = true;
								break;
							}
							
						}
						if (postoji) {
							clientOutput.println("DOSLO");
							continue;
						}
						clientOutput.println("Unesite lozinku: ");
						String pass = clientInput.readLine();
						if (pass == null) break;
						while (!checkString(pass) || pass.length() < 8) {
							clientOutput.println("Lozinka nije dobra, unesite novu: ");
							pass = clientInput.readLine();
						}

						valid = true;
						Korisnik e = new Korisnik(user, pass);
						korisnici.addLast(e);
					}
					break;
				case 2:
				
					
				}
				clientOutput.println("Unesite korisnicko ime: ");
				username = clientInput.readLine();

				valid = true;
				clientOutput.println(">>> Dobrodosao " + username + "\nZa izlazak unesite ***quit");

			} while (!valid);

			String msg;

			// ------------------------------------------------------------------------------------//

			while (true) {

				clientOutput.println("Za sabiranje unesite 1, za oduzimanje unesite 2");
				String izborString = clientInput.readLine();
				if (izborString == null || izborString.startsWith("***quit")) {
					break;
				}

				int izbor = 0;
				try {
					izbor = Integer.parseInt(izborString);
				} catch (NumberFormatException e1) {

					clientOutput.println("Niste uneli izbor lepo. Unesite ponovo. ");
				}

				switch (izbor) {
				case 1:
					clientOutput.println("Izabrali ste zbir. Unesite izraz u obliku X+Y: ");
					String input = clientInput.readLine();
					String[] niz = input.split("\\+");

					clientOutput.println("Zbir je: ");

					try {
						clientOutput.println(Integer.parseInt(niz[0]) + Integer.parseInt(niz[1]));
					} catch (Exception e) {
						clientOutput.println("Niste lepo uneli");

					}

					break;
				case 2:
					clientOutput.println("Izabrali ste razliku. Unesite izraz u obliku X-Y: ");
					String input1 = clientInput.readLine();
					String[] niz1 = input1.split("\\-");

					clientOutput.println("Razlika je: ");

					try {
						clientOutput.println(Integer.parseInt(niz1[0]) - Integer.parseInt(niz1[1]));
					} catch (Exception e) {
						clientOutput.println("Niste lepo uneli");

					}

					break;

				default:
					clientOutput.println("Niste uneli ni jednu od opcija. ");
					break;
				}
			}

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
