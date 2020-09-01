package w.expenses8.data.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.NumberExpression;

import w.expenses8.data.core.criteria.RangeCriteria;

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

	public static <T extends Number & Comparable<T>> BooleanBuilder addDateRange(BooleanBuilder predicate, RangeCriteria<LocalDate> range, ComparableExpression<Date> value) {
		if (range != null) {
			if (range.getFrom() != null) {
				Date d =  Date.from(range.getFrom().atStartOfDay(ZoneId.systemDefault()).toInstant());
				predicate = predicate.and(value.gt(d).or(value.eq(d)));
			}
			if (range.getTo() != null) {
				Date d =  Date.from(range.getTo().atStartOfDay(ZoneId.systemDefault()).toInstant());
				predicate = predicate.and(value.lt(d));
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
