package w.expenses8.data.domain.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import w.expenses8.data.core.dao.IGenericDao;
import w.expenses8.data.core.dao.IUidableDao;
import w.expenses8.data.domain.model.ExpenseType;

public interface IExpenseTypeDao extends IGenericDao<ExpenseType, Long>, IUidableDao<ExpenseType> {

	ExpenseType findByName(String name);
	
	@Query("select distinct p from ExpenseType p where p.selectable = true order by p.name")
	List<ExpenseType> findBySelectable();
	
	@Query("select distinct p from ExpenseType p where lower(p.name) like ?1 and p.selectable = true order by p.name")
	List<ExpenseType> findBySelectable(String prefix);
}
