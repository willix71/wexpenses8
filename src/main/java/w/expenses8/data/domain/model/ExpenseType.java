package w.expenses8.data.domain.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import w.expenses8.data.core.model.AbstractType;
import w.expenses8.data.domain.model.enums.PayeeDisplayer;

@SuperBuilder(builderMethodName = "with")
@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = "WEX_ExpenseType")
public class ExpenseType extends AbstractType<ExpenseType> {

	private static final long serialVersionUID = 1L;

	@Builder.Default
	private PayeeDisplayer displayer = PayeeDisplayer.DEFAULT;
	
	public ExpenseType(String name) {
		super(name, null, true);
	}
}
