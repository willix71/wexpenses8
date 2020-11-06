package w.expenses8.data.domain.validation;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CcpValidator implements ConstraintValidator<Ccpnized, String> {

	@Override
	public void initialize(Ccpnized constraintAnnotation) {
		// void
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {		
		if (value==null || value.trim().length()==0) return true;
		String parts[] = value.split("-");
		if (parts.length<3) return false;
		String allInOne = getPrefix(2-parts[0].length()) + parts[0] + getPrefix(6-parts[1].length()) + parts[1] + parts[2];   
		return ModuloTenValidator.isValidNumber(allInOne);
	}
	
	private static String getPrefix(int len) {
		char[] c = new char[len];
		Arrays.fill(c, '0');
		return new String(c);
	}
}
