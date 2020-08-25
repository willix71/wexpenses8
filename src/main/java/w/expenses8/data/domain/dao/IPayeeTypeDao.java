package w.expenses8.data.domain.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import w.expenses8.data.core.dao.IGenericDao;
import w.expenses8.data.core.dao.IUidableDao;
import w.expenses8.data.domain.model.PayeeType;

public interface IPayeeTypeDao extends IGenericDao<PayeeType, Long>, IUidableDao<PayeeType> {

	PayeeType findByName(String name);
	
	@Query("select distinct p from PayeeType p where p.selectable = true order by p.name")
	List<PayeeType> findBySelectable();
	
	@Query("select distinct p from PayeeType p where lower(p.name) like ?1 and p.selectable = true order by p.name")
	List<PayeeType> findBySelectable(String prefix);
}
