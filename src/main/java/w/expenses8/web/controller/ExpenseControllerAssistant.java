package w.expenses8.web.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.config.CurrencyValue;
import w.expenses8.data.domain.model.ExchangeRate;
import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.model.Payee;

@Slf4j
@Component
@Scope("prototype")
public class ExpenseControllerAssistant {

	private final CurrencyValue currencyValue;

	private Expense currentExpense;
	private LocalDateTime currentDate;
	private BigDecimal currentAmount;
	private Map<String,ExchangeRate> potentialExchangeRates = new HashMap<String, ExchangeRate>();
	
	@Autowired
	public ExpenseControllerAssistant(CurrencyValue currencyValue) {
		this.currencyValue = currencyValue;
		log.info("new expense controller assistant");
	}
	
	public void setCurrentExpense(Expense currentExpense) {
		this.potentialExchangeRates.clear();

		this.currentExpense = currentExpense;
		if (currentExpense != null) {
			this.currentDate = currentExpense.getDate();
			this.currentAmount = currentExpense.getCurrencyAmount();
			
			if (currentExpense.getExchangeRate()!=null) {
				this.potentialExchangeRates.put(currentExpense.getExchangeRate().getFromCurrencyCode(), currentExpense.getExchangeRate());
			}
		}
	}
	
	public void onCurrencyChange() {
		String newCurrencyCode = currentExpense.getCurrencyCode();
		log.debug("onCurrencyChange {}",newCurrencyCode);
		if (currencyValue.getCode().equals(newCurrencyCode)) {
			currentExpense.setExchangeRate(null);
		} else {
			ExchangeRate potentialExchangeRate = potentialExchangeRates.get(newCurrencyCode);
			if (potentialExchangeRate!=null) {
				currentExpense.setExchangeRate(potentialExchangeRate);
			} else {
				Payee institution = null; // lookup institution
				// new expense rate
				ExchangeRate newExchangeRate = ExchangeRate.with()
						.institution(institution)
						.date(currentExpense.getDate().toLocalDate())
						.fromCurrencyCode(newCurrencyCode)
						.toCurrencyCode(currencyValue.getCode())
						.rate(1.2)
						.build();
				
				potentialExchangeRates.put(newCurrencyCode, newExchangeRate);
				currentExpense.setExchangeRate(newExchangeRate);
			}
		}
	}
	
	public void onDateChange() {
		log.debug("onDateChange old {} new {}",currentDate, currentExpense.getDate());
		currentExpense.updateDate(currentDate);
		currentDate = currentExpense.getDate();
	}
	
	public void onAmountChange() {
		log.info("onAmountChange old {} new {}",currentAmount, currentExpense.getCurrencyAmount());
		currentExpense.updateAmountValues(currentAmount, currencyValue.getPrecision());
		currentAmount = currentExpense.getCurrencyAmount();
	}
}
