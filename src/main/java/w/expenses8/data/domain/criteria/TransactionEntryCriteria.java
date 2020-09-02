package w.expenses8.data.domain.criteria;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import w.expenses8.data.domain.model.Tag;

@SuperBuilder(builderMethodName = "with")
@Getter @Setter  @NoArgsConstructor @AllArgsConstructor
public class TransactionEntryCriteria extends ExpenseCriteria {

	private static final long serialVersionUID = 1L;

	private Integer accountingYear;
	
	@Builder.Default
	private List<Tag> tags = new ArrayList<Tag>();
	
	public static TransactionEntryCriteria from(int year) {
		TransactionEntryCriteria criteria = new TransactionEntryCriteria();
		criteria.setAccountingYear(year);
		return criteria;
	}
}
