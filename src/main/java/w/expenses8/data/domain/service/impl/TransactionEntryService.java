package w.expenses8.data.domain.service.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.internal.util.collections.CollectionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
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
import w.expenses8.data.utils.StringHelper;

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
		if (criteria.getCurrencyCode()!=null) {
			predicate = predicate.and(entry.expense.currencyCode.equalsIgnoreCase(criteria.getCurrencyCode()));
		}
		if (criteria.getExpenseType()!=null) {
			predicate = predicate.and(QExpense.expense.expenseType.eq(criteria.getExpenseType()));
		}		
		if (criteria.getPayee()!=null) {
			predicate = predicate.and(QExpense.expense.payee.eq(criteria.getPayee()));
		}		
		if (!StringHelper.isEmpty(criteria.getPayeeText())) {
			String text = CriteriaHelper.like(criteria.getPayeeText().toLowerCase());
			QExpense ex = QExpense.expense;
			predicate = predicate.and(
			 ex.payee.prefix.lower().like(text).or(ex.payee.name.lower().like(text)).or(ex.payee.extra.lower().like(text)).or(ex.payee.city.lower().like(text)));
		}
		
		// TransactionEntry criteria
		predicate = CriteriaHelper.addRange(predicate, criteria.getCurrencyAmount(), entry.currencyAmount);
		predicate = CriteriaHelper.addRange(predicate, criteria.getAccountingValue(), entry.accountingValue);
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
			.leftJoin(entry.tags).fetchJoin()
			.where(predicate)
			.orderBy(new OrderSpecifier<>(Order.DESC, entry.accountingOrder));
		return query.fetch();
	}
	
	
}