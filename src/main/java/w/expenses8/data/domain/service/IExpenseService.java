package w.expenses8.data.domain.service;

import java.util.List;

import w.expenses8.data.core.service.IGenericService;
import w.expenses8.data.core.service.IReloadableService;
import w.expenses8.data.domain.criteria.ExpenseCriteria;
import w.expenses8.data.domain.model.Expense;

public interface IExpenseService extends IGenericService<Expense, Long>, IReloadableService<Expense> {
	
	Long findExpenseIdByTransactionEntryId(Long tid);
	
	List<Expense> findSimiliarExpenses(Expense x);

	List<Expense> findExpenses(ExpenseCriteria criteria);

	List<Expense> findLatestExpenses();

	List<Expense> findExpensesToPay();
}