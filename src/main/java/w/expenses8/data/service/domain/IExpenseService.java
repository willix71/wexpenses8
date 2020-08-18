package w.expenses8.data.service.domain;

import java.util.List;

import w.expenses8.data.criteria.domain.ExpenseCriteria;
import w.expenses8.data.model.domain.Expense;
import w.expenses8.data.service.core.IGenericService;

public interface IExpenseService extends IGenericService<Expense, Long> {
	
	List<Expense> findExpenses(ExpenseCriteria criteria);
	
	List<Expense> findExpenses2(ExpenseCriteria criteria);
}