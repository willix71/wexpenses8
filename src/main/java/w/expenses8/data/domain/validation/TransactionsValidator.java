package w.expenses8.data.domain.validation;

import java.math.BigDecimal;
import java.util.Set;
import java.util.function.Function;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.domain.model.TransactionEntry;

@Slf4j
public class TransactionsValidator implements ConstraintValidator<Transactionsized, Set<TransactionEntry>> {

	@Override
	public boolean isValid(Set<TransactionEntry> lines, ConstraintValidatorContext context) {
		if (lines!=null && lines.size()>=2) {
			Function<TransactionEntry, BigDecimal> amountMapper = l->l.getCurrencyAmount().multiply(new BigDecimal(l.getFactor().getFactor()));
			
			BigDecimal currencyAmount = lines.stream().filter(l->!l.isSystemEntry()).map(amountMapper).reduce((l,r)->l==null?r:r==null?null:l.add(r)).orElse(BigDecimal.ZERO);
			log.debug("Sum currency amount is {}", currencyAmount);
			if (currencyAmount.compareTo(BigDecimal.ZERO)!=0) {
				return false;
			}

			Function<TransactionEntry, BigDecimal> valueMapper = l->l.getAccountingValue().multiply(new BigDecimal(l.getFactor().getFactor()));
			
			BigDecimal accountingValue = lines.stream().filter(l->!l.isSystemEntry()).map(valueMapper).reduce((l,r)->l==null?r:r==null?null:l.add(r)).orElse(BigDecimal.ZERO);
			log.debug("Sum accounting value is {}", accountingValue);
			if (accountingValue.compareTo(BigDecimal.ZERO)!=0) {
				return false;
			}
		}
		return true;
	}
}