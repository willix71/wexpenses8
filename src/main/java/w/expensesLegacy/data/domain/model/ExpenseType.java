package w.expensesLegacy.data.domain.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity(name = "ExpenseTypeOld")
@Table(name = "ExpenseType")
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY, region="typable")
public class ExpenseType extends AbstractType<ExpenseType> {

	private static final long serialVersionUID = 2482940442245899869L;

	private String paymentGeneratorClassName;
	
	public ExpenseType() {
		super();
	}

	public ExpenseType(String name) {
		super(name);
	}
	
	public ExpenseType(String name, boolean selectable) {
		super(name, selectable);
	}

	public ExpenseType(String name, boolean selectable, String paymentGeneratorClassName) {
		super(name, selectable);
		this.paymentGeneratorClassName = paymentGeneratorClassName;
	}
	
	public String getPaymentGeneratorClassName() {
		return paymentGeneratorClassName;
	}

	public void setPaymentGeneratorClassName(String paymentGeneratorClassName) {
		this.paymentGeneratorClassName = paymentGeneratorClassName;
	}
}
