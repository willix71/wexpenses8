package w.expenses8.data.service.domain.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;

import lombok.var;
import w.expenses8.data.criteria.domain.TransactionEntryCriteria;
import w.expenses8.data.dao.domain.ITransactionEntryDao;
import w.expenses8.data.model.domain.QExpense;
import w.expenses8.data.model.domain.QTransactionEntry;
import w.expenses8.data.model.domain.TransactionEntry;
import w.expenses8.data.service.core.GenericServiceImpl;
import w.expenses8.data.service.domain.ITransactionEntryService;
import w.expenses8.data.utils.CriteriaHelper;

@Service
public class TransactionEntryService extends GenericServiceImpl<TransactionEntry, Long, ITransactionEntryDao> implements ITransactionEntryService {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	public TransactionEntryService(ITransactionEntryDao dao) {
		super(TransactionEntry.class, dao);
	}

	@Override
	public List<TransactionEntry> findTransactionEntrys(TransactionEntryCriteria criteria) {
		QTransactionEntry entry = QTransactionEntry.transactionEntry;
		BooleanBuilder predicate = new BooleanBuilder();
		
		// expense criteria
		predicate = CriteriaHelper.addRange(predicate, criteria.getDate(), entry.expense.date);
		predicate = CriteriaHelper.addRange(predicate, criteria.getCurrencyAmount(), entry.currencyAmount);
		predicate = CriteriaHelper.addRange(predicate, criteria.getAccountingValue(), entry.accountingValue);

		if (criteria.getCurrencyCode()!=null) {
			predicate = predicate.and(entry.expense.currencyCode.equalsIgnoreCase(criteria.getCurrencyCode()));
		}
		if (criteria.getExpenseType()!=null) {
			predicate = predicate.and(QExpense.expense.expenseType.eq(criteria.getExpenseType()));
		}		

		// TransactionEntry criteria
		if (criteria.getAccountingYear()!=null) {
			predicate = predicate.and(entry.accountingYear.eq(criteria.getAccountingYear()));
		}
		if (criteria.getTag()!=null) {
			predicate = predicate.and(entry.tags.contains(criteria.getTag()));
		}
		
		var query = new JPAQuery<TransactionEntry>(entityManager);
		query.distinct().select(entry).from(entry)
			.leftJoin(entry.expense).fetchJoin()
			.leftJoin(entry.expense.exchangeRate).fetchJoin()
			.leftJoin(entry.expense.payee).fetchJoin()
			.leftJoin(entry.tags).fetchJoin()
			.where(predicate)
			.orderBy(new OrderSpecifier<Date>(Order.DESC, entry.expense.date));
		return query.fetch();
	}
	
	
}