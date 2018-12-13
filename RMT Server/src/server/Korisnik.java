package server;

public class Korisnik {

	
	private String username;
	private String password;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
//		boolean valid = false;
//		while (!valid) {
//			System.out.println("Unesite lozinku koja sadrzi jedno veliko slovo, jedan broj i ima bar 8 karaktera: ");
//			if (password.length()>=8 && checkString(password)) {
				this.password = password;
//				valid = true;
				
//			}
				
//		}
		
	}
	
	
	public Korisnik(String username, String password) {
		super();
		setUsername(username);
		setPassword(password);
	}
	
}
