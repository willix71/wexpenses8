package w.expenses8.data.domain.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import w.expenses8.data.core.model.DBable;
import w.expenses8.data.domain.model.enums.TransactionFactor;
import w.expenses8.data.utils.DateHelper;

@SuperBuilder(builderMethodName = "with")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "WEX_Expense")
public class Expense extends DBable<Expense> {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY, cascade={MERGE, REFRESH, DETACH})
	private ExpenseType expenseType;
	
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	@NotNull
	private BigDecimal currencyAmount;

	@NotBlank
	@Size(min=3,max=3)
	private String currencyCode;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade={MERGE, REFRESH, DETACH})
	private ExchangeRate exchangeRate;
	
	@NotNull
	private BigDecimal accountingValue;

	@ManyToOne(fetch = FetchType.LAZY, cascade={MERGE, REFRESH, DETACH})
	private Payee payee;
	
	private String description;
	
	private String externalReference;
	
	@Valid
	@Size(min = 2, message = "A expense must have at least 2 transaction lines: one IN and one OUT")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "expense", cascade = { CascadeType.ALL }, orphanRemoval = true)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@OrderBy("factor, accountingValue")
	private Set<TransactionEntry> transactions;
	
	public Expense addTransaction(TransactionEntry transaction) {
		if (transactions == null) {
			transactions = new HashSet<TransactionEntry>();
		}
		transactions.add(transaction);
		transaction.setExpense(this);
		return this;
	}
	
	public List<Tag> getTags() {
		return transactions.stream().flatMap(t->t.getTags().stream()).distinct().collect(Collectors.toList());
	}
	
	public void updateValues() {
		transactions.stream().filter(t->t.getAccountingYear()==null).forEach(t->t.setAccountingYear(DateHelper.year(date)));
		if (exchangeRate == null) {
			accountingValue = currencyAmount;
		} else {
			accountingValue = exchangeRate.apply(currencyAmount);
		}
		computeCurrencyAmount(TransactionFactor.IN);
		computeCurrencyAmount(TransactionFactor.OUT);
		transactions.stream().forEach(e->e.setAccountingValue(exchangeRate == null?e.getCurrencyAmount():exchangeRate.apply(e.getCurrencyAmount())));
	}
	
	private void computeCurrencyAmount(TransactionFactor factor) {
		long numberOfEmpty = transactions.stream().filter(t->t.getFactor()==factor && t.getCurrencyAmount()==null).count();
		if (numberOfEmpty > 0) {
			BigDecimal leftOver = currencyAmount;
			for(TransactionEntry t : transactions) {
				if (t.getFactor()==factor && t.getCurrencyAmount()!=null) {
					leftOver = leftOver.subtract(t.getCurrencyAmount());
				}
			}
			BigDecimal perEach = leftOver.divide(BigDecimal.valueOf(numberOfEmpty));
			transactions.stream().filter(t->t.getFactor()==factor && t.getCurrencyAmount()==null).forEach(t->t.setCurrencyAmount(perEach));
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
