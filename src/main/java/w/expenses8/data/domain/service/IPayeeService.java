package w.expenses8.data.domain.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import w.expenses8.data.core.service.IGenericService;
import w.expenses8.data.domain.criteria.PayeeCriteria;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.model.enums.PayeeDisplayer;

public interface IPayeeService extends IGenericService<Payee, Long> {

	@Transactional
	Payee reload(Object o);
	
	Payee findByName(String name);

	List<Payee> findByText(String text);

	List<Payee> findByText(String text, PayeeDisplayer displayer);
	
	List<Payee> findPayees(PayeeCriteria criteria);
}