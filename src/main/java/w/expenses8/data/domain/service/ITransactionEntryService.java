package w.expenses8.data.domain.service;

import java.util.List;

import w.expenses8.data.core.criteria.RangeLocalDateCriteria;
import w.expenses8.data.core.service.IGenericService;
import w.expenses8.data.core.service.IReloadableService;
import w.expenses8.data.domain.criteria.TagCriteria;
import w.expenses8.data.domain.criteria.TransactionEntryCriteria;
import w.expenses8.data.domain.model.Consolidation;
import w.expenses8.data.domain.model.TransactionEntry;

public interface ITransactionEntryService extends IGenericService<TransactionEntry, Long>, IReloadableService<TransactionEntry> {
	
	List<TransactionEntry> findTransactionEntries(TransactionEntryCriteria criteria);

	List<TransactionEntry> findConsolidationEntries(Consolidation conso);
	
	List<TransactionEntry> findConsolidatableEntries(List<TagCriteria> tags, RangeLocalDateCriteria dateRange);
}
