package w.expenses8.data.utils;

import java.math.BigDecimal;
import java.util.Date;

import w.expenses8.data.model.domain.Expense;
import w.expenses8.data.model.domain.ExpenseType;
import w.expenses8.data.model.domain.Payee;
import w.expenses8.data.model.domain.Tag;
import w.expenses8.data.model.domain.TransactionEntry;
import w.expenses8.data.model.enums.TransactionFactor;

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
					x.addTransaction(TransactionEntry.with().factor(TransactionFactor.OUT).build().addTag(t));
				} else {
					x.addTransaction(TransactionEntry.with().factor(TransactionFactor.IN).build().addTag(t));
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
}
