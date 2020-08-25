package w.expenses8.data.core.dao;

public interface IUidableDao<T> {
	
	T findByUidStartsWith(String uid);

	T findByUidEndsWith(String uid);
	
	T findByUid(String uid);
}