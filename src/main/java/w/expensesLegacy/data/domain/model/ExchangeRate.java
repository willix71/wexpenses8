package w.expensesLegacy.data.domain.model;

import java.text.MessageFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity(name = "ExchangeRateOld")
@Table(name = "ExchangeRate")
public class ExchangeRate extends DBable<ExchangeRate> {

	private static final long serialVersionUID = 2482940442245899869L;

	@ManyToOne
	// @JoinColumn(name="PAYEE_OID")
	private Payee institution;

	@Temporal(TemporalType.DATE)
	private Date date;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	private Currency toCurrency;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	private Currency fromCurrency;

	@NotNull
	private Double rate;

	private Double fee;

	private Double fixFee;
	
	public ExchangeRate() {}
	
	public Payee getInstitution() {
		return institution;
	}

	public void setInstitution(Payee institution) {
		this.institution = institution;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Currency getToCurrency() {
		return toCurrency;
	}

	public void setToCurrency(Currency currency) {
		this.toCurrency = currency;
	}

	public Currency getFromCurrency() {
		return fromCurrency;
	}

	public void setFromCurrency(Currency currency) {
		this.fromCurrency = currency;
	}

	public Double getStrenghedRate() {
		if (fromCurrency.getStrengh() != null && toCurrency.getStrengh() !=null && fromCurrency.getStrengh() > toCurrency.getStrengh()) {
			return 1/rate;
		}
		
		return rate;
	}
	
	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

	public Double getFixFee() {
      return fixFee;
   }

   public void setFixFee(Double fixFee) {
      this.fixFee = fixFee;
   }

   @Override
	public String toString() {
		return MessageFormat.format("{0} x {2,number, 0.00000} {1}", fromCurrency, toCurrency, rate);
	}
}
