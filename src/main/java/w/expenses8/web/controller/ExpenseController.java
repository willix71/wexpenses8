package w.expenses8.web.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

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
@SuppressWarnings("serial")
public class ExpenseController implements Serializable {

	@Inject
	private IExpenseService expenseService;
	
	private ExpenseCriteria criteria = new ExpenseCriteria();
	private List<Expense> expenses;
	private Expense selectedExpense;
    
	public List<Expense> getExpenses() {
		if (expenses == null) {
			refresh();
		}
		return expenses;
	}
	
	public void refresh() {
		log.debug("Resetting expenses matching {}", criteria);
		expenses = expenseService.findExpenses(criteria);
	}
	
	public void reset() {
		log.debug("Resetting expense criteria");
		criteria = new ExpenseCriteria();
		refresh();
	}
	
	public void save() {
		int index = expenses.indexOf(selectedExpense);
		Expense newP = expenseService.save(selectedExpense);
		if (index<0) {
			expenses.add(newP);
		} else {
			expenses.set(index, newP);
		}
	}

	public void newExpense() {
		selectedExpense = new Expense();
	}
	
	public void delete() {
		int index = expenses.indexOf(selectedExpense);
		expenseService.delete(selectedExpense);
		expenses.remove(index);
	}
}
