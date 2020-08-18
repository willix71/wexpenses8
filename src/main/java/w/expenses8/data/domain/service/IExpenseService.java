package w.expenses8.data.domain.service;

import java.util.List;

import w.expenses8.data.core.service.IGenericService;
import w.expenses8.data.domain.criteria.ExpenseCriteria;
import w.expenses8.data.domain.model.Expense;

public interface IExpenseService extends IGenericService<Expense, Long> {
	
	List<Expense> findExpenses(ExpenseCriteria criteria);
}