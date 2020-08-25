package w.expensesLegacy.data.domain.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AccountPeriod extends Number implements Serializable, Comparable<AccountPeriod>, Klonable<AccountPeriod> {

	private static final long serialVersionUID = 1L;

	private static final List<String> MONTHS = Arrays.asList(null,
	      "JAN","FEB","MAR","APR","MAI","JUN","JUL","AUG","SEP","OCT",
	      "NOV","DEC", null, null, null, null, null, null, null, null,
	      "S1", "S2", null, null, null, null, null, null, null, null,
	      "T1", "T2", "T3", null, null, null, null, null, null, null,
	      "Q1", "Q2", "Q3", "Q4");
	
	private static final String[] MONTHS_SPACE = {"",
	   "JAN ","FEB ","MAR ","APR ","MAI ","JUN ","JUL ","AUG ","SEP ","OCT ",
	   "NOV ","DEC ", null, null, null, null, null, null, null, null,
      "S1 ", "S2 ", null, null, null, null, null, null, null, null,
      "T1 ", "T2 ", "T3 ", null, null, null, null, null, null, null,
      "Q1 ", "Q2 ", "Q3 ", "Q4 "};
	
	private int period;

	public AccountPeriod(int value) {
	   int mod = value % 100;
	   if (mod < 0 || mod >= MONTHS_SPACE.length || MONTHS_SPACE[mod] == null) {
	      throw new IllegalArgumentException("Illegal period "+value);
	   }
	   
		this.period = value;
	}
	
	@Override
	public double doubleValue() {
		return period;
	}

	@Override
	public float floatValue() {
		return period;
	}

	@Override
	public int intValue() {
		return period;
	}

	@Override
	public long longValue() {
		return period;
	}

	public boolean isYearly() {
	   return period % 100 == 0;	   
	}
	
   public boolean isMonthly() {
      int mod = period % 100;
      return mod > 0 && mod < 13;
   }
	 
   public boolean isQuaterly() {
      int mod = period % 100;
      return mod > 40 && mod < 45;
   }
   
   public AccountPeriod nextPeriod() {
      int mod = period % 100;
      switch(mod) {
         case 0: 
            return AccountPeriod.valueOf(period + 100);
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 21:
         case 31:
         case 32:
         case 41:
         case 42:
         case 43:
            return AccountPeriod.valueOf(period + 1);
         case 12: 
            return AccountPeriod.valueOf(period + 89);
         case 22:
            return AccountPeriod.valueOf(period + 99);
         case 33:
            return AccountPeriod.valueOf(period + 98);
         case 44:
            return AccountPeriod.valueOf(period + 97);
         default:
            throw new IllegalArgumentException("Illegal period "+period);
      }
   }
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public AccountPeriod klone() {
		try {
			return (AccountPeriod) clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Can not clone " + this.getClass().getName(), e);
		}
	}

	@Override
	public int compareTo(AccountPeriod other) {
		if (other==null) return -1;
		
		return other.period-period;
	}

	@Override
	public String toString() {
		return MONTHS_SPACE[period%100] + (period/100);
	}

	@Override
	public int hashCode() {
		return period;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccountPeriod other = (AccountPeriod) obj;
		return period == other.period;
	}
	
	public static AccountPeriod valueOf(Integer value) {
		if (value == null) return null;
		else return new AccountPeriod(value);
	}

	public static AccountPeriod valueOf(Double value) {
		if (value == null) return null;
		else return new AccountPeriod((int) (value.doubleValue() * 100));
	}
	
	public static AccountPeriod valueOf(String value) {
		if (value == null || value.length()==0) return null;
		
		value = value.trim();
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month =0;
		
		if (value.length()>0) {
			if (value.equals(".")) {
				month = c.get(Calendar.MONTH) + 1;
			} else if (value.contains(".")){
				String parts[] = value.split("\\.");
				if (parts[0].length()>0) year = Integer.parseInt(parts[0]);
				if (parts[1].length()>0) month = Integer.parseInt(parts[1]);
			} else if (value.contains(" ")) {
				String parts[] = value.split("\\s+");
				int index = MONTHS.indexOf(parts[0].toUpperCase());
				if (index > 0) {
					month = index;
				}
				year = Integer.parseInt(parts[1]);
			} else  {
				int index = MONTHS.indexOf(value.toUpperCase());
				if (index > 0) {
					month = index;
				} else {
					int period = Integer.parseInt(value);				
					if (period <100000) {
						year = period;
					} else {
						month = period % 100;
						year = period /100;
					}
				}
			}
			
			if (month < 0 || month > MONTHS_SPACE.length || MONTHS_SPACE[month] == null) {
				throw new IllegalArgumentException(String.format("Can't convert '%s' to an AccountPeriod", value));
			}
		}
		
		return new AccountPeriod(year*100 + month);
	}
}
