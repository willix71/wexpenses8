package w.expenses8.data.dao.core;

public interface IUidableDao<T> {
	T findByUid(String uid);
}