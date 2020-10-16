package w.expenses8.data.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

//import org.hibernate.annotations.Type;
//import org.hibernate.annotations.TypeDef;
//import org.hibernate.annotations.TypeDefs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import w.expenses8.data.config.CurrencyValue;
import w.expenses8.data.core.model.DBable;
import w.expenses8.data.domain.model.enums.TransactionFactor;

@SuperBuilder(builderMethodName = "with")
@Getter @Setter  @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "WEX_TransactionEntry")
public class TransactionEntry extends DBable<TransactionEntry> {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Expense expense;

	@Builder.Default
	private boolean systemEntry = false;
	
	@NotNull
	private TransactionFactor factor;
	
	@NotNull
	private BigDecimal currencyAmount;

	@NotNull
	private BigDecimal accountingValue;

	private Integer accountingYear;
	
	@NotNull
	private LocalDate accountingDate;

	private Long accountingOrder;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private DocumentFile consolidationFile;
	
	private BigDecimal accountingBalance;
	
	@Transient
	private BigDecimal liveBalance;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "WEX_TransactionEntry_WEX_Tag", joinColumns = @JoinColumn(name = "WEX_TransactionEntry_id"), inverseJoinColumns = @JoinColumn(name = "tags_id"))
	@OrderBy("number,name")
	private Set<Tag> tags;
	
	public TransactionEntry addTag(Tag tag) {
		if (tags == null) {
			new HashSet<Tag>();
		}
		tags.add(tag);
		return this;
	}
	
	// we need a list for the <p:autoComplete>
	public List<Tag> getTagsList() {
		return tags==null?null:new LinkedList<Tag>(tags);
	}
	
	public void setTagsList(List<Tag> tagsList) {
		if (tagsList == null) {
			tags = null;
		} else if (tags==null) {
			tags = new HashSet<Tag>(tagsList);
		} else {
			tags.addAll(tagsList);
			tags.retainAll(tagsList);
		}
	}
	
	public void updateValue(BigDecimal precision) {
		updateValue(expense==null?null:expense.getExchangeRate(), precision);
	}
	
	public void updateValue(ExchangeRate exchangeRate, BigDecimal precision) {
		// apply exchange rate
		if (exchangeRate == null) {
			accountingValue = currencyAmount;
		} else if (currencyAmount != null){
			accountingValue = exchangeRate.apply(currencyAmount);
		}
		
		// apply precision
		if (precision != null) {
			accountingValue = CurrencyValue.applyPrecision(accountingValue, precision);
		}
	}

	private TransactionEntry(TransactionFactor factor, Tag... tags) {
		this.factor = factor;
		this.tags = new HashSet<Tag>(Arrays.asList(tags));
	}

	public static TransactionEntry in(Tag...tags) {
		return new TransactionEntry(TransactionFactor.IN, tags);
	}
	
	public static TransactionEntry out(Tag...tags) {
		return new TransactionEntry(TransactionFactor.OUT, tags);
	}
}
