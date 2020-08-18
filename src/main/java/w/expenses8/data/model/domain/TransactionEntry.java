package w.expenses8.data.model.domain;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import w.expenses8.data.model.core.DBable;
import w.expenses8.data.model.enums.TransactionFactor;
import w.expenses8.data.model.enums.TransactionFactorType;

@SuperBuilder(builderMethodName = "with")
@Accessors(chain = true) @Getter @Setter  @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "TransactionEntry2")
@TypeDefs({@TypeDef(name = "transactionFactorType", typeClass = TransactionFactorType.class) })
public class TransactionEntry extends DBable<TransactionEntry> {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Expense expense;

	@Builder.Default
	private boolean systemEntry = false;
	
	@NonNull
	@javax.validation.constraints.NotNull
	@Type(type = "transactionFactorType")
	private TransactionFactor factor;
	
	@NonNull
	@javax.validation.constraints.NotNull
	private BigDecimal currencyAmount;

	@NonNull
	@javax.validation.constraints.NotNull
	private BigDecimal accountingValue;

	private Integer accountingYear;

	private Long acountingOrder;
	
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
		this.acountingOrder = t.acountingOrder;
		this.accountingBalance = t.accountingBalance;
		this.tags = t.getTags()==null?null:new HashSet<Tag>(t.getTags());
	}
}
