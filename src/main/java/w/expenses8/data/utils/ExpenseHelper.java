package w.expenses8.data.utils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.stream.Collectors;

import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.model.ExpenseType;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.model.Tag;
import w.expenses8.data.domain.model.TransactionEntry;

public class ExpenseHelper {

	public static Expense build(Object ...args) {
		Expense x = new Expense();
		for(Object o:args) {
			if (o instanceof Date) {
				x.setDate(((Date) o).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
				continue;
			}
			if (o instanceof LocalDate) {
				x.setDate(((LocalDate) o).atStartOfDay());
				continue;
			}
			if (o instanceof LocalDateTime) {
				x.setDate((LocalDateTime) o);
				continue;
			}
			if (o instanceof ExpenseType) {
				x.setExpenseType((ExpenseType) o);
				continue;
			}
			if (o instanceof BigDecimal) {
				x.setCurrencyAmount((BigDecimal) o);
				continue;
			}
			if (o instanceof Payee) {
				x.setPayee((Payee) o);
				continue;
			}
			if (o instanceof TransactionEntry) {
				x.addTransaction((TransactionEntry) o);
				continue;
			}
			if (o instanceof Tag) {
				Tag t = (Tag) o;
				if (x.getTransactions()==null || x.getTransactions().isEmpty()) {
					x.addTransaction(TransactionEntry.out(t));
				} else {
					x.addTransaction(TransactionEntry.in(t));
				}
				continue;
			}
			if (o instanceof String) {
				String v = (String) o;
				if (v.matches("[A-Z]{3}")) {
					x.setCurrencyCode(v);
				} else {
					x.setDescription(v);
				}
				continue;
			}
			throw new IllegalArgumentException("Don't know what to do with " + o.getClass().getName());
		}
		
		if (x.getTransactions()==null || x.getTransactions().isEmpty()) {
			x.addTransaction(TransactionEntry.out());
			x.addTransaction(TransactionEntry.in());	
		}
		x.updateValues();
		
		return x;
	}
	
	public static String toString(Expense x) {
		StringBuilder b = new StringBuilder();
		if (x.isNew()) {
			b.append("[new] ");
		} else {
			b.append("id:").append(x.getId()+"."+x.getVersion()).append(" ");
		}
		b.append("uid:").append(x.getUid());
		b.append(x.getExpenseType()==null?"[type]":x.getExpenseType().getName()).append(" ");
		b.append(x.getDate()==null?"[date] ":x.getDate().format(DateTimeFormatter.ofPattern(("dd/MM/yyyy "))));
		b.append(MessageFormat.format("{0,number,0.00}{1} ", x.getCurrencyAmount(), x.getCurrencyCode()));
		if (x.getExchangeRate()!=null) {
			b.append(MessageFormat.format("{0,number,0.00000} ",x.getExchangeRate().getRate()));
		}
		if (x.getPayee()!=null) {
			b.append(x.getPayee().getName());
		}
		b.append(x.getTransactions().stream().map(t->t.getTags()).collect(Collectors.toList()));
		return b.toString();
	}
	
	
}
