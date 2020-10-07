package w.expenses8.data.domain.service.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.internal.util.collections.CollectionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;

import lombok.var;
import w.expenses8.data.core.service.GenericServiceImpl;
import w.expenses8.data.domain.criteria.TagCriteria;
import w.expenses8.data.domain.criteria.TransactionEntryCriteria;
import w.expenses8.data.domain.dao.ITransactionEntryDao;
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
		if (!CollectionHelper.isEmpty(criteria.getTagCriterias())) {
			boolean not = false;
			for(TagCriteria t:criteria.getTagCriterias()) {
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