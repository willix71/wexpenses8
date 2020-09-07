package w.expenses8.web.controller;

import java.time.Year;
import java.time.YearMonth;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.domain.criteria.ExpenseCriteria;
import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.service.IExpenseService;

@Slf4j
@Named
@ViewScoped
@Getter @Setter
public class ExpenseController extends AbstractListController<Expense> {

	private static final long serialVersionUID = 3351336696734127296L;

	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private IExpenseService expenseService;
	
	private ExpenseCriteria criteria = ExpenseCriteria.from(YearMonth.now().atDay(1));

	public void resetMonth() {
		criteria = ExpenseCriteria.from(YearMonth.now().atDay(1));
		loadEntities();
	}
	public void resetYear() {
		criteria = ExpenseCriteria.from(Year.now().atDay(1));
		loadEntities();
	}
	public void resetAll() {
		criteria = ExpenseCriteria.from(null);
		loadEntities();
	}
	
	@Override
	protected void loadEntities() {
		log.info("loading expenses with {}", criteria);
		elements = expenseService.findExpenses(criteria);
	}
}
