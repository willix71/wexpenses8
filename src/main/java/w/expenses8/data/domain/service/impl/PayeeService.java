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
import w.expenses8.data.domain.service.IPayeeService;
import w.expenses8.data.utils.CriteriaHelper;
import w.expenses8.data.utils.StringHelper;

@Service
public class PayeeService extends GenericServiceImpl<Payee, Long, IPayeeDao> implements IPayeeService {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	public PayeeService(IPayeeDao dao) {
		super(Payee.class, dao);
	}

	@Override
	public Payee findByName(String name) {
		return getDao().findByName(name);
	}

	@Override
	public List<Payee> findByText(String text) {
		return getDao().findByText(CriteriaHelper.like(text.toLowerCase()));
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
