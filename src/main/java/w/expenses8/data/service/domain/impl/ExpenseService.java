package w.expenses8.data.service.domain.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.LiteralExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;

import lombok.var;
import w.expenses8.data.criteria.core.RangeCriteria;
import w.expenses8.data.criteria.domain.ExpenseCriteria;
import w.expenses8.data.dao.domain.IExpenseDao;
import w.expenses8.data.model.domain.Expense;
import w.expenses8.data.model.domain.Payee;
import w.expenses8.data.model.domain.QExpense;
import w.expenses8.data.model.domain.QPayee;
import w.expenses8.data.service.core.GenericServiceImpl;
import w.expenses8.data.service.domain.IExpenseService;
import w.expenses8.data.service.domain.IPayeeService;

@Service
public class ExpenseService extends GenericServiceImpl<Expense, Long, IExpenseDao> implements IExpenseService {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private IPayeeService payeeService;
	
	@Autowired
	public ExpenseService(IExpenseDao dao) {
		super(Expense.class, dao);
	}

	@Override
	public Expense save(Expense entity) {
		if (entity.getPayee()!=null && entity.getPayee().isNew()) {
			entity.setPayee(payeeService.save(entity.getPayee()));
		}
		return super.save(entity);
	}

	private <T extends Comparable<T>> BooleanBuilder add(BooleanBuilder predicate, RangeCriteria<T> range, ComparableExpression<T> value) {
		if (range != null) {
			if (range.getFrom() != null) {
				predicate = predicate.and(value.gt(range.getFrom()).or(value.eq(range.getFrom())));
			}
			if (range.getTo() != null) {
				predicate = predicate.and(value.lt(range.getFrom()));
			}
		}
		return predicate;
	}
	private <T extends Number & Comparable<T>> BooleanBuilder add(BooleanBuilder predicate, RangeCriteria<T> range, NumberExpression<T> value) {
		if (range != null) {
			if (range.getFrom() != null) {
				predicate = predicate.and(value.gt(range.getFrom()).or(value.eq(range.getFrom())));
			}
			if (range.getTo() != null) {
				predicate = predicate.and(value.lt(range.getFrom()));
			}
		}
		return predicate;
	}
	
	@Override
	public List<Expense> findExpenses(ExpenseCriteria criteria) {
		IExpenseDao dao = getDao();
		
		BooleanBuilder predicate = new BooleanBuilder();
		predicate = add(predicate, criteria.getDate(), QExpense.expense.date);
		predicate = add(predicate, criteria.getCurrencyAmount(), QExpense.expense.currencyAmount);
		predicate = add(predicate, criteria.getAccountingValue(), QExpense.expense.accountingValue);

		if (criteria.getCurrencyCode()!=null) {
			predicate = predicate.and(QExpense.expense.currencyCode.equalsIgnoreCase(criteria.getCurrencyCode()));
		}
		if (criteria.getExpenseType()!=null) {
			predicate = predicate.and(QExpense.expense.expenseType.eq(criteria.getExpenseType()));
		}
		
		var query = new JPAQuery<Expense>(entityManager);
		query.from(QExpense.expense).where(predicate).orderBy(new OrderSpecifier<Date>(Order.DESC, QExpense.expense.date));
		return query.fetch();
	}

	@Override
	public List<Expense> findExpenses2(ExpenseCriteria criteria) {
		IExpenseDao dao = getDao();
		
		BooleanBuilder predicate = new BooleanBuilder();
		predicate = add(predicate, criteria.getDate(), QExpense.expense.date);
		predicate = add(predicate, criteria.getCurrencyAmount(), QExpense.expense.currencyAmount);
		predicate = add(predicate, criteria.getAccountingValue(), QExpense.expense.accountingValue);

		if (criteria.getCurrencyCode()!=null) {
			predicate = predicate.and(QExpense.expense.currencyCode.equalsIgnoreCase(criteria.getCurrencyCode()));
		}
		if (criteria.getExpenseType()!=null) {
			predicate = predicate.and(QExpense.expense.expenseType.eq(criteria.getExpenseType()));
		}
		
		var query = new JPAQuery<Expense>(entityManager);
		query.from(QExpense.expense).where(predicate).orderBy(new OrderSpecifier<Date>(Order.DESC, QExpense.expense.date));
		return query.fetch();
	}
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
