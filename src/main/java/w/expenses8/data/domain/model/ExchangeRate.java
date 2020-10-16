package w.expenses8.data.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import w.expenses8.data.core.model.DBable;

@SuperBuilder(builderMethodName = "with")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "WEX_ExchangeRate")
public class ExchangeRate extends DBable<ExchangeRate> {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	private Payee institution;

	private LocalDate date;

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

	public ExchangeRate duplicate() {
		ExchangeRate d=new ExchangeRate();
		d.institution = this.institution;
		d.date = this.date;
		d.toCurrencyCode = this.toCurrencyCode;
		d.fromCurrencyCode = this.fromCurrencyCode;
		d.rate = this.rate;
		d.fee = this.fee;
		d.fixFee = this.fixFee;
		return d;
	}

}
