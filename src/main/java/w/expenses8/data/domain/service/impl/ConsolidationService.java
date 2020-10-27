package w.expenses8.data.domain.service.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.expenses8.data.core.model.DBable;
import w.expenses8.data.core.service.GenericServiceImpl;
import w.expenses8.data.domain.dao.IConsolidationDao;
import w.expenses8.data.domain.model.Consolidation;
import w.expenses8.data.domain.service.IConsolidationService;

@Service
public class ConsolidationService extends GenericServiceImpl<Consolidation, Long, IConsolidationDao> implements IConsolidationService {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	public ConsolidationService(IConsolidationDao dao) {
		super(Consolidation.class, dao);
	}
	
	void persist(DBable<?> d) {
		if (d!=null && d.isNew()) {
			entityManager.persist(d);
		}
	}
	
	@Override
	public Consolidation reload(Object o) {
		if (o == null) return new Consolidation();
		Long id;
		if (o instanceof Consolidation) {
			Consolidation x=(Consolidation)o;
			if (x.isNew()) {
				return x;
			}
			id = x.getId();
		} else if (o instanceof Long) {
			id = (Long)o;
		} else {			
			id = loadByUid((String) o).getId();
		}
		return getDao().reload(id);
	}
	
	@Override
	public Consolidation save(Consolidation x) {
		persist(x.getInstitution());
		persist(x.getDocumentFile());
		//persist(x.getOpeningEntry());
		//persist(x.getClosingEntry());
		return super.save(x);
	}
}