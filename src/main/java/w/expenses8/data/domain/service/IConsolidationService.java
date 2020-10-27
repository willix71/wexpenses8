package w.expenses8.data.domain.service;

import org.springframework.transaction.annotation.Transactional;

import w.expenses8.data.core.service.IGenericService;
import w.expenses8.data.domain.model.Consolidation;
import w.expenses8.data.domain.model.Expense;

public interface IConsolidationService extends IGenericService<Consolidation, Long> {

	@Transactional
	Consolidation reload(Object o);
	
}
