package w.expenses8.data.domain.service;

import java.util.List;

import w.expenses8.data.core.service.IGenericService;
import w.expenses8.data.domain.model.PayeeType;

public interface IPayeeTypeService extends IGenericService<PayeeType, Long> {

	PayeeType findByName(String name);

	List<PayeeType> findBySelectable(String prefix);
}
