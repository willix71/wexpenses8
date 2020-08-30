package w.expenses8.data.domain.validation;

import java.math.BigInteger;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IbanValidator implements ConstraintValidator<Ibanized, String> {

	private static final BigInteger MODULO = new BigInteger("97");
	
	@Override
	public void initialize(Ibanized constraintAnnotation) {
		// void
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value==null || value.trim().length()==0) return true;
		char[] cars = value.toCharArray();
		
		int length = computeIbanLength(cars);
		if (length<14||length>34) {
			// iban must be between 14 and 34 characters long
			return false;
		}
		BigInteger bi = new BigInteger(convertToNumber(cars));
		return bi.mod(MODULO).intValue() == 1;
	}

	public static int computeIbanLength(char[] cars) {
		int l=0;
		for(char c: cars) {
			if (Character.isLetterOrDigit(c)) l++;
		}
		return l;
	}
	
	/**
	 * @see http://fr.wikipedia.org/wiki/ISO_13616
	 * 
	 * @param cars an IBAN account number
	 * @return an 'ibanized' number
	 */
	public static String convertToNumber(char[] cars) {
		int l = cars.length;
		
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<l;i++) {
			char c = cars[(i+4) % l];
			if (Character.isDigit(c)) {
				sb.append(c);
			} else if (Character.isLetter(c)) {
				sb.append(convertToNumber(c));				
			}
		}
		return sb.toString();
	}
	
	public static int convertToNumber(char c) {
		return 10 + Character.toUpperCase(c) - 'A';
	}
	
	
	public static String formatIban(String iban) {
	    if (iban==null) return null;
	    int i =0;
	    StringBuilder sb = new StringBuilder();
	    for(char c: iban.toCharArray()) {
	    	if (Character.isWhitespace(c)) continue;
	    	sb.append(c);
	    	if (++i % 4 == 0) {
	    		sb.append(" ");
	    	}	    	
	    }
	    return sb.toString();	
	}
}