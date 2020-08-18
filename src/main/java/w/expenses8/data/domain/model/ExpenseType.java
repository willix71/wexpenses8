package w.expenses8.data.domain.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import w.expenses8.data.core.model.AbstractType;

@SuperBuilder(builderMethodName = "with")
@Accessors(chain = true) @Getter @Setter  @NoArgsConstructor
@Entity
@Table(name = "ExpenseType2")
public class ExpenseType extends AbstractType<ExpenseType> {

	private static final long serialVersionUID = 1L;

	public ExpenseType(String name) {
		super(name, null, true);
	}
}
