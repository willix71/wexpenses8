package w.expenses8.web.controller;

import java.time.LocalDateTime;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import w.expenses8.data.config.CurrencyValue;

@Named
@RequestScoped
@Getter
public class RequestController {

	@Inject
	private CurrencyValue defaultCurrency;
	
	public LocalDateTime time = LocalDateTime.now();
	
}
