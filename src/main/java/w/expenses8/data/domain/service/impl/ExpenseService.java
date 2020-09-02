package w.expenses8.data.domain.service.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;

import lombok.var;
import w.expenses8.data.core.model.DBable;
import w.expenses8.data.core.service.GenericServiceImpl;
import w.expenses8.data.domain.criteria.ExpenseCriteria;
import w.expenses8.data.domain.dao.IExpenseDao;
import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.model.QExpense;
import w.expenses8.data.domain.model.QTransactionEntry;
import w.expenses8.data.domain.service.IExpenseService;
import w.expenses8.data.utils.CollectionHelper;
import w.expenses8.data.utils.CriteriaHelper;

@Service
public class ExpenseService extends GenericServiceImpl<Expense, Long, IExpenseDao> implements IExpenseService {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	public ExpenseService(IExpenseDao dao) {
		super(Expense.class, dao);
	}
	
	void persist(DBable<?> d) {
		if (d!=null && d.isNew()) {
			entityManager.persist(d);
		}
	}
	
	@Override
	public Expense reload(Expense expense) {
		if (expense==null) return null;
		var query = baseQuery().where(QExpense.expense.id.eq(expense.getId()));
		Expense x = query.fetchOne();
		return x;
	}

	@Override
	public Expense save(Expense x) {
		persist(x.getExpenseType());
		persist(x.getPayee());
		persist(x.getExchangeRate());
		CollectionHelper.stream(x.getTransactions()).forEach(te->CollectionHelper.stream(te.getTags()).forEach(tag->persist(tag)));
		return super.save(x);
	}
		
	@Override
	public List<Expense> findExpenses(ExpenseCriteria criteria) {
		QExpense ex = QExpense.expense;
		
		BooleanBuilder predicate = new BooleanBuilder();
		predicate = CriteriaHelper.addLocalDateTimeRange(predicate, criteria.getLocalDate(), ex.date);	

		if ("value".equalsIgnoreCase(criteria.getCurrencyCode())) {
			predicate = CriteriaHelper.addRange(predicate, criteria.getAmountValue(), ex.accountingValue);
		} else {
			predicate = CriteriaHelper.addRange(predicate, criteria.getAmountValue(), ex.currencyAmount);
			if (criteria.getCurrencyCode()!=null) {
				predicate = predicate.and(ex.currencyCode.equalsIgnoreCase(criteria.getCurrencyCode()));
			}			
		}
		
		if (criteria.getExpenseType()!=null) {
			predicate = predicate.and(ex.expenseType.eq(criteria.getExpenseType()));
		}
		if (criteria.getPayee()!=null) {
			predicate = predicate.and(ex.payee.eq(criteria.getPayee()));
		}
		
		Predicate payeeTextPredicate = CriteriaHelper.getPayeeTextCriteria(ex.payee, criteria.getPayeeText());
		if (payeeTextPredicate!=null) {
			predicate = predicate.and(payeeTextPredicate);
		}
		
		var query = baseQuery().where(predicate).orderBy(QExpense.expense.date.desc());
		return query.fetch();
		
	}
	private JPAQuery<Expense> baseQuery() {
		QExpense ex = QExpense.expense;
		var query = new JPAQuery<Expense>(entityManager);
		query.distinct().select(ex).from(ex)
			.leftJoin(ex.expenseType).fetchJoin()
			.leftJoin(ex.exchangeRate).fetchJoin()
			.leftJoin(ex.payee).fetchJoin()
			.leftJoin(ex.transactions, QTransactionEntry.transactionEntry).fetchJoin()
			.leftJoin(QTransactionEntry.transactionEntry.tags).fetchJoin()
			.leftJoin(QExpense.expense.documentFiles).fetchJoin();
		return query;
	} 
}
