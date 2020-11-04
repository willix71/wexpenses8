package w.expenses8.data.domain.model.enums;

import java.math.BigDecimal;

public enum TransactionFactor {

	IN(1, BigDecimal.valueOf(1)), SUM(0, BigDecimal.ZERO), OUT(-1, BigDecimal.valueOf(-1));
	
	private final int factor;
	private final BigDecimal bigfactor;
	
	private TransactionFactor(int factor, BigDecimal bigfactor) {
		this.factor = factor;
		this.bigfactor = bigfactor;
	}

	public int getFactor() {
		return factor;
	}

	public BigDecimal getBigFactor() {
		return bigfactor;
	}
}
