package w.expenses8.data.core.service;

import org.springframework.transaction.annotation.Transactional;

import w.expenses8.data.core.model.DBable;

public interface IReloadableService<T extends DBable<?>> {

	@Transactional
	T reload(Object o);
}
