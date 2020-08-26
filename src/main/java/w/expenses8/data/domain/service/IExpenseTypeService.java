package w.expenses8.data.domain.service;

import java.util.List;

import w.expenses8.data.core.service.IGenericService;
import w.expenses8.data.domain.model.ExpenseType;

public interface IExpenseTypeService extends IGenericService<ExpenseType, Long> {

	ExpenseType findByName(String name);

	List<ExpenseType> findBySelectable(String prefix);
}
