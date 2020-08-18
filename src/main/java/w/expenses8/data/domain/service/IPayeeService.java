package w.expenses8.data.domain.service;

import java.util.List;

import w.expenses8.data.core.service.IGenericService;
import w.expenses8.data.domain.model.Payee;

public interface IPayeeService extends IGenericService<Payee, Long> {

	Payee findByName(String name);

	List<Payee> findByText(String text);
}