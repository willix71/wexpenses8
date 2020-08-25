package w.expensesLegacy.data.domain.model;

public interface Klonable<T> extends Cloneable {

	T klone();
}
