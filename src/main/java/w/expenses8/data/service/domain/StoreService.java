package w.expenses8.data.service.domain;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import w.expenses8.data.model.core.DBable;

@Service
public class StoreService {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Transactional
	public <T extends DBable<T>> T save(T entity) {
		if (entity.isNew()) {
			entityManager.persist(entity);
			return entity;
		} else {
			return entityManager.merge(entity);
		}
	}
	
	public <T extends DBable<T>> T load(Class<T> clazz, Serializable id) {
		return entityManager.find(clazz, id);
	}
	
}
