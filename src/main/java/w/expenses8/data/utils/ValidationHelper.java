package w.expenses8.data.utils;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;

public class ValidationHelper {
	protected static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	public static <T> String validate(T o, Class<?>... groups) {
		Set<ConstraintViolation<T>> violations = validator.validate(o, groups);
		if (violations.isEmpty()) return null;
		throw new ConstraintViolationException(violations);
	}

}