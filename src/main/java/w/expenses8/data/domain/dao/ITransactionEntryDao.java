package w.expenses8.data.domain.dao;

import w.expenses8.data.core.dao.IGenericDao;
import w.expenses8.data.core.dao.IUidableDao;
import w.expenses8.data.domain.model.TransactionEntry;

public interface ITransactionEntryDao  extends IGenericDao<TransactionEntry, Long>, IUidableDao<TransactionEntry> {

}
