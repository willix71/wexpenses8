package w.expenses8.data.domain.service;

import java.util.List;

import w.expenses8.data.core.criteria.RangeLocalDateCriteria;
import w.expenses8.data.core.service.IGenericService;
import w.expenses8.data.domain.criteria.TagCriteria;
import w.expenses8.data.domain.criteria.TransactionEntryCriteria;
import w.expenses8.data.domain.model.TransactionEntry;

public interface ITransactionEntryService extends IGenericService<TransactionEntry, Long> {
	
	List<TransactionEntry> findTransactionEntrys(TransactionEntryCriteria criteria);

	List<TransactionEntry> findConsolidatableEntrys(List<TagCriteria> tags, RangeLocalDateCriteria dateRange);
}
