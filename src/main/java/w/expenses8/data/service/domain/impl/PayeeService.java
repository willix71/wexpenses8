package w.expenses8.data.service.domain.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.expenses8.data.dao.domain.IPayeeDao;
import w.expenses8.data.model.domain.Payee;
import w.expenses8.data.service.core.GenericServiceImpl;
import w.expenses8.data.service.domain.IPayeeService;

@Service
public class PayeeService extends GenericServiceImpl<Payee, Long, IPayeeDao> implements IPayeeService {

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
