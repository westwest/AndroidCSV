package se.acoder.androidcsv.file;

/**
 * Class represents a three-letter file-suffix like txt or htm. It 
 * may hold ANY three-letter combination. 
 * @author Johannes Westlund
 *
 */
public class Extention {
	String ext;
	
	/**
	 * Default constructor. It automatically supplies a "." in front
	 *  of the three letters given.
	 * @param ext Any three-letter combination intended as a file-suffix.
	 */
	private Extention(String ext){
		this.ext = "."+ext;
	}
	
	public static Extention createExtention(String ext){
		if((ext.length() == 3) && isAlpha(ext))
			return new Extention(ext);
		return null;
	}
	
	public String getExtention(){
		return ext;
	}
	
	/**
	 * Checks if string contains only letters
	 * @param str String to check 
	 * @return True if string only consist of letters
	 */
	private static boolean isAlpha(String str) {
	    char[] chars = str.toCharArray();

	    for (char c : chars) {
	        if(!Character.isLetter(c)) {
	            return false;
	        }
	    }
	    return true;
	}
}
