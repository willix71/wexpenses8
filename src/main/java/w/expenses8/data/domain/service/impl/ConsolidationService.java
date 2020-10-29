package w.expenses8.data.domain.service.impl;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.expenses8.WexpensesConstants;
import w.expenses8.data.core.model.DBable;
import w.expenses8.data.core.service.GenericServiceImpl;
import w.expenses8.data.domain.dao.IConsolidationDao;
import w.expenses8.data.domain.dao.ITransactionEntryDao;
import w.expenses8.data.domain.model.Consolidation;
import w.expenses8.data.domain.model.TransactionEntry;
import w.expenses8.data.domain.model.enums.TagType;
import w.expenses8.data.domain.service.IConsolidationService;

@Service
public class ConsolidationService extends GenericServiceImpl<Consolidation, Long, IConsolidationDao> implements IConsolidationService {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired 
	private ITransactionEntryDao transactionEntryDao;
	
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
		if (o == null || o == WexpensesConstants.NEW_INSTANCE) return new Consolidation();
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
	public Consolidation save(Consolidation conso) {
		persist(conso.getInstitution());
		persist(conso.getDocumentFile());	
		return super.save(conso);
	}

	@Override
	public Consolidation save(Consolidation conso, Collection<TransactionEntry> entries) {
		Consolidation newConso = save(conso);
		for(TransactionEntry entry: entries) {
			if (entry.getConsolidation()!=null) {				
				entry.setConsolidation(newConso);
			}
			transactionEntryDao.save(entry);
		}
		return newConso;
	}
	
	@Override
	public void delete(Consolidation conso) {
		for(TransactionEntry entry : transactionEntryDao.findByConsolidation(conso)) {
			entry.setConsolidation(null);
			entry.setAccountingOrder(null);
			entry.setAccountingBalance(null);
			entry.getTags().removeIf(t->t.getType()==TagType.CONSOLIDATION);
		}
		
		super.delete(conso);
	}
}