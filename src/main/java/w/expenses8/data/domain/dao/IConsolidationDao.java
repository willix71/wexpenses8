package w.expenses8.data.domain.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import w.expenses8.data.core.dao.IGenericDao;
import w.expenses8.data.core.dao.IUidableDao;
import w.expenses8.data.domain.model.Consolidation;

public interface IConsolidationDao  extends IGenericDao<Consolidation, Long>, IUidableDao<Consolidation> {

	@Query("select distinct c from Consolidation c left join fetch c.institution left join fetch c.documentFile left join fetch c.transactions te left join fetch te.tags left join fetch te.expense x left join fetch x.payee where c.id = ?1")
	Consolidation reload(Long id);
	
	@Override
	@Query("select distinct c from Consolidation c left join fetch c.institution left join fetch c.documentFile left join fetch c.openingEntry oe left join fetch oe.tags left join fetch c.closingEntry ce left join fetch ce.tags order by c.date desc")
	List<Consolidation> findAll();
}
