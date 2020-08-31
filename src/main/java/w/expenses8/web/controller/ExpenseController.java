package w.expenses8.web.controller;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import w.expenses8.data.domain.criteria.ExpenseCriteria;
import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.service.IExpenseService;

@Named
@ViewScoped
@SuppressWarnings("serial")
public class ExpenseController extends AbstractListController<Expense> {

	@Inject
	private IExpenseService expenseService;
	
	private ExpenseCriteria criteria = new ExpenseCriteria();
	
	public ExpenseCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ExpenseCriteria criteria) {
		this.criteria = criteria;
	}

	@Override
	protected void loadEntities() {
		elements = expenseService.findExpenses(criteria);
	}
}
