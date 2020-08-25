package w.expensesLegacy.data.domain.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name = "PayeeTypeOld")
@Table(name = "PayeeType")
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY, region="typable")
public class PayeeType extends AbstractType<PayeeType> {

	private static final long serialVersionUID = 2482940442245899869L;

	public PayeeType() {
	   super();
   }

	public PayeeType(String name) {
	   super(name);
   }
	
	public PayeeType(String name, boolean selectable) {
	   super(name, selectable);
   }

}
