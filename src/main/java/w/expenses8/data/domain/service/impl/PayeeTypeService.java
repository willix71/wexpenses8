package w.expenses8.data.domain.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.expenses8.data.core.service.GenericServiceImpl;
import w.expenses8.data.domain.dao.IPayeeTypeDao;
import w.expenses8.data.domain.model.PayeeType;
import w.expenses8.data.domain.service.IPayeeTypeService;
import w.expenses8.data.utils.CriteriaHelper;
import w.expenses8.data.utils.StringHelper;

@Service
public class PayeeTypeService extends GenericServiceImpl<PayeeType, Long, IPayeeTypeDao> implements IPayeeTypeService {

	@Autowired
	public PayeeTypeService(IPayeeTypeDao dao) {
		super(PayeeType.class, dao);
	}
	
	@Override
	public PayeeType findByName(String name) {
		return getDao().findByName(name);
	}
	
	@Override
	public List<PayeeType> findBySelectable(String prefix) {
		if (StringHelper.isEmpty(prefix)) {
			return getDao().findBySelectable();
		} else {
			return getDao().findBySelectable(CriteriaHelper.like(prefix.toLowerCase()));
		}
	}
}
