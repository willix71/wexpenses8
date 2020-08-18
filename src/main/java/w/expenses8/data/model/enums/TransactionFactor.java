package w.expenses8.data.model.enums;

public enum TransactionFactor {

	IN(1), SUM(0), OUT(-1);
	
	private int factor;
	
	private TransactionFactor(int factor) {
		this.factor = factor;
	}

	public int getFactor() {
		return factor;
	}

}
