package w.expenses8.data.model.domain;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import w.expenses8.data.model.core.DBable;

@SuperBuilder(builderMethodName = "with")
@Accessors(chain = true) @Getter @Setter  @NoArgsConstructor //@AllArgsConstructor
@Entity
@Table(name = "Expense2")
public class Expense extends DBable<Expense> {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.EAGER, cascade={MERGE, REFRESH, DETACH})
	private ExpenseType expenseType;
	
	@NonNull
	@javax.validation.constraints.NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	@NonNull
	@javax.validation.constraints.NotNull
	private BigDecimal currencyAmount;

	@NonNull
	@NotBlank
	@Size(min=3,max=3)
	private String currencyCode;

	@NonNull
	@javax.validation.constraints.NotNull
	private BigDecimal accountingValue;

	@ManyToOne(fetch = FetchType.EAGER, cascade={MERGE, REFRESH, DETACH})
	private ExchangeRate exchangeRate;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade={MERGE, REFRESH, DETACH})
	private Payee payee;
	
	private String description;
	
	@Valid
	@Size(min = 2, message = "A expense must have at least 2 transaction lines: one IN and one OUT")
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "expense", cascade = { CascadeType.ALL }, orphanRemoval = true)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@OrderBy("factor, accountingValue")
	private List<TransactionEntry> transactions;
	
	public void addTransaction(TransactionEntry transaction) {
		if (transactions == null) {
			transactions = new ArrayList<TransactionEntry>();
		}
		transactions.add(transaction);
		transaction.setExpense(this);
	}

	public Expense withTransaction(TransactionEntry transaction) {
		addTransaction(transaction);
		return this;
	}
	
	public void updateValues() {
		if (exchangeRate == null) {
			accountingValue = currencyAmount;
		} else {
			accountingValue = exchangeRate.apply(currencyAmount);
		}
		for(TransactionEntry e : transactions) {
			if (e.getCurrencyAmount()==null) {
				e.setCurrencyAmount(currencyAmount);
			}
			if (e.getAccountingValue()==null) {
				if (exchangeRate == null) {
					e.setAccountingValue(e.getCurrencyAmount());
				} else {
					e.setAccountingValue(exchangeRate.apply(e.getCurrencyAmount()));
				}
			}
		}
	}
	
	@Override
	public void copy(Expense x) {
		super.copy(x);
		this.date = x.date;
		this.currencyAmount = x.currencyAmount;
		this.currencyCode = x.currencyCode;
		this.accountingValue = x.accountingValue;
		this.exchangeRate = x.exchangeRate;
		this.payee = x.payee;
		if (transactions != null) {
			for(TransactionEntry e: x.transactions) {
				TransactionEntry e2 = new TransactionEntry();
				e2.copy(e);
				addTransaction(e2);
			}
		}
	}
}
