package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

public class ClientHandler extends Thread {

	BufferedReader clientInput = null;
	PrintStream clientOutput = null;
	Socket socket = null;
	String username;

	public static LinkedList<Korisnik> korisnici = new LinkedList<Korisnik>();

	public boolean registrovan = false;

	public void dodaj() {

		try (BufferedReader br = new BufferedReader(new FileReader("data/log.txt"))) {
			String line;
			int i = 0;
			String user = "";
			String pass = "";

			while ((line = br.readLine()) != null) {
				if (i == 0 || i % 2 != 0) {
					user = line;

				} else {
					pass = line;
				}
				
				if (i > 0 && i % 2 == 0) {
					
					Korisnik e = new Korisnik(user, pass);
					
					korisnici.add(e);
					clientOutput.println(e.getUsername());
					clientOutput.println(e.getPassword());
				}

				i++;
			}
			clientOutput.println("ima ih " + korisnici.size());
		} catch (FileNotFoundException e1) {

			e1.printStackTrace();
		} catch (IOException e1) {

			e1.printStackTrace();
		}
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

	private String unos() {
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

	private void racuna() {
		
	}
	
	public void run() {

		try {

			clientInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			clientOutput = new PrintStream(socket.getOutputStream());

			//dodaj(); // unosimo rucno jednog korisnika radi testiranja
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
					break;

				}

				switch (izborReg) {
				case 1:
					boolean validUser = false;
					boolean postoji = false;

					while (!validUser) {
						clientOutput.println("Unesite korisnicko ime za novog korisnika: ");
						String user = unos();

						for (int i = 0; i < korisnici.size(); i++) {
							if (korisnici.get(i).getUsername().equals(user)) {
								clientOutput.println(
										"Korisnik sa korisnickim imenom " + user + " postoji. Pokusajte ponovo.");
								postoji = true;
								break;
							}

						}
						if (postoji) {
							// clientOutput.println("DOSAO");
							postoji = false;
							continue;
						}
						// clientOutput.println("DOSAOaaa");
						clientOutput.println("Unesite lozinku: ");
						String pass = unos();

						while (pass == null || !checkString(pass) || pass.length() < 8) {
							clientOutput.println("Lozinka nije dobra, unesite novu: ");
							pass = unos();
						}

						validUser = true;
						Korisnik e = new Korisnik(user, pass);
						korisnici.addLast(e);
						registrovan = true;
						try (FileWriter fw = new FileWriter("data/log.txt", true);
								BufferedWriter bw = new BufferedWriter(fw);
								PrintWriter out = new PrintWriter(bw)) {
							out.println(e.getUsername());
							out.println(e.getPassword());
						} catch (IOException e1) {

						}
					}
					break;
				case 2:
					clientOutput.println("Unesite vas username: ");
					String usernn = unos();

					boolean postojiNalog = false;
					for (int i = 0; i < korisnici.size(); i++) {
						if (korisnici.get(i).getUsername().contains(usernn)) {
							clientOutput.println("Korisnicko ime pronadjeno. Unesite sifru: ");
							postojiNalog = true;
							boolean validPass = false;
							while (!validPass) {
								String passs = unos();
								if (passs.equals(korisnici.get(i).getPassword())) {
									validPass = true;
									clientOutput.println("Lozinka upsesno unesena.");
									break;
								}

								else
									clientOutput.println("Lozinka netacna, pokusajte ponovo.");
							}
							if (validPass) {
								registrovan = true;
								break;
							}
						}
					}
					if (!postojiNalog) {
						clientOutput.println(
								"Uneti username ne postoji medju registrovanim korisnicima. Nastavljate kao gost. ");
						registrovan = false;
					}

					break;

				}
				clientOutput.println("Unesite korisnicko ime: ");
				username = clientInput.readLine();

				valid = true;
				clientOutput.println(">>> Dobrodosao " + username + "\nZa izlazak unesite ***quit");

			} while (!valid);

			// ------------------------------------------------------------------------------------//

			if (registrovan) {
				while (true) {
					clientOutput.println("Za sabiranje unesite 1, za oduzimanje unesite 2");
					String izborString = unos();
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
			} else {
				int brojac = 0;
				clientOutput.println("Niste registrovani, imate pravo na 3 kalkulacije:");
				while (brojac < 3) {
					clientOutput.println("Za sabiranje unesite 1, za oduzimanje unesite 2");
					String izborString = unos();
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
					brojac++;

				}

				clientOutput.println(">>> Iskoristili ste 3 kalkulacije.Pay bitch!");

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
