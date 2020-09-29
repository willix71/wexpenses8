package w.expenses8.data.utils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.function.Function;

import w.expenses8.data.domain.model.TransactionEntry;
import w.expenses8.data.domain.model.enums.TransactionFactor;

public class TransactionsSums {

	public class TransactionsSum {
		private final Function<TransactionEntry, BigDecimal> mapper;

		private BigDecimal delta = BigDecimal.ZERO;		
		private BigDecimal sumIn = BigDecimal.ZERO;
		private BigDecimal sumOut = BigDecimal.ZERO;
		
		public TransactionsSum(Function<TransactionEntry, BigDecimal> mapper) {
			this.mapper = mapper;
		}
		
		public TransactionsSum compute(Collection<TransactionEntry> lines) {
			delta = lines.stream().filter(l->!l.isSystemEntry()).map(l->{
				BigDecimal d = mapper.apply(l);
				return d==null?BigDecimal.ZERO:d.multiply(new BigDecimal(l.getFactor().getFactor()));
			}).reduce((l,r)->l==null?r:r==null?null:l.add(r)).orElse(BigDecimal.ZERO);
			sumIn = lines.stream().filter(l->!l.isSystemEntry() && l.getFactor()==TransactionFactor.IN).map(mapper).filter(d->d!=null).reduce((l,r)->l==null?r:r==null?null:l.add(r)).orElse(BigDecimal.ZERO);
			sumOut = lines.stream().filter(l->!l.isSystemEntry() && l.getFactor()==TransactionFactor.OUT).map(mapper).filter(d->d!=null).reduce((l,r)->l==null?r:r==null?null:l.add(r)).orElse(BigDecimal.ZERO);
			return this;
		}

		public BigDecimal getDelta() {
			return delta;
		}

		public BigDecimal getSumIn() {
			return sumIn;
		}

		public BigDecimal getSumOut() {
			return sumOut;
		}
		
		public boolean isValid() {
			return BigDecimal.ZERO.compareTo(delta) == 0;
		}
	}
	
	private TransactionsSum currencyAmountSums = new TransactionsSum(t->t.getCurrencyAmount());
	private TransactionsSum accountingValueSums = new TransactionsSum(t->t.getAccountingValue());
	
	public TransactionsSum getCurrencyAmountSums() {
		return currencyAmountSums;
	}
	public TransactionsSum getAccountingValueSums() {
		return accountingValueSums;
	}
	
	public TransactionsSums compute(Collection<TransactionEntry> lines) {
		this.currencyAmountSums.compute(lines);
		this.accountingValueSums.compute(lines);
		return this;
	}
	
	public boolean isValid() {
		return this.currencyAmountSums.isValid() && this.accountingValueSums.isValid();
	}
}
