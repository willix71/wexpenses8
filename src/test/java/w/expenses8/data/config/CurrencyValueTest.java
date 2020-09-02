package w.expenses8.data.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class CurrencyValueTest {
	
	CurrencyValue currencyValue = new CurrencyValue("CHF", new BigDecimal("0.05"));
	
	private void test(BigDecimal amount, CurrencyValue currencyValue, BigDecimal expected) {
		BigDecimal value = currencyValue.applyPrecision(amount);
		assertThat(value).isEqualByComparingTo(expected);		
	}
	@Test
	public void testOneThird() {
		test(new BigDecimal(1.0/3.0), currencyValue, new BigDecimal("0.3"));
	}
	
	@Test
	public void test1048() {
		test(new BigDecimal("10.484"), currencyValue, new BigDecimal("10.45"));
	}

	@Test
	public void test75() {
		test(new BigDecimal("75.00"), currencyValue, new BigDecimal("75.00"));
	}
	
}
