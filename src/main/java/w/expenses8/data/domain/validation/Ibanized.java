package w.expenses8.data.domain.validation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.METHOD,ElementType.FIELD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=IbanValidator.class)
public @interface Ibanized {

	/**
	 * Can be overriden with a custom message by using curly brackers
	 * i.e.: "{w.wexpense.validation.iban}"
	 */
	String message() default "Invalid IBAN number";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
}
