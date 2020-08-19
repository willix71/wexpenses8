package w.expenses8.data.domain.service.impl;

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
	public Expense save(Expense x) {
		persist(x.getExpenseType());
		persist(x.getPayee());
		persist(x.getExchangeRate());
		CollectionHelper.stream(x.getTransactions()).forEach(te->CollectionHelper.stream(te.getTags()).forEach(tag->persist(tag)));
		return super.save(x);
	}
		
	@Override
	public List<Expense> findExpenses(ExpenseCriteria criteria) {
		BooleanBuilder predicate = new BooleanBuilder();
		predicate = CriteriaHelper.addRange(predicate, criteria.getDate(), QExpense.expense.date);
		predicate = CriteriaHelper.addRange(predicate, criteria.getCurrencyAmount(), QExpense.expense.currencyAmount);
		predicate = CriteriaHelper.addRange(predicate, criteria.getAccountingValue(), QExpense.expense.accountingValue);

		if (criteria.getCurrencyCode()!=null) {
			predicate = predicate.and(QExpense.expense.currencyCode.equalsIgnoreCase(criteria.getCurrencyCode()));
		}
		if (criteria.getExpenseType()!=null) {
			predicate = predicate.and(QExpense.expense.expenseType.eq(criteria.getExpenseType()));
		}
		if (criteria.getPayee()!=null) {
			predicate = predicate.and(QExpense.expense.payee.eq(criteria.getPayee()));
		}
		
		//select ex from Expense ex left join fetch ex.expenseType left join fetch ex.exchangeRate left join fetch ex.payee left join fetch ex.transactions t join fetch t.tags
		var query = new JPAQuery<Expense>(entityManager);
		query.distinct().select(QExpense.expense).from(QExpense.expense)
			.leftJoin(QExpense.expense.expenseType).fetchJoin()
			.leftJoin(QExpense.expense.exchangeRate).fetchJoin()
			.leftJoin(QExpense.expense.payee).fetchJoin()
			.leftJoin(QExpense.expense.transactions, QTransactionEntry.transactionEntry).fetchJoin()
			.leftJoin(QTransactionEntry.transactionEntry.tags).fetchJoin()
			.where(predicate)
			.orderBy(new OrderSpecifier<Date>(Order.DESC, QExpense.expense.date));
		return query.fetch();
		
	}

// return StreamSupport.stream(getDao().findAll(predicate, new OrderSpecifier<Date>(Order.DESC, QExpense.expense.date)).spliterator(),false).collect(Collectors.toList());
//        var qCity = QCity.city;
//
//        var query = new JPAQuery(entityManager);
//
//        query.from(qCity).where(qCity.name.eq("Bratislava")).distinct();
//        var c1 = query.fetch();
//
//        logger.info("{}", c1);
//
//        var query2 = new JPAQuery(entityManager);
//        query2.from(qCity).where(qCity.name.endsWith("est").and(qCity.population.lt(1800000)));
//        var cities = query2.fetch();
//
//        logger.info("{}", cities);
//
//        BooleanExpression booleanExpression = qCity.population.goe(2_000_000);
//        OrderSpecifier<String> orderSpecifier = qCity.name.asc();
//        var cities2 = cityRepository.findAll(booleanExpression, orderSpecifier);
//
//        logger.info("{}", cities2);
//    
}
