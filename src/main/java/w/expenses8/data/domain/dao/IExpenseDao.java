package w.expenses8.data.domain.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import w.expenses8.data.core.dao.IGenericDao;
import w.expenses8.data.core.dao.IUidableDao;
import w.expenses8.data.domain.model.Expense;

public interface IExpenseDao extends IGenericDao<Expense, Long>, IUidableDao<Expense> {

    @Override
    @Query("select distinct ex from Expense ex left join fetch ex.expenseType left join fetch ex.exchangeRate left join fetch ex.payee left join fetch ex.transactions t left join fetch t.tags")
    List<Expense> findAll();
    
    @Query("select ex.id from Expense ex left join ex.transactions t where t.id = ?1")
    Long findExpenseIdByTransactionEntryId(Long tid);
}
