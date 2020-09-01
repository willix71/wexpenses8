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
import w.expenses8.data.utils.StringHelper;

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
		predicate = CriteriaHelper.addRange(predicate, criteria.getDate(), ex.date);
		predicate = CriteriaHelper.addRange(predicate, criteria.getCurrencyAmount(), ex.currencyAmount);
		predicate = CriteriaHelper.addRange(predicate, criteria.getAccountingValue(), ex.accountingValue);

		if (criteria.getCurrencyCode()!=null) {
			predicate = predicate.and(ex.currencyCode.equalsIgnoreCase(criteria.getCurrencyCode()));
		}
		if (criteria.getExpenseType()!=null) {
			predicate = predicate.and(ex.expenseType.eq(criteria.getExpenseType()));
		}
		if (criteria.getPayee()!=null) {
			predicate = predicate.and(ex.payee.eq(criteria.getPayee()));
		}
		if (!StringHelper.isEmpty(criteria.getPayeeText())) {
			String text = CriteriaHelper.like(criteria.getPayeeText().toLowerCase());
			predicate = predicate.and(
			 ex.payee.prefix.lower().like(text).or(ex.payee.name.lower().like(text)).or(ex.payee.extra.lower().like(text)).or(ex.payee.city.lower().like(text)));
		}
		
		//select ex from Expense ex left join fetch ex.expenseType left join fetch ex.exchangeRate left join fetch ex.payee left join fetch ex.transactions t join fetch t.tags
		var query = baseQuery().where(predicate).orderBy(new OrderSpecifier<Date>(Order.DESC, ex.date));
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
