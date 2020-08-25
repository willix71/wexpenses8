package w.expenses8.data.domain.model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import w.expenses8.data.core.model.DBable;
import w.expenses8.data.domain.model.enums.TransactionFactor;
import w.expenses8.data.domain.model.enums.TransactionFactorType;

@SuperBuilder(builderMethodName = "with")
@Getter @Setter  @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "WEX_TransactionEntry")
@TypeDefs({@TypeDef(name = "transactionFactorType", typeClass = TransactionFactorType.class) })
public class TransactionEntry extends DBable<TransactionEntry> {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Expense expense;

	@Builder.Default
	private boolean systemEntry = false;
	
	@NotNull
	@Type(type = "transactionFactorType")
	private TransactionFactor factor;
	
	@NotNull
	private BigDecimal currencyAmount;

	@NotNull
	private BigDecimal accountingValue;

	private Integer accountingYear;

	private Long accountingOrder;
	
	private BigDecimal accountingBalance;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@OrderBy("number")
	private Set<Tag> tags;
	
	public TransactionEntry addTag(Tag tag) {
		if (tags == null) {
			tags = new HashSet<Tag>();
		}
		tags.add(tag);
		return this;
	}
	
	@Override
	public void copy(TransactionEntry t) {
		super.copy(t);
		this.expense = t.expense;
		this.systemEntry = t.systemEntry;
		this.factor = t.factor;
		this.currencyAmount = t.currencyAmount;
		this.accountingValue = t.accountingValue;
		this.accountingYear = t.accountingYear;
		this.accountingOrder = t.accountingOrder;
		this.accountingBalance = t.accountingBalance;
		this.tags = t.getTags()==null?null:new HashSet<Tag>(t.getTags());
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
