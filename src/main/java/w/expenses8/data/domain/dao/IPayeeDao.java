package w.expenses8.data.domain.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import w.expenses8.data.core.dao.IGenericDao;
import w.expenses8.data.core.dao.IUidableDao;
import w.expenses8.data.domain.model.Payee;

public interface IPayeeDao extends IGenericDao<Payee, Long>, IUidableDao<Payee> {

	Payee findByName(String name);

	@Query("from Payee p where lower(p.name) like ?1 or lower(p.prefix) like ?1 or lower(p.extra) like ?1 or lower(p.city) like ?1")
	List<Payee> findByText(String text);
}
