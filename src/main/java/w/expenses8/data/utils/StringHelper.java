package w.expenses8.data.utils;

public class StringHelper {

	public static boolean isEmpty(String s) {
		return s==null || s.trim().isEmpty();
	}
	
	public static boolean hasUpperCase(String s) {
		for(char c:s.toCharArray()) {
			if (Character.isUpperCase(c)) return true;
		}
		return false;
	}
	
}
