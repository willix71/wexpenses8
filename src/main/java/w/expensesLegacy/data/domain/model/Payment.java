package w.expensesLegacy.data.domain.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class Payment extends DBable<Payment> implements Selectable, Closable {

	private static final long serialVersionUID = 2482940442245899869L;

	public static final String DEFAULT_FILENAME = "undefined";

	@NotEmpty
	private String filename = DEFAULT_FILENAME;

	@Temporal(TemporalType.DATE)
	private Date date;

	private boolean selectable = true;

	@OneToMany(mappedBy = "payment", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@OrderBy("date, amount")
	private List<Expense> expenses;

	@OneToMany(mappedBy = "payment" /* , cascade={CascadeType.ALL} */)
	@OrderBy("orderBy")
	private List<PaymentDta> dtaLines;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	// little hack to display something when the date is null
	public Object getNextDate() {
		if (date == null)
			return "next";
		else
			return MessageFormat.format("{0,date,dd.MM.yyyy}", date);
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public boolean isSelectable() {
		return selectable;
	}

	@Override
	public boolean isClosed() {
		return !selectable;
	}

	@Override
	public boolean getSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	public List<Expense> getExpenses() {
		return expenses;
	}

	public void setExpenses(List<Expense> expenses) {
		this.expenses = expenses;
	}

	public List<Expense> resetExpenses() {
		List<Expense> xs = this.expenses;
		this.expenses = new ArrayList<Expense>();
		return xs;
	}

	public void addExpense(Expense expense) {
		if (this.expenses == null)
			this.expenses = new ArrayList<Expense>();
		this.expenses.add(expense);
		expense.setPayment(this);
	}

	public void removeExpense(Expense expense) {
		if (this.expenses != null) {
			if (this.expenses.remove(expense)) {
				expense.setPayment(null);
			}
		}
	}

	public List<PaymentDta> getDtaLines() {
		return dtaLines;
	}

	public void setDtaLines(List<PaymentDta> dtaLines) {
		this.dtaLines = dtaLines;
	}

	@Override
	public String toString() {
		if (date == null)
			return "next" + " " + filename;
		return MessageFormat.format("{0,date,dd/MM/yyyy} {1}", date, filename);
	}

	@Override
	public Payment duplicate() {
		Payment klone = super.duplicate();
		klone.setExpenses(new ArrayList<Expense>());
		klone.setDtaLines(new ArrayList<PaymentDta>());
		return klone;
	}

	@Override
	public Payment klone() {
		Payment klone = super.klone();
		if (klone.getExpenses() != null) {
			klone.setExpenses(new ArrayList<Expense>(klone.getExpenses()));
		}
		if (klone.getDtaLines() != null) {
			klone.setDtaLines(new ArrayList<PaymentDta>(klone.getDtaLines()));
		}
		return klone;
	}
}
