package w.expenses8.data.domain.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.weld.exceptions.IllegalArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;

import lombok.var;
import w.expenses8.WexpensesConstants;
import w.expenses8.data.config.CurrencyValue;
import w.expenses8.data.core.criteria.RangeLocalDateCriteria;
import w.expenses8.data.core.model.DBable;
import w.expenses8.data.core.service.GenericServiceImpl;
import w.expenses8.data.domain.criteria.ExpenseCriteria;
import w.expenses8.data.domain.criteria.TagCriteria;
import w.expenses8.data.domain.dao.IExpenseDao;
import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.model.QExpense;
import w.expenses8.data.domain.model.QTag;
import w.expenses8.data.domain.model.QTransactionEntry;
import w.expenses8.data.domain.model.Tag;
import w.expenses8.data.domain.model.TagGroup;
import w.expenses8.data.domain.model.enums.PayeeDisplayer;
import w.expenses8.data.domain.model.enums.TagType;
import w.expenses8.data.domain.service.IExpenseService;
import w.expenses8.data.utils.CollectionHelper;
import w.expenses8.data.utils.CriteriaHelper;
import w.expenses8.data.utils.ExpenseHelper;
import w.expenses8.data.utils.StringHelper;
import w.expenses8.data.utils.ValidationHelper;

@Service
public class ExpenseService extends GenericServiceImpl<Expense, Long, IExpenseDao> implements IExpenseService {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private CurrencyValue currencyValue;
	
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
	public Expense reload(Object o) {
		if (o == null || o == WexpensesConstants.NEW_INSTANCE) return ExpenseHelper.build(currencyValue);
		var query = baseQuery(QExpense.expense);
		if (o instanceof Expense) {
			Expense x=(Expense)o;
			if (x.isNew()) {
				return x;
			}
			return query.where(QExpense.expense.id.eq(x.getId())).fetchOne();
		} else if (o instanceof Long) {
			return query.where(QExpense.expense.id.eq((Long)o)).fetchOne();
		} else if (o instanceof String) {
			String uid = (String)o;
			if (uid.contains(".")) {
				Expense xx = loadByUid(uid);
				if (xx==null) return null;
				uid = xx.getUid(); // get the full uid;
			}
			return query.where(QExpense.expense.uid.eq(uid)).fetchOne();
		} else {
			throw new IllegalArgumentException("Can't reload Expense from " + o);
		}
	}

	@Override
	public Expense save(Expense x) {
		// force the validation because if only the transactions have changed, the expense itself is not validated
		ValidationHelper.validate(x); 
		
		persist(x.getExpenseType());
		persist(x.getPayee());
		persist(x.getExchangeRate());
		CollectionHelper.stream(x.getTransactions()).forEach(te->CollectionHelper.stream(te.getTags()).forEach(tag->persist(tag)));
		CollectionHelper.stream(x.getDocumentFiles()).forEach(doc->persist(doc));
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
		
		if (criteria.getAccountingYear()!=null) {
			predicate = predicate.and(QTransactionEntry.transactionEntry.accountingYear.eq(criteria.getAccountingYear()));
		}
		if (!CollectionHelper.isEmpty(criteria.getTagCriterias())) {
			
			int i=0;
			boolean not = false;
			for(TagCriteria t:criteria.getTagCriterias()) {
				QTransactionEntry subte1 = new QTransactionEntry("subte"+(i++));
				var subquery = JPAExpressions.select(subte1.expense).from(subte1);
				if (t instanceof Tag) {
					subquery = subquery.where(subte1.tags.contains((Tag) t));
				} else if (t instanceof TagGroup) {
					subquery = subquery.where(subte1.tags.any().in(((TagGroup) t).getTags()));
				} else if (t instanceof TagType) {
					QTag ttag = new QTag("subtt"+(i++));
					var sub= JPAExpressions.select(ttag).from(ttag).where(ttag.type.eq((TagType) t));

					subquery = subquery.where(subte1.tags.any().in(sub));
				} else if (t==TagCriteria.NOT) {
					not = true;
					continue;
				} 
				predicate = not?predicate.and(ex.notIn(subquery)):predicate.and(ex.in(subquery));
				not = false;
			}
		}
		
		if (!StringHelper.isEmpty(criteria.getDescription())) {
			if (StringHelper.hasUpperCase(criteria.getDescription())) {
				predicate = predicate.and(ex.description.like(CriteriaHelper.like(criteria.getDescription())));
			} else {
				predicate = predicate.and(ex.description.lower().like(CriteriaHelper.like(criteria.getDescription())));
			}
		}
		if (!StringHelper.isEmpty(criteria.getExternalReference())) {
			if (StringHelper.hasUpperCase(criteria.getExternalReference())) {
				predicate = predicate.and(ex.externalReference.like(CriteriaHelper.like(criteria.getExternalReference())));
			} else {
				predicate = predicate.and(ex.externalReference.lower().like(CriteriaHelper.like(criteria.getExternalReference())));
			}
		}
		
		LOGGER.info("Predicate {}", predicate);
		var query = baseQuery(ex).where(predicate).orderBy(QExpense.expense.date.desc());
		return query.fetch();
		
	}
	
	@Override
	public Long findExpenseIdByTransactionEntryId(Long tid) {
		return getDao().findExpenseIdByTransactionEntryId(tid);
	}

	@Override
    public List<Expense> findSimiliarExpenses(Expense x) {
		QExpense ex = QExpense.expense;
		
		BooleanBuilder predicate = new BooleanBuilder().and(ex.currencyAmount.eq(x.getCurrencyAmount())).and(ex.currencyCode.eq(x.getCurrencyCode()));
		predicate = CriteriaHelper.addLocalDateTimeRange(predicate, new RangeLocalDateCriteria(x.getDate().toLocalDate(),x.getDate().toLocalDate().plusDays(1)), ex.date);
		if (!x.isNew()) {
			predicate = predicate.and(ex.id.ne(x.getId()));
		}
		
		var query = baseQuery(ex).where(predicate).orderBy(QExpense.expense.date.desc());
		return query.fetch();
    }
	
	@Override
	public List<Expense> findLatestExpenses() {
		QExpense ex = QExpense.expense;
		
		Date noOlderThan = Date.from(LocalDateTime.now().minusHours(24).atZone(ZoneId.systemDefault()).toInstant());
		BooleanBuilder predicate = new BooleanBuilder().and(ex.createdTs.gt(noOlderThan).or(ex.modifiedTs.gt(noOlderThan)));
		
		var query = baseQuery(ex).where(predicate).orderBy(QExpense.expense.date.desc());
		return query.fetch();
	}
	
	@Override
	public List<Expense> findExpensesToPay() {
		QExpense ex = QExpense.expense;
		
		BooleanBuilder predicate = new BooleanBuilder().and(ex.payedDate.isNull());
		predicate = predicate.and(ex.expenseType.displayer.ne(PayeeDisplayer.DEFAULT));
		predicate = CriteriaHelper.addLocalDateTimeRange(predicate, new RangeLocalDateCriteria(LocalDateTime.now().minusDays(30).toLocalDate(),null), ex.date);
		
		var query = baseQuery(ex).where(predicate).orderBy(QExpense.expense.date.asc());
		return query.fetch();
	}

	private JPAQuery<Expense> baseQuery(QExpense ex) {
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
