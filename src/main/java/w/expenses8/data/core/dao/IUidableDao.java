package w.expenses8.data.core.dao;

public interface IUidableDao<T> {
	T findByUid(String uid);
}