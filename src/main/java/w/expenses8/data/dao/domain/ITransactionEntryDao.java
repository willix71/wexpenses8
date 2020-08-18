package w.expenses8.data.dao.domain;

import w.expenses8.data.dao.core.IGenericDao;
import w.expenses8.data.dao.core.IUidableDao;
import w.expenses8.data.model.domain.TransactionEntry;

public interface ITransactionEntryDao  extends IGenericDao<TransactionEntry, Long>, IUidableDao<TransactionEntry> {

}
