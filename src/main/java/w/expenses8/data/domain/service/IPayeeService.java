package w.expenses8.data.domain.service;

import java.util.List;

import w.expenses8.data.core.service.IGenericService;
import w.expenses8.data.core.service.IReloadableService;
import w.expenses8.data.domain.criteria.PayeeCriteria;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.model.enums.PayeeDisplayer;

public interface IPayeeService extends IGenericService<Payee, Long>, IReloadableService<Payee> {
	
	Payee findByName(String name);

	List<Payee> findByText(String text);

	List<Payee> findByText(String text, PayeeDisplayer displayer);
	
	List<Payee> findPayees(PayeeCriteria criteria);
}