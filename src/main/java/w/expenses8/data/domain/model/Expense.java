package w.expenses8.data.domain.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.boot.model.relational.Namespace.ComparableHelper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import w.expenses8.data.config.CurrencyValue;
import w.expenses8.data.core.model.DBable;
import w.expenses8.data.domain.model.enums.TransactionFactor;
import w.expenses8.data.domain.validation.Expensenized;
import w.expenses8.data.domain.validation.ModuloTenized;
import w.expenses8.data.domain.validation.Transactionsized;
import w.expenses8.data.domain.validation.Warning;

@SuperBuilder(builderMethodName = "with")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "WEX_Expense")
@Expensenized
public class Expense extends DBable<Expense> {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY, cascade={MERGE, REFRESH, DETACH})
	private ExpenseType expenseType;
	
	@NotNull(message = "Expense's date is mandatory")
	private LocalDateTime date;

	private LocalDate payedDate;

	@NotNull(message = "Expense's amount is mandatory")
	@Positive(message = "Expense's amount 'should' be prositive",groups = Warning.class)
	private BigDecimal currencyAmount;

	@NotBlank(message = "Expense's currency is mandatory")
	@Size(min=3,max=3)
	private String currencyCode;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade={MERGE, REFRESH, DETACH})
	private ExchangeRate exchangeRate;
	
	@NotNull(message = "Expense's value is mandatory")
	private BigDecimal accountingValue;

	@NotNull(message = "Expense's payee is compulsory",groups = Warning.class)
	@ManyToOne(fetch = FetchType.LAZY, cascade={MERGE, REFRESH, DETACH})
	private Payee payee;
	
	private String description;
	
	@ModuloTenized(groups=Warning.class)
	private String externalReference;
	
	@Valid
	@Size(min = 2, message = "Expenses must have at least two transaction lines: one IN and one OUT")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "expense", cascade = { CascadeType.ALL }, orphanRemoval = true)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@OrderBy("factor, accountingValue")
	@Transactionsized
	private Set<TransactionEntry> transactions;
	
	private Integer documentCount;
	
	@Valid
	@ManyToMany(fetch = FetchType.LAZY, cascade={MERGE, REFRESH, DETACH})
	@JoinTable(name = "WEX_Expense_WEX_Document", joinColumns = @JoinColumn(name = "WEX_Expense_id"), inverseJoinColumns = @JoinColumn(name = "documentFiles_id"))
	@OrderBy("documentDate, fileName")
	private Set<DocumentFile> documentFiles;
	
	public void updateDocumentCount() {
		this.documentCount=documentFiles==null?0:documentFiles.size();
	}

	public Expense addDocumentFile(DocumentFile document) {
		if (documentFiles == null) {
			documentFiles = new HashSet<DocumentFile>();
		}
		documentFiles.add(document);
		updateDocumentCount();
		return this;
	}

	public Expense removeDocumentFile(DocumentFile document) {
		if (documentFiles != null) documentFiles.remove(document);
		updateDocumentCount();
		return this;
	}
	
	public Expense addTransaction(TransactionEntry transaction) {
		if (transactions == null) {
			transactions = new TreeSet<TransactionEntry>(new Comparator<TransactionEntry>() {
				@Override
				public int compare(TransactionEntry o1, TransactionEntry o2) {
					int c = ComparableHelper.compare(o2.getFactor(), o1.getFactor());
					if (c!=0) return c;
					c = ComparableHelper.compare(o2.getAccountingValue(), o1.getAccountingValue());
					if (c!=0) return c;
					return ComparableHelper.compare(o2.getUid(), o1.getUid());
				}
			});
		}
		transactions.add(transaction);
		transaction.setExpense(this);
		return this;
	}

	public Expense removeTransaction(TransactionEntry transaction) {
		if (transactions != null) transactions.remove(transaction);
		transaction.setExpense(null);
		updateDocumentCount();
		return this;
	}
	
	private static final Comparator<Tag> TagComparator = new Comparator<Tag>() {
		@Override
		public int compare(Tag o1, Tag o2) {
			int c = ComparableHelper.compare(o1.getType(), o2.getType());
			if (c!=0) return c;
			c = ComparableHelper.compare(o1.getNumber(), o2.getNumber());
			if (c!=0) return c;
			return ComparableHelper.compare(o1.getName(), o2.getName());
		}
	
	};
	public List<Tag> getTags() {
		return transactions.stream().flatMap(t->t.getTags().stream()).sorted(TagComparator).distinct().collect(Collectors.toList());
	}
	
	public void updateValues(BigDecimal precision) {
		updateDate(null);
		updateAmountValues(null, precision);
	}
	
	public void updateDate(LocalDate previousDate) {
		if (date==null) return;
		if (exchangeRate!=null && (exchangeRate.getDate()==null || (previousDate!=null && exchangeRate.getDate().equals(previousDate)))) exchangeRate.setDate(date.toLocalDate());
		transactions.stream().filter(t->t.getAccountingYear()==null || (previousDate!=null && t.getAccountingYear().equals(previousDate.getYear()))).forEach(t->t.setAccountingYear(date.getYear()));
		transactions.stream().filter(t->t.getAccountingDate()==null || (previousDate!=null && t.getAccountingDate().equals(previousDate))).forEach(t->t.setAccountingDate(date.toLocalDate()));
	}
	
	public void updateAmountValues(BigDecimal previousAmount, BigDecimal precision) {
		if (currencyAmount==null) return;
		
		// update same currency amount as expense
		if (previousAmount != null) {
			transactions.stream().filter(t->t.getCurrencyAmount()!=null && t.getCurrencyAmount().compareTo(previousAmount)==0).forEach(t->t.setCurrencyAmount(currencyAmount));
		}
		
		// update currency amount that are empty
		fillEmptyCurrencyAmount(TransactionFactor.IN, previousAmount);
		fillEmptyCurrencyAmount(TransactionFactor.OUT, previousAmount);
		
		// apply exchange rate
		if (exchangeRate == null) {
			accountingValue = currencyAmount;
			transactions.stream().forEach(e->e.setAccountingValue(e.getCurrencyAmount()));
		} else if (currencyAmount != null){
			accountingValue = exchangeRate.apply(currencyAmount);
			BigDecimal fullRatio = accountingValue.divide(currencyAmount, 8, RoundingMode.HALF_EVEN);
			transactions.stream().forEach(e->e.setAccountingValue(e.getCurrencyAmount().equals(currencyAmount)?accountingValue:e.getCurrencyAmount().multiply(fullRatio)));
		}
		
		// apply precision
		if (precision != null) {
			accountingValue = CurrencyValue.applyPrecision(accountingValue, precision);
			transactions.stream().forEach(e->e.setAccountingValue(CurrencyValue.applyPrecision(e.getAccountingValue(), precision)));
		}
	}
	
	private void fillEmptyCurrencyAmount(TransactionFactor factor, BigDecimal previousAmount) {
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
}
