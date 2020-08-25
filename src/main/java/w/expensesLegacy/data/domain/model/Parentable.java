package w.expensesLegacy.data.domain.model;

import java.util.List;

public interface Parentable<T> {

	@SuppressWarnings("unchecked")
	default T getRoot() {
		Parentable<T> parent = this;
		while(parent.getParent() != null) {
			parent = (Parentable<T>) parent.getParent();
		}
		return (T) parent;
	}
	
	T getParent();
	
	void setParent(T t);
	
	List<T> getChildren();
}
