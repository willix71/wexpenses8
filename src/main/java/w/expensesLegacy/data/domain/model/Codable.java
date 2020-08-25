package w.expensesLegacy.data.domain.model;

import java.io.Serializable;

public interface Codable<T extends Codable<T>> extends Serializable, Klonable<T> {
	
	String getCode();
	
	String getName();
	
}
