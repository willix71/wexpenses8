package w.expenses8.web.controller;

import org.primefaces.event.RowEditEvent;

import w.expenses8.data.core.model.AbstractType;

public class AbstractTypeListController<T extends AbstractType<T>> extends AbstractListController<T> {

	private static final long serialVersionUID = 3351336696734127296L;

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
	
	public void onRowEdit(RowEditEvent<T> event) {
		T t = event.getObject();
		t = elementService.save(selectedElement);
		showMessage(nameOf(t) + " saved " + t.getFullId());
		refresh();
	}

	public void onRowCancel(RowEditEvent<T> event) {
		refresh();
	}
	
	@Override
	protected String nameOf(T entity) {
		return entity == null?super.nameOf(entity):super.nameOf(entity) + " [" + entity.getName() + "]";
	}
}
