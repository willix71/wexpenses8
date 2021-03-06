package w.expenses8.data.domain.service.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.internal.util.collections.CollectionHelper;
import org.jboss.weld.exceptions.IllegalArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;

import lombok.var;
import w.expenses8.WexpensesConstants;
import w.expenses8.data.core.criteria.RangeLocalDateCriteria;
import w.expenses8.data.core.service.GenericServiceImpl;
import w.expenses8.data.domain.criteria.TagCriteria;
import w.expenses8.data.domain.criteria.TransactionEntryCriteria;
import w.expenses8.data.domain.dao.ITransactionEntryDao;
import w.expenses8.data.domain.model.Consolidation;
import w.expenses8.data.domain.model.QExpense;
import w.expenses8.data.domain.model.QTag;
import w.expenses8.data.domain.model.QTagGroup;
import w.expenses8.data.domain.model.QTransactionEntry;
import w.expenses8.data.domain.model.Tag;
import w.expenses8.data.domain.model.TagGroup;
import w.expenses8.data.domain.model.TransactionEntry;
import w.expenses8.data.domain.model.enums.TagType;
import w.expenses8.data.domain.service.ITransactionEntryService;
import w.expenses8.data.utils.CriteriaHelper;
import w.expenses8.data.utils.ExpenseHelper;
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
	public TransactionEntry reload(Object o) {
		if (o == null || o == WexpensesConstants.NEW_INSTANCE) return ExpenseHelper.buildTransactionEntry();
		var query = baseQuery(QTransactionEntry.transactionEntry);
		if (o instanceof TransactionEntry) {
			TransactionEntry x=(TransactionEntry)o;
			if (x.isNew()) {
				return x;
			}
			return query.where(QTransactionEntry.transactionEntry.id.eq(x.getId())).fetchOne();
		} else if (o instanceof Long) {
			return query.where(QTransactionEntry.transactionEntry.id.eq((Long)o)).fetchOne();
		} else if (o instanceof String) {
			String uid = (String)o;
			if (uid.contains(".")) {
				TransactionEntry xx = loadByUid(uid);
				if (xx==null) return null;
				uid = xx.getUid(); // get the full uid;
			}
			return query.where(QTransactionEntry.transactionEntry.uid.eq(uid)).fetchOne();
		} else {
			throw new IllegalArgumentException("Can't reload TransactionEntry from " + o);
		}
	}

	@Override
	public List<TransactionEntry> findTransactionEntries(TransactionEntryCriteria criteria) {
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
		if (!StringHelper.isEmpty(criteria.getDescription())) {
			if (StringHelper.hasUpperCase(criteria.getDescription())) {
				predicate = predicate.and(QExpense.expense.description.like(CriteriaHelper.like(criteria.getDescription())));
			} else {
				predicate = predicate.and(QExpense.expense.description.lower().like(CriteriaHelper.like(criteria.getDescription())));
			}
		}
		if (!StringHelper.isEmpty(criteria.getExternalReference())) {
			if (StringHelper.hasUpperCase(criteria.getExternalReference())) {
				predicate = predicate.and(QExpense.expense.externalReference.like(CriteriaHelper.like(criteria.getExternalReference())));
			} else {
				predicate = predicate.and(QExpense.expense.externalReference.lower().like(CriteriaHelper.like(criteria.getExternalReference())));
			}
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
		
		applyTagCriteria(entry, predicate, criteria.getTagCriterias());

		var query = baseQuery(entry).where(predicate).orderBy(entry.accountingDate.asc(), entry.accountingOrder.asc());
		return query.fetch();
	}
	
	@Override
	public List<TransactionEntry> findConsolidationEntries(Consolidation conso) {
		return getDao().findByConsolidation(conso);
	}

	@Override
	public List<TransactionEntry> findConsolidatableEntries(Consolidation conso, List<TagCriteria> tags, RangeLocalDateCriteria dateRange) {
		QTransactionEntry entry = QTransactionEntry.transactionEntry;
		BooleanBuilder predicate = new BooleanBuilder();
		if (conso==null || conso.isNew()) {
			predicate = predicate.and(entry.consolidation.isNull());			
		} else {
			predicate = predicate.and(entry.consolidation.isNull().or(entry.consolidation.eq(conso)));
		}
		predicate = CriteriaHelper.addLocalDateRange(predicate, dateRange, entry.accountingDate);
		applyTagCriteria(entry, predicate, tags);
		var query = baseQuery(entry).where(predicate).orderBy(entry.accountingDate.asc(), entry.accountingOrder.asc());
		return query.fetch();
	}

	private void applyTagCriteria(QTransactionEntry entry, BooleanBuilder predicate, List<TagCriteria> tags) {
		if (!CollectionHelper.isEmpty(tags)) {
			boolean not = false;
			for(TagCriteria t:tags) {
				if (t instanceof Tag) {
					predicate = not?
							predicate.andNot(entry.tags.contains((Tag) t)):
							predicate.and(entry.tags.contains((Tag) t));
				} else if (t instanceof TagGroup) {
					TagGroup group = (TagGroup) t;
					if (!group.isNew()) {
						QTag tt = new QTag("tt");
						QTagGroup tg = new QTagGroup("tg");
						SubQueryExpression<Tag> e= JPAExpressions.select(tt).from(tg).join(tg.tags, tt).where(tg.eq((TagGroup) t));
						predicate = not?
								predicate.andNot(entry.tags.any().in(e)):
								predicate.and(entry.tags.any().in(e));
					} else {
						// just in case, this code works for a new TagGroup but is sql is longer
						BooleanBuilder tagpredicate = new BooleanBuilder();
						for(Tag tt: ((TagGroup) t).getTags()) {
							predicate = tagpredicate.or(entry.tags.contains(tt));
						}
						predicate = not?
								predicate.andNot(tagpredicate):
								predicate.and(tagpredicate);
					}
				} else if (t instanceof TagType) {
					QTag qtag = new QTag("tt");
					SubQueryExpression<Tag> e= JPAExpressions.select(qtag).from(qtag).where(qtag.type.eq((TagType) t));
					predicate = not?
							predicate.andNot(entry.tags.any().in(e)):
							predicate.and(entry.tags.any().in(e));
				} else if (t==TagCriteria.NOT) {
					not = true;
					continue;
				}
				not = false;
			}
		}
	}
	
	private JPAQuery<TransactionEntry> baseQuery(QTransactionEntry entry) {
		var query = new JPAQuery<TransactionEntry>(entityManager);
		query.distinct().select(entry).from(entry)
			.leftJoin(entry.consolidation).fetchJoin()
			.leftJoin(entry.expense, QExpense.expense).fetchJoin()
			.leftJoin(QExpense.expense.payee).fetchJoin()
			.leftJoin(QExpense.expense.expenseType).fetchJoin()
			.leftJoin(QExpense.expense.documentFiles).fetchJoin()
			.leftJoin(entry.tags).fetchJoin();
		return query;
		
	}
}