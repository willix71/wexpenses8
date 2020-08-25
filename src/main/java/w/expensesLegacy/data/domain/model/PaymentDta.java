package w.expensesLegacy.data.domain.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class PaymentDta extends DBable<PaymentDta> {

	private static final long serialVersionUID = 2482940442245899869L;

	@NotNull
	private int orderBy;

	@NotNull
	@ManyToOne(fetch=FetchType.LAZY)
	private Payment payment;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Expense expense;
	
	@NotNull
	@Size(min = 128, max = 128)
	private String data;

	public PaymentDta() {}
		
	public PaymentDta(Payment payment, int orderBy, Expense expense, String data) {
		super();
		this.orderBy = orderBy;
		this.payment = payment;
		this.expense = expense;
		this.data = data;
	}

	public int getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(int orderBy) {
		this.orderBy = orderBy;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public Expense getExpense() {
		return expense;
	}

	public void setExpense(Expense expense) {
		this.expense = expense;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return data;
	}
}
