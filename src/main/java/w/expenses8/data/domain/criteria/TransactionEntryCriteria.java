package w.expenses8.data.domain.criteria;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder(builderMethodName = "with")
@Getter @Setter @NoArgsConstructor //@AllArgsConstructor
public class TransactionEntryCriteria extends ExpenseCriteria {

	private static final long serialVersionUID = 1L;
	
	public static TransactionEntryCriteria from(int year) {
		TransactionEntryCriteria criteria = new TransactionEntryCriteria();
		criteria.setAccountingYear(year);
		return criteria;
	}
}
