package w.expenses8.data.domain.criteria;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import w.expenses8.data.core.criteria.RangeCriteria;
import w.expenses8.data.domain.model.ExpenseType;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.utils.DateHelper;

@SuperBuilder(builderMethodName = "with")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ExpenseCriteria {

	private Payee payee;
	private String payeeText;
	private ExpenseType expenseType;
	private RangeCriteria<Date> date;
	private RangeCriteria<BigDecimal> currencyAmount;
	private RangeCriteria<BigDecimal> accountingValue;
	private String currencyCode;
	
	public static ExpenseCriteria thisYear() {
		ExpenseCriteria criteria = new ExpenseCriteria();
		criteria.date = new RangeCriteria<Date>(DateHelper.toDate(1,1), null);
		criteria.currencyAmount = new RangeCriteria<BigDecimal>();
		criteria.accountingValue = new RangeCriteria<BigDecimal>();
		return criteria;
	}
}
