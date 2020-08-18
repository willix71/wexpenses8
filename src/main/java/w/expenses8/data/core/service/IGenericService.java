package w.expenses8.data.core.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

public interface IGenericService <T, ID extends Serializable> {

	Class<T> getEntityClass();
	
	T newInstance(Object ... args);

	T load(ID id);

	T loadByUid(String uid);
	
	long count();
	
	List<T> loadAll();	
	
	@Transactional
	T save(T entity);

	@Transactional
	void delete(T entity);

	@Transactional
	void delete(ID id);

}
