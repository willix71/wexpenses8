package w.expenses8.data.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberExpression;

import w.expenses8.data.core.criteria.RangeCriteria;
import w.expenses8.data.core.criteria.RangeLocalDateCriteria;
import w.expenses8.data.domain.model.QPayee;

public class CriteriaHelper {

	public static <T extends Comparable<T>> BooleanBuilder addRange(BooleanBuilder predicate, RangeCriteria<T> range, ComparableExpression<T> value) {
		if (range != null) {
			if (range.getFrom() != null) {
				predicate = predicate.and(value.gt(range.getFrom()).or(value.eq(range.getFrom())));
			}
			if (range.getTo() != null) {
				predicate = predicate.and(value.lt(range.getTo()));
			}
		}
		return predicate;
	}
	
	public static <T extends Number & Comparable<T>> BooleanBuilder addRange(BooleanBuilder predicate, RangeCriteria<T> range, NumberExpression<T> value) {
		if (range != null) {
			if (range.getFrom() != null) {
				predicate = predicate.and(value.gt(range.getFrom()).or(value.eq(range.getFrom())));
			}
			if (range.getTo() != null) {
				predicate = predicate.and(value.lt(range.getTo()));
			}
		}
		return predicate;
	}
	
	public static BooleanBuilder addLocalDateTimeRange(BooleanBuilder predicate, RangeLocalDateCriteria range, DateTimePath<LocalDateTime> value) {
		if (range != null) {
			if (range.getFrom() != null) {
				predicate = predicate.and(value.gt(range.getFrom().atStartOfDay()).or(value.eq(range.getFrom().atStartOfDay())));
			}
			if (range.getTo() != null) {
				predicate = predicate.and(value.lt(range.getTo().atStartOfDay()));
			}
		}
		return predicate;
	}

	public static BooleanBuilder addLocalDateRange(BooleanBuilder predicate, RangeLocalDateCriteria range, DatePath<LocalDate> value) {
		if (range != null) {
			if (range.getFrom() != null) {
				predicate = predicate.and(value.gt(range.getFrom()).or(value.eq(range.getFrom())));
			}
			if (range.getTo() != null) {
				predicate = predicate.and(value.lt(range.getTo()));
			}
		}
		return predicate;
	}

	public static Predicate getPayeeTextCriteria(QPayee payee,String text) {
		Predicate p = null;
		if (!StringHelper.isEmpty(text)) {
			String lowered = text.toLowerCase();
			if (lowered.startsWith("ccp:")) {
				String liked = CriteriaHelper.like(lowered.substring(4).trim());
				p = payee.postalAccount.lower().like(liked);				
			} else if (lowered.startsWith("iban:")) {
				String liked = CriteriaHelper.like(lowered.substring(5).trim());
				p = payee.iban.lower().like(liked);				
			} else {
				String liked = CriteriaHelper.like(lowered);
				p = payee.prefix.lower().like(liked).or(payee.name.lower().like(liked)).or(payee.extra.lower().like(liked)).or(payee.city.lower().like(liked));
			}
		}
		return p;
	}
	
	public static String like(String txt) {
		if (txt.contains("*")) 
			return txt.replace('*', '%');
		else 
			return "%" + txt + "%";
	}
	
	public static String safeLowerLike(String txt) {
		return txt==null?"*":like(txt.toLowerCase());
	}
	
}
