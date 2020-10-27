package w.expenses8.data.domain.service.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.expenses8.data.core.service.GenericServiceImpl;
import w.expenses8.data.domain.criteria.PayeeCriteria;
import w.expenses8.data.domain.dao.IPayeeDao;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.model.enums.PayeeDisplayer;
import w.expenses8.data.domain.service.IPayeeService;
import w.expenses8.data.utils.CriteriaHelper;
import w.expenses8.data.utils.StringHelper;

@Service
public class PayeeService extends GenericServiceImpl<Payee, Long, IPayeeDao> implements IPayeeService {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public Payee reload(Object o) {
		if (o == null) return new Payee();
		Long id;
		if (o instanceof Payee) {
			Payee p = (Payee)o;
			if (p.isNew()) {
				return p;
			}
			id = p.getId();
		} else if (o instanceof Long) {
			id = (Long)o;
		} else {			
			id = loadByUid((String) o).getId();
		}
		return getDao().reload(id);
	}

	@Autowired
	public PayeeService(IPayeeDao dao) {
		super(Payee.class, dao);
	}

	@Override
	public Payee findByName(String name) {
		return getDao().findByName(name);
	}

	@Override
	public List<Payee> findByText(String text, PayeeDisplayer displayer) {
		if (displayer!=null) {
			switch(displayer) {
			case CCP:
				return getDao().findByTextAndPostalAccount(CriteriaHelper.safeLowerLike(text));
			case IBAN:
				return getDao().findByTextAndIban(CriteriaHelper.safeLowerLike(text));
			default:
				// default behavior
			}
		}
		return findByText(text);
	}
	
	@Override
	public List<Payee> findByText(String text) {
		return getDao().findByText(CriteriaHelper.safeLowerLike(text));
	}

	@Override
	public List<Payee> findPayees(PayeeCriteria criteria) {
		if (criteria==null || StringHelper.isEmpty(criteria.getText())) {
			return loadAll();
		} else {
			return findByText(criteria.getText());
		}
	}
}
