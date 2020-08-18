package w.expenses8.data.dao.domain;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import w.expenses8.data.dao.core.IGenericDao;
import w.expenses8.data.dao.core.IUidableDao;
import w.expenses8.data.model.domain.Payee;

public interface IPayeeDao extends IGenericDao<Payee, Long>, IUidableDao<Payee> {

	Payee findByName(String name);

	@Query("from Payee p where p.name like ?1 or p.prefix like ?1 or p.extra like ?1 or p.city like ?1")
	List<Payee> findByText(String text);
}
