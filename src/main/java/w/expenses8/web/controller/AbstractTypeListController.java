package w.expenses8.web.controller;

import w.expenses8.data.core.model.AbstractType;

@SuppressWarnings("serial")
public class AbstractTypeListController<T extends AbstractType<T>> extends AbstractListController<T> {

	public AbstractTypeListController() {
		super();
	}

	public AbstractTypeListController(Class<T> clazz) {
		super(clazz);
	}

	@Override
	public void newElement() throws Exception {
		super.newElement();
		elements.add(selectedElement);
	}

	@Override
	protected String nameOf(T entity) {
		return entity == null?super.nameOf(entity):super.nameOf(entity) + " [" + entity.getName() + "]";
	}
}
