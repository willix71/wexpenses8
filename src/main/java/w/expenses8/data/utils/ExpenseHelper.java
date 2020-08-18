package w.expenses8.data.utils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.model.ExpenseType;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.model.Tag;
import w.expenses8.data.domain.model.TransactionEntry;
import w.expenses8.data.domain.model.enums.TransactionFactor;

public class ExpenseHelper {

	public static Expense build(Object ...args) {
		Expense x = new Expense();
		for(Object o:args) {
			if (o instanceof Date) {
				x.setDate((Date) o);
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
					x.addTransaction(new TransactionEntry().setFactor(TransactionFactor.OUT).addTag(t));
				} else {
					x.addTransaction(new TransactionEntry().setFactor(TransactionFactor.IN).addTag(t));
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
			x.addTransaction(new TransactionEntry().setFactor(TransactionFactor.OUT));
			x.addTransaction(new TransactionEntry().setFactor(TransactionFactor.IN));	
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
		b.append(x.getDate()==null?"[date] ":new SimpleDateFormat("dd/MM/yyyy ").format(x.getDate()));
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
