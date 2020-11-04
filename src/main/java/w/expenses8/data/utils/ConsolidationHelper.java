package w.expenses8.data.utils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import w.expenses8.data.domain.model.Consolidation;
import w.expenses8.data.domain.model.TransactionEntry;
import w.expenses8.data.domain.model.enums.TransactionFactor;

public class ConsolidationHelper {

	public static void balanceConsolidation(Consolidation conso, List<TransactionEntry> entries) {
		long order = ((conso.getDate().getYear()*100) + conso.getDate().getMonthValue()) * 10000;
		if (conso.getOpeningValue()!=null && conso.getClosingValue()!=null) {
			BigDecimal closingValue = balanceConsolidationEntries(conso.getOpeningValue(), order, entries);
			conso.setDeltaValue(closingValue.subtract(conso.getClosingValue()));
		}
	}
	
	public static BigDecimal balanceConsolidationEntries(BigDecimal openingValue, long order, Collection<TransactionEntry> entries) {
		BigDecimal balance = openingValue;

		if (!CollectionHelper.isEmpty(entries)) {
			for(TransactionEntry entry: entries) {
				entry.setAccountingOrder(++order);
				if (entry.getFactor()==TransactionFactor.IN) {
					balance = balance.add(entry.getAccountingValue());
				} else if (entry.getFactor()==TransactionFactor.OUT) {
					balance = balance.subtract(entry.getAccountingValue());
				}
				entry.setAccountingBalance(balance);
			}
		}
		return balance;
    }

	public static void clearConsolidationEntries(Collection<TransactionEntry> entries) {
		if (!CollectionHelper.isEmpty(entries)) {
			for(TransactionEntry entry: entries) {
				entry.setAccountingOrder(null);
				entry.setAccountingBalance(null);
			}
		}
	}
	
	public static BigDecimal sum(Collection<TransactionEntry> entries) {
		return CollectionHelper.isEmpty(entries)?null:entries.stream().filter(l->l.getFactor()!=null).map(l->l.getAccountingValue().multiply(l.getFactor().getBigFactor())).filter(d->d!=null).reduce((l,r)->l==null?r:r==null?null:l.add(r)).orElse(BigDecimal.ZERO);
	}

	public static BigDecimal sum(Collection<TransactionEntry> entries, TransactionFactor factor) {
		return CollectionHelper.isEmpty(entries)?null:entries.stream().filter(l->l.getFactor()==factor).map(l->l.getAccountingValue()).filter(d->d!=null).reduce((l,r)->l==null?r:r==null?null:l.add(r)).orElse(BigDecimal.ZERO);
	}
}
