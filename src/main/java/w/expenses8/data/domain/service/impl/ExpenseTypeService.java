package w.expenses8.data.domain.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.expenses8.data.core.service.GenericServiceImpl;
import w.expenses8.data.domain.dao.IExpenseTypeDao;
import w.expenses8.data.domain.model.ExpenseType;
import w.expenses8.data.domain.service.IExpenseTypeService;
import w.expenses8.data.utils.CriteriaHelper;
import w.expenses8.data.utils.StringHelper;

@Service
public class ExpenseTypeService extends GenericServiceImpl<ExpenseType, Long, IExpenseTypeDao> implements IExpenseTypeService {

	@Autowired
	public ExpenseTypeService(IExpenseTypeDao dao) {
		super(ExpenseType.class, dao);
	}
	
	@Override
	public ExpenseType findByName(String name) {
		return getDao().findByName(name);
	}
	
	@Override
	public List<ExpenseType> findBySelectable(String prefix) {
		if (StringHelper.isEmpty(prefix)) {
			return getDao().findBySelectable();
		} else {
			return getDao().findBySelectable(CriteriaHelper.like(prefix.toLowerCase()));
		}
	}
}
