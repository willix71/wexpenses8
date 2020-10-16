package w.expenses8.data.utils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import w.expenses8.data.core.model.DBable;
import w.expenses8.data.domain.model.DocumentFile;
import w.expenses8.data.domain.model.ExchangeRate;
import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.model.ExpenseType;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.model.Tag;
import w.expenses8.data.domain.model.TransactionEntry;
import w.expenses8.data.domain.model.enums.TransactionFactor;

public class ExpenseHelper {

	@SuppressWarnings({ "unchecked"})
	public static Expense build(Object ...args) {
		Expense x = new Expense();
		for(Object o:args) {
			if (o==null) continue;
			if (o.getClass().equals(DBable.class)) {
				DBable<?> db = (DBable<?>) o;
				x.setId(db.getId());
				x.setUid(db.getUid());
				x.setCreatedTs(db.getCreatedTs());
				x.setModifiedTs(db.getModifiedTs());
				continue;
			}
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
			if (o instanceof ExchangeRate) {
				x.setExchangeRate((ExchangeRate) o);
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
			if (o instanceof DocumentFile) {
				x.addDocumentFile((DocumentFile) o);
				continue;
			}
			if (o instanceof Collection) {
				Object first = CollectionHelper.first((Collection<?>) o);
				if (first != null) {
					if (first instanceof TransactionEntry) {
						x.setTransactions(new HashSet<>((Collection<TransactionEntry>) o));
					} else if (first instanceof DocumentFile) {
						x.setDocumentFiles(new HashSet<>((Collection<DocumentFile>) o));
					}
				}
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
		x.updateValues(null);
		
		return x;
	}
	
	@SuppressWarnings({ "unchecked"})
	public static TransactionEntry buildTransactionEntry(Object ...args) {
		TransactionEntry x = new TransactionEntry();
		for(Object o:args) {
			if (o==null) continue;
			if (o.getClass().equals(DBable.class)) {
				DBable<?> db = (DBable<?>) o;
				x.setId(db.getId());
				x.setUid(db.getUid());
				x.setCreatedTs(db.getCreatedTs());
				x.setModifiedTs(db.getModifiedTs());
				continue;
			}
			if (o instanceof Date) {
				x.setAccountingDate(((Date) o).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
				continue;
			}
			if (o instanceof LocalDate) {
				x.setAccountingDate((LocalDate) o);
				continue;
			}
			if (o instanceof LocalDateTime) {
				x.setAccountingDate(((LocalDateTime) o).toLocalDate());
				continue;
			}
			if (o instanceof Integer) {
				x.setAccountingYear((Integer) o);
				continue;
			}
			if (o instanceof Expense) {
				x.setExpense((Expense) o);
				continue;
			}
			if (o instanceof BigDecimal) {
				x.setCurrencyAmount((BigDecimal) o);
				continue;
			}
			if (o instanceof TransactionFactor) {
				x.setFactor((TransactionFactor) o);
				continue;
			}			
			if (o instanceof Tag) {
				x.addTag((Tag) o);
				continue;
			}			
			if (o instanceof Collection) {
				Object first = CollectionHelper.first((Collection<?>) o);
				if (first != null && first instanceof Tag) {
					x.setTags(new HashSet<>((Collection<Tag>) o));
				}				
				continue;
			}			
			if (o instanceof DocumentFile) {
				x.setConsolidationFile((DocumentFile) o);
				continue;
			}
			throw new IllegalArgumentException("Don't know what to do with " + o.getClass().getName());
		}
		return x;
	}
	
	public static List<TransactionEntry> getTransactionEntry(Expense x, TransactionFactor factor) {
		return x.getTransactions().stream().filter(t->t.getFactor()==factor).collect(Collectors.toList());
	}
	
	public static List<TransactionEntry> getTransactionIn(Expense x) {
		return getTransactionEntry(x,TransactionFactor.IN);
	}
	
	public static List<TransactionEntry> getTransactionOut(Expense x) {
		return getTransactionEntry(x,TransactionFactor.OUT);
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
