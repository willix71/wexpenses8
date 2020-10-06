package w.expenses8.data.domain.service.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.internal.util.collections.CollectionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;

import lombok.var;
import w.expenses8.data.core.service.GenericServiceImpl;
import w.expenses8.data.domain.criteria.TransactionEntryCriteria;
import w.expenses8.data.domain.dao.ITransactionEntryDao;
import w.expenses8.data.domain.model.QExpense;
import w.expenses8.data.domain.model.QTransactionEntry;
import w.expenses8.data.domain.model.Tag;
import w.expenses8.data.domain.model.TransactionEntry;
import w.expenses8.data.domain.service.ITransactionEntryService;
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
		if (criteria.getExpenseType()!=null) {
			predicate = predicate.and(QExpense.expense.expenseType.eq(criteria.getExpenseType()));
		}		
		if (criteria.getPayee()!=null) {
			predicate = predicate.and(QExpense.expense.payee.eq(criteria.getPayee()));
		}		
		Predicate payeeTextPredicate = CriteriaHelper.getPayeeTextCriteria(QExpense.expense.payee, criteria.getPayeeText());
		if (payeeTextPredicate!=null) {
			predicate = predicate.and(payeeTextPredicate);
		}
		
		// TransactionEntry criteria
		predicate = CriteriaHelper.addLocalDateRange(predicate, criteria.getLocalDate(), entry.accountingDate);
		if ("value".equalsIgnoreCase(criteria.getCurrencyCode())) {
			predicate = CriteriaHelper.addRange(predicate, criteria.getAmountValue(), entry.accountingValue);
		} else {
			predicate = CriteriaHelper.addRange(predicate, criteria.getAmountValue(), entry.currencyAmount);
			if (criteria.getCurrencyCode()!=null) {
				predicate = predicate.and(entry.expense.currencyCode.equalsIgnoreCase(criteria.getCurrencyCode()));
			}			
		}

		if (criteria.getAccountingYear()!=null) {
			predicate = predicate.and(entry.accountingYear.eq(criteria.getAccountingYear()));
		}
		if (!CollectionHelper.isEmpty(criteria.getTags())) {
			for(Tag t: criteria.getTags()) {
				predicate = predicate.and(entry.tags.contains(t));
			}
		}

		var query = new JPAQuery<TransactionEntry>(entityManager);
		query.distinct().select(entry).from(entry)
			.leftJoin(entry.expense, QExpense.expense).fetchJoin()
			.leftJoin(QExpense.expense.payee).fetchJoin()
			.leftJoin(QExpense.expense.expenseType).fetchJoin()
			.leftJoin(QExpense.expense.documentFiles).fetchJoin()
			.leftJoin(entry.tags).fetchJoin()
			.where(predicate)
			.orderBy(entry.accountingDate.asc(), entry.accountingOrder.asc());
		return query.fetch();
	}
	
	
}