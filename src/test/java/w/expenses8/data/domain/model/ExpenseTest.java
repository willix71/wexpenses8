package w.expenses8.data.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import w.expenses8.data.domain.model.enums.TagType;
import w.expenses8.data.utils.ExpenseHelper;

public class ExpenseTest {

	static final BigDecimal precision = new BigDecimal("0.05");
	static final Offset<BigDecimal> offset = Offset.offset(new BigDecimal("0.00001"));
	
	@Test
	public void testUpdateAmount() {
		BigDecimal amount = new BigDecimal("45.02");
		Expense x = ExpenseHelper.build(LocalDate.now(), amount);
		assertThat(x.getTransactions()).hasSize(2);
		assertThat(x.getAccountingValue()).isEqualTo(amount);

		x.updateAmountValues(null, precision);		
		BigDecimal value = new BigDecimal("45.00");
		assertThat(x.getAccountingValue()).isEqualTo(value);		
		assertThat(x.getTransactions()).extracting("currencyAmount").allMatch(d->d.equals(amount));
		assertThat(x.getTransactions()).extracting("accountingValue").allMatch(d->d.equals(value));
		
		BigDecimal newAmount = new BigDecimal("54.04");
		BigDecimal newValue = new BigDecimal("54.05");
		x.setCurrencyAmount(newAmount);
		x.updateAmountValues(amount,precision);
		assertThat(x.getAccountingValue()).isEqualTo(newValue);		
		assertThat(x.getTransactions()).extracting("currencyAmount").allMatch(d->((BigDecimal)d).compareTo(newAmount)==0);
		assertThat(x.getTransactions()).extracting("accountingValue").allMatch(d->((BigDecimal)d).compareTo(newValue)==0);
	}
	
	@Test
	public void testUpdateAmountWithExchangeRate() {
		BigDecimal amount = new BigDecimal("45.02");
		Expense x = ExpenseHelper.build(LocalDate.now(), amount, ExchangeRate.with().rate(1.2).fee(.1).fixFee(2.0).fromCurrencyCode("US").toCurrencyCode("CHF").build());
		assertThat(x.getAccountingValue()).isEqualTo(new BigDecimal("61.4264"));

		x.updateAmountValues(null, precision);		
		BigDecimal roundedValue = new BigDecimal("61.45");
		assertThat(x.getAccountingValue()).isEqualTo(roundedValue);		
		assertThat(x.getTransactions()).extracting("currencyAmount").allMatch(d->d.equals(amount));
		assertThat(x.getTransactions()).extracting("accountingValue").allMatch(d->d.equals(roundedValue));
		
		BigDecimal newAmount = new BigDecimal("54.04");
		BigDecimal newValue = new BigDecimal("73.3328");
		x.setCurrencyAmount(newAmount);
		x.updateAmountValues(amount,null);
		assertThat(x.getAccountingValue()).isEqualTo(newValue);		
		assertThat(x.getTransactions()).extracting("currencyAmount").allMatch(d->((BigDecimal)d).compareTo(newAmount)==0);
		assertThat(x.getTransactions()).extracting("accountingValue").allMatch(d->((BigDecimal)d).compareTo(newValue)==0);
		
		BigDecimal roundedNewValue = new BigDecimal("73.35");
		x.setCurrencyAmount(newAmount);
		x.updateAmountValues(amount,precision);
		assertThat(x.getAccountingValue()).isEqualTo(roundedNewValue);		
		assertThat(x.getTransactions()).extracting("currencyAmount").allMatch(d->((BigDecimal)d).compareTo(newAmount)==0);
		assertThat(x.getTransactions()).extracting("accountingValue").allMatch(d->((BigDecimal)d).compareTo(roundedNewValue)==0);
	}
	
	@Test
	public void testUpdateAmountWithExchangeRateAndMoreThanTwoTransactions() {
		Expense x = ExpenseHelper.build(LocalDate.now(), new BigDecimal("45.02"),
				ExchangeRate.with().rate(1.2).fee(.1).fixFee(2.0).fromCurrencyCode("US").toCurrencyCode("CHF").build(), 
				Tag.with().name("cash").type(TagType.ASSET).build(), Tag.with().name("pills").type(TagType.EXPENSE).build(), Tag.with().name("doctor").type(TagType.EXPENSE).build());
		assertThat(x.getTransactions()).hasSize(3);
		assertThat(x.getAccountingValue()).isEqualTo("61.4264");
		assertThat(ExpenseHelper.getTransactionOut(x).get(0).getCurrencyAmount()).isEqualTo("45.02");
		assertThat(ExpenseHelper.getTransactionOut(x).get(0).getAccountingValue()).isEqualTo("61.4264");
		assertThat(ExpenseHelper.getTransactionIn(x).get(0).getCurrencyAmount()).isEqualTo("22.51");
		assertThat(ExpenseHelper.getTransactionIn(x).get(1).getCurrencyAmount()).isEqualTo("22.51");
		assertThat(ExpenseHelper.getTransactionIn(x).get(0).getAccountingValue()).isCloseTo(new BigDecimal("30.7132"),offset);
		assertThat(ExpenseHelper.getTransactionIn(x).get(1).getAccountingValue()).isCloseTo(new BigDecimal("30.7132"),offset);
		
		x.updateAmountValues(null, precision);		
		assertThat(x.getAccountingValue()).isEqualTo("61.45");
		assertThat(ExpenseHelper.getTransactionOut(x).get(0).getCurrencyAmount()).isEqualTo("45.02");
		assertThat(ExpenseHelper.getTransactionOut(x).get(0).getAccountingValue()).isEqualTo("61.45");
		assertThat(ExpenseHelper.getTransactionIn(x).get(0).getCurrencyAmount()).isEqualTo("22.51");
		assertThat(ExpenseHelper.getTransactionIn(x).get(1).getCurrencyAmount()).isEqualTo("22.51");
		assertThat(ExpenseHelper.getTransactionIn(x).get(0).getAccountingValue()).isEqualTo("30.70");
		assertThat(ExpenseHelper.getTransactionIn(x).get(1).getAccountingValue()).isEqualTo("30.70");
				
		x.setCurrencyAmount(new BigDecimal("54.04"));
		x.updateAmountValues(new BigDecimal("45.02"),null);
		assertThat(x.getAccountingValue()).isEqualTo("73.3328");
		assertThat(ExpenseHelper.getTransactionOut(x).get(0).getCurrencyAmount()).isEqualTo("54.04");
		assertThat(ExpenseHelper.getTransactionOut(x).get(0).getAccountingValue()).isEqualTo("73.3328");
		assertThat(ExpenseHelper.getTransactionIn(x).get(0).getCurrencyAmount()).isEqualTo("22.51"); // they are not automatically updated
		assertThat(ExpenseHelper.getTransactionIn(x).get(1).getCurrencyAmount()).isEqualTo("22.51");
		assertThat(ExpenseHelper.getTransactionIn(x).get(0).getAccountingValue()).isCloseTo(new BigDecimal("30.54628660"),offset);
		assertThat(ExpenseHelper.getTransactionIn(x).get(1).getAccountingValue()).isCloseTo(new BigDecimal("30.54628660"),offset);
	}
}
