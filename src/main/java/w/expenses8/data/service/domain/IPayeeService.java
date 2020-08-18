package w.expenses8.data.service.domain;

import java.util.List;

import w.expenses8.data.model.domain.Payee;
import w.expenses8.data.service.core.IGenericService;

public interface IPayeeService extends IGenericService<Payee, Long> {

	Payee findByName(String name);

	List<Payee> findByText(String text);
}