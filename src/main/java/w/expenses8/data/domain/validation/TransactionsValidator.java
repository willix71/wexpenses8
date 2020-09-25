package w.expenses8.data.domain.validation;

import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.domain.model.TransactionEntry;
import w.expenses8.data.utils.TransactionsSums;

@Slf4j
public class TransactionsValidator implements ConstraintValidator<Transactionsized, Set<TransactionEntry>> {

	@Override
	public boolean isValid(Set<TransactionEntry> lines, ConstraintValidatorContext context) {
		if (lines!=null && lines.size()>=2) {
			TransactionsSums sums = new TransactionsSums().compute(lines);
			if (!sums.isValid()) {
				if (!sums.getCurrencyAmountSums().isValid()) {
					log.warn("Sum currency amount is {}", sums.getCurrencyAmountSums().getDelta());
				}
				if (!sums.getAccountingValueSums().isValid()) {
					log.warn("Sum accounting value is {}", sums.getAccountingValueSums().getDelta());
				}
				return false;
				
			}
		}
		return true;
	}
}