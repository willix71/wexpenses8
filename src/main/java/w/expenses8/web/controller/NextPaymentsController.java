package w.expenses8.web.controller;

import java.time.LocalDate;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.service.IExpenseService;

@Slf4j
@Named
@ViewScoped
@Getter @Setter
public class NextPaymentsController {

	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private IExpenseService expenseService;
	
	public List<Expense> getExpensesToPay() {
		return expenseService.findExpensesToPay();
	}
	
	public void payed(Expense x) {
		log.info("Payed {}", x);
		x.setPayedDate(LocalDate.now());
		expenseService.save(x);
	}
}
