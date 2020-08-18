package w.expenses8.data.domain.service.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.expenses8.data.core.service.GenericServiceImpl;
import w.expenses8.data.domain.dao.IPayeeDao;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.service.IPayeeService;

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
		return getDao().findByText(like(text));
	}
}
