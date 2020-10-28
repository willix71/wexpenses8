package w.expenses8.data.domain.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import w.expenses8.data.core.dao.IGenericDao;
import w.expenses8.data.core.dao.IUidableDao;
import w.expenses8.data.domain.model.Consolidation;
import w.expenses8.data.domain.model.TransactionEntry;

public interface ITransactionEntryDao  extends IGenericDao<TransactionEntry, Long>, IUidableDao<TransactionEntry> {

	@Query("select distinct t from TransactionEntry t left join fetch t.consolidation left join fetch t.expense e left join fetch e.payee left join fetch t.tags where t.consolidation = ?1 order by t.accountingOrder")
	List<TransactionEntry> findByConsolidation(Consolidation consolidation);
}
