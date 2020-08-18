package w.expenses8.data.dao.domain;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import w.expenses8.data.dao.core.IGenericDao;
import w.expenses8.data.dao.core.IUidableDao;
import w.expenses8.data.model.domain.Expense;

public interface IExpenseDao extends IGenericDao<Expense, Long>, IUidableDao<Expense> {

    @Override
    @Query("select ex from Expense ex left join fetch ex.transactions left join fetch ex.exchangeRate left join fetch ex.payee")
    List<Expense> findAll();
}
