package w.expenses8.data.domain.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import w.expenses8.data.core.model.AbstractType;

@SuperBuilder(builderMethodName = "with")
@Getter @Setter  @NoArgsConstructor
@Entity
@Table(name = "WEX_PayeeType")
public class PayeeType extends AbstractType<PayeeType> {

	private static final long serialVersionUID = 1L;

	public PayeeType(String name) {
		super(name, null, true);
	}
}
