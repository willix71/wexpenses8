package w.expenses8.data.domain.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import w.expenses8.data.core.service.IGenericService;
import w.expenses8.data.domain.criteria.ExpenseCriteria;
import w.expenses8.data.domain.model.Expense;

public interface IExpenseService extends IGenericService<Expense, Long> {
	
	@Transactional
	Expense reload(Object o);
	
	List<Expense> findSimiliarExpenses(Expense x);

	List<Expense> findExpenses(ExpenseCriteria criteria);
}