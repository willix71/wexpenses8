package w.expenses8.data.config;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@ConfigurationProperties("wexpenses.currency-value")
public class CurrencyValue {
	
	private String code;
	
	private BigDecimal precision;
	
	public BigDecimal applyPrecision(BigDecimal amount) {
		return applyPrecision(amount, precision);
	}

	public static BigDecimal applyPrecision(BigDecimal amount, BigDecimal precision) {
		if (amount==null || precision==null) return amount;
		long x = amount.divide(precision).setScale(0, RoundingMode.HALF_UP).longValue();
		return BigDecimal.valueOf(x).multiply(precision);
	}
}
