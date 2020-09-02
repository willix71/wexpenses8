package w.expenses8.data.config;

import java.math.BigDecimal;

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
		return amount.divideToIntegralValue(precision).multiply(precision);
	}
}
