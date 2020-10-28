package w.expenses8.data.domain.service;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

import w.expenses8.data.core.service.IGenericService;
import w.expenses8.data.domain.model.Consolidation;
import w.expenses8.data.domain.model.TransactionEntry;

public interface IConsolidationService extends IGenericService<Consolidation, Long> {

	@Transactional
	Consolidation reload(Object o);

	@Transactional
	Consolidation save(Consolidation conso, Collection<TransactionEntry> entries);
	
}
