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
		boolean valid = false;
		while (!valid) {
			System.out.println("Unesite lozinku koja sadrzi jedno veliko slovo, jedan broj i ima bar 8 karaktera: ");
			if (password.length()>=8 && checkString(password)) {
				this.password = password;
				valid = true;
			}
				
		}
		
	}
	
	private static boolean checkString(String str) {
	    char ch;
	    boolean velikoSlovo = false;
	    boolean maloSlovo = false;
	    boolean broj = false;
	    for(int i=0;i < str.length();i++) {
	        ch = str.charAt(i);
	        if( Character.isDigit(ch)) {
	        	broj = true;
	        }
	        else if (Character.isUpperCase(ch)) {
	        	velikoSlovo = true;
	        } else if (Character.isLowerCase(ch)) {
	        	maloSlovo = true;
	        }
	        if(broj && velikoSlovo && maloSlovo)
	            return true;
	    }
	    return false;
	}
}
