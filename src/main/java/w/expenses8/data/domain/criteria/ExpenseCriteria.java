package w.expenses8.data.domain.criteria;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import w.expenses8.data.core.criteria.RangeLocalDateCriteria;
import w.expenses8.data.core.criteria.RangeNumberCriteria;
import w.expenses8.data.domain.model.ExpenseType;
import w.expenses8.data.domain.model.Payee;

@SuperBuilder(builderMethodName = "with")
@ToString
@EqualsAndHashCode
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ExpenseCriteria implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Payee payee;
	private String payeeText;
	private ExpenseType expenseType;
	private String currencyCode;
	private String description;
	private String externalReference;

	private Integer accountingYear;
	@Builder.Default
	private RangeLocalDateCriteria localDate = new RangeLocalDateCriteria();
	@Builder.Default
	private RangeNumberCriteria amountValue = new RangeNumberCriteria();
	@Builder.Default
	private List<TagCriteria> tagCriterias = new ArrayList<TagCriteria>();

	public void clear() {
		payee=null;
		payeeText=null;
		expenseType=null;
		currencyCode= null;
		description=null;
		externalReference=null;
		accountingYear=null;
		localDate.clear();
		amountValue.clear();
		tagCriterias.clear();
	}
	
	public static ExpenseCriteria from(LocalDate d) {
		ExpenseCriteria criteria = new ExpenseCriteria();
		criteria.localDate.setFrom(d);
		return criteria;
	}
}
