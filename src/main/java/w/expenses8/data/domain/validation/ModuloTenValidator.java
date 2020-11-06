package w.expenses8.data.domain.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @See https://www.postfinance.ch/content/dam/pfch/doc/cust/download/modulo_biz_fr.pdf
 * @See https://www.raiffeisen.ch/rch/fr/clients-entreprises/trafic-paiements-et-liquidites/debiteurs/bulletin-de-versement-orange.html
 * @author willi
 *
 */
public class ModuloTenValidator implements ConstraintValidator<ModuloTenized, String> {

	private static final int[][] KEYS = {
			{0,9,4,6,8,2,7,1,3,5,0},
			{9,4,6,8,2,7,1,3,5,0,9},
			{4,6,8,2,7,1,3,5,0,9,8},
			{6,8,2,7,1,3,5,0,9,4,7},
			{8,2,7,1,3,5,0,9,4,6,6},
			{2,7,1,3,5,0,9,4,6,8,5},
			{7,1,3,5,0,9,4,6,8,2,4},
			{1,3,5,0,9,4,6,8,2,7,3},
			{3,5,0,9,4,6,8,2,7,1,2},
			{5,0,9,4,6,8,2,7,1,3,1}};
	
	public static boolean isValidNumber(String value) {
		char[] cs=value.toCharArray();
		int num = 0;
		int computed = 0;
		int digit = 0;
		for (char c: cs) {
			computed = KEYS[num][10]; // value in last column
			digit = c-'0';
			if (digit<0 || digit>9) continue; // not a digit
			num = KEYS[num][digit];
		}
		return digit==computed;
	}
	
	@Override
	public void initialize(ModuloTenized constraintAnnotation) {
		// void
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value==null || value.trim().length()==0) return true;
		return isValidNumber(value);
	}
}