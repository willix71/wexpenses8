package w.expenses8.data.model.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import w.expenses8.data.model.core.DBable;

@SuperBuilder(builderMethodName = "with")
@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ExpenseRate2")
public class ExchangeRate extends DBable<ExchangeRate> {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	private Payee institution;

	@Temporal(TemporalType.DATE)
	private Date date;

	@NonNull
	@NotBlank
	@Size(min = 3, max = 3)
	private String toCurrencyCode;

	@NonNull
	@NotBlank
	@Size(min = 3, max = 3)
	private String fromCurrencyCode;

	@Builder.Default
	private double rate = 1.0;

	private Double fee;

	private Double fixFee;

	public BigDecimal apply(BigDecimal amount) {
		BigDecimal value = amount.multiply(BigDecimal.valueOf(getRate()));
		if (getFee() != null) {
			value = value.multiply(BigDecimal.valueOf(1 + getFee()));
		}
		if (getFixFee() != null) {
			value = value.add(BigDecimal.valueOf(getFixFee()));
		}
		return value;
	}

	@Override
	public void copy(ExchangeRate t) {
		super.copy(t);
		this.institution = t.institution;
		this.date = t.date;
		this.toCurrencyCode = t.toCurrencyCode;
		this.fromCurrencyCode = t.fromCurrencyCode;
		this.rate = t.rate;
		this.fee = t.fee;
		this.fixFee = t.fixFee;
	}

}
