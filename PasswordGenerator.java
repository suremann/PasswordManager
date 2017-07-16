package stuff2;

import java.util.ArrayList;
import java.util.Random;

public class PasswordGenerator {
	
	String upper_alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	String lower_alpha = upper_alpha.toLowerCase();
	String specials = "~`!@#$%^&*_+-=";
	String digits = "0123456789";
	
	public String generate_password(int size){
		String password = "";
		Random rand = new Random();

		while(password.length() < size){
			switch (rand.nextInt(4)){
				case 0: password += upper_alpha.charAt(rand.nextInt(upper_alpha.length())); break;	
				case 1: password += lower_alpha.charAt(rand.nextInt(lower_alpha.length())); break;	
				case 2: password += specials.charAt(rand.nextInt(specials.length())); break;	
				case 3: password += digits.charAt(rand.nextInt(10)); break;	
			}
		}
		return password;
	}
	
	public String generate_password(int size, ArrayList<String> characters){
		String password = "";
		Random rand = new Random();
		
		if(characters.size() > 0){
			while(password.length() < size){
				int index = rand.nextInt(characters.size());
				password += characters.get(index).charAt(rand.nextInt(characters.get(index).length())); 
			}
		}
		
		return password;
	}

}
