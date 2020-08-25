package w.expensesLegacy.data.domain.model;

public interface Duplicatable<T> extends Klonable<T> {

	T duplicate();
}
