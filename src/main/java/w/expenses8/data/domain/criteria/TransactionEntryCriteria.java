package w.expenses8.data.domain.criteria;

import java.util.Collection;

import lombok.AllArgsConstructor;
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
	private Collection<Tag> tags;
}
