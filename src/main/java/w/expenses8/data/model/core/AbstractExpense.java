package w.expenses8.data.model.core;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import w.expenses8.data.model.domain.ExchangeRate;
import w.expenses8.data.model.domain.Payee;

@SuperBuilder(builderMethodName = "with")
@Accessors(chain = true) @Getter @Setter  @NoArgsConstructor @AllArgsConstructor
@MappedSuperclass
public abstract class AbstractExpense<T extends AbstractExpense<T>> extends DBable<T> {

	private static final long serialVersionUID = 1L;
	
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
	
	@Override
	public void copy(T t) {
		super.copy(t);
		AbstractExpense<?> a = (AbstractExpense<?>) t;
		this.date = a.date;
		this.currencyAmount = a.currencyAmount;
		this.currencyCode = a.currencyCode;
		this.accountingValue = a.accountingValue;
		this.exchangeRate = a.exchangeRate;
		this.payee = a.payee;
	}
}
