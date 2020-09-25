package w.expenses8.data.domain.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import w.expenses8.data.domain.model.Expense;

public class ExpenseValidator implements ConstraintValidator<Expensenized, Expense> {

	@Override
	public boolean isValid(Expense value, ConstraintValidatorContext context) {
		// nothing yet
		return true;
	}

}
