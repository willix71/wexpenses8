package w.expenses8.data.domain.criteria;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import w.expenses8.data.core.criteria.RangeCriteria;
import w.expenses8.data.domain.model.ExpenseType;

@SuperBuilder(builderMethodName = "with")
@Accessors(chain = true) @Getter @Setter  @NoArgsConstructor @AllArgsConstructor
public class ExpenseCriteria {

	private ExpenseType expenseType;
	private RangeCriteria<Date> date;
	private RangeCriteria<BigDecimal> currencyAmount;
	private String currencyCode;
	private RangeCriteria<BigDecimal> accountingValue;
}
