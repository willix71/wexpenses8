package w.expenses8.data.service.domain;

import java.util.List;

import w.expenses8.data.criteria.domain.TransactionEntryCriteria;
import w.expenses8.data.model.domain.TransactionEntry;
import w.expenses8.data.service.core.IGenericService;

public interface ITransactionEntryService extends IGenericService<TransactionEntry, Long> {
	
	List<TransactionEntry> findTransactionEntrys(TransactionEntryCriteria criteria);
}
