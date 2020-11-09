package w.expenses8.data.domain.service;

import w.expenses8.data.core.service.IGenericService;
import w.expenses8.data.domain.model.ExchangeRate;
import w.expenses8.data.domain.model.Expense;

public interface IExchangeRateService extends IGenericService<ExchangeRate, Long> {

	ExchangeRate copyExchangeRate(ExchangeRate x);
	
	ExchangeRate buildExchangeRate(Expense x);
}
