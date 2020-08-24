package w.expenses8.data.core.service;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import w.expenses8.data.core.dao.IGenericDao;
import w.expenses8.data.core.dao.IUidableDao;

public class GenericServiceImpl <T, ID extends Serializable, D extends IGenericDao<T, ID>> implements IGenericService<T, ID> {

	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private final D dao;

	private final Class<T> entityClass;

	public GenericServiceImpl(Class<T> entityClass, D dao) {
		this.entityClass = entityClass;
		this.dao = dao;
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public D getDao() {
		return dao;
	}

	@Override
	public T newInstance(Object... args) {
		try {
			T t = entityClass.newInstance();
			return t;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public T load(ID id) {
		return dao.findById(id).orElse(null);
	}

	@Override
	public T loadByUid(String uid) {
		if (dao instanceof IUidableDao) {
			@SuppressWarnings("unchecked")
			IUidableDao<T> idBableJpaDao = (IUidableDao<T>) dao;
			return idBableJpaDao.findByUid(uid);
		}
		return null;
	}
	
	@Override
	public long count() {
		return dao.count();
	}

	@Override
	public List<T> loadAll() {
		return dao.findAll();
	}


	@Override
	@Transactional
	public T save(T entity) {
		return dao.saveAndFlush(entity);
	}

	@Override
	@Transactional
	public void delete(T entity) {
		dao.delete(entity);
	}

	@Override
	@Transactional
	public void delete(ID id) {
		dao.deleteById(id);
	}

}
