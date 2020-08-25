package w.expensesLegacy.data.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class AmountValue extends Number implements Serializable, Klonable<AmountValue> {

	private static final long serialVersionUID = 1L;

	private static final long PRECISION = 100L;
	
	private long value = 0;
	
	private AmountValue() {}
	
	public AmountValue(String s) {
		setRealValue(Double.parseDouble(s));
	}	
		
	// ===
	
	private void setValue(long value) {
		this.value = value;
	}
	
	public long getValue() {
		return value;
	}
		
	private void setRealValue(double d) {
		this.value = (long) (d*PRECISION);
	}
	
	public double getRealValue() {
		return ((double) value)/PRECISION;
	}
	
	private void setBigValue(BigDecimal b) {
		setRealValue(b.doubleValue());
	}
	
	public BigDecimal getBigValue() {		
		return new BigDecimal(new DecimalFormat("0.00").format(getRealValue()));
	}

	// ===	

	@Override
	public int intValue() {
		return (int) getRealValue();
	}

	@Override
	public long longValue() {
		return (long) getRealValue();
	}

	@Override
	public float floatValue() {
		return (float) getRealValue();
	}

	@Override
	public double doubleValue() {
		return getRealValue();
	}
	
	// ===
	
	@Override
	public int hashCode() {
		return (int) value;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		return value == ((AmountValue) obj).value;
	}
	
	@Override
	public String toString() {		
		return String.valueOf(getRealValue());
	}	
	
   @Override
   public AmountValue klone() {
	   try {
	   	return (AmountValue) clone();
	   }
	   catch(CloneNotSupportedException e) {
	   	throw new RuntimeException("Can not clone " + this.getClass().getName(), e);
	   }
   }
	
	@Override
   protected Object clone() throws CloneNotSupportedException {
	   return super.clone();
   }
	

	public static AmountValue fromValue(long l) {
		AmountValue v = new AmountValue();
		v.setValue(l);
		return v;
	}
	
	public static AmountValue fromRealValue(double d) {
		AmountValue v = new AmountValue();
		v.setRealValue(d);
		return v;
	}
	
	public static AmountValue fromBigValue(BigDecimal d) {
		AmountValue v = new AmountValue();
		v.setBigValue(d);
		return v;
	}
}
