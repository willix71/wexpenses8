package w.expenses8.web.controller;

import java.time.LocalDateTime;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import w.expenses8.data.config.CurrencyValue;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.model.enums.PayeeDisplayer;

@Named
@RequestScoped
@Getter
public class RequestController {

	@Inject
	private CurrencyValue defaultCurrency;
	
	public LocalDateTime time = LocalDateTime.now();
	
	// this method is needed because we can't access a static method and PayeeDisplayer.DEFAULT is static
	public String displayPayee(Payee p) {
		return PayeeDisplayer.DEFAULT.display(p);
	}
}
