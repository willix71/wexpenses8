package w.expenses8.data.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.NumberExpression;

import w.expenses8.data.core.criteria.RangeCriteria;
import w.expenses8.data.core.criteria.RangeLocalDateCriteria;

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

	public static BooleanBuilder addLocalDateRange(BooleanBuilder predicate, RangeLocalDateCriteria range, DateTimePath<LocalDate> value) {
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

	
	public static String like(String txt) {
		if (txt.contains("*")) 
			return txt.replace('*', '%');
		else 
			return "%" + txt + "%";
	}
}
