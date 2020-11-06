package w.expenses8.web.controller;

import org.primefaces.event.RowEditEvent;

import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.core.model.AbstractType;

@Slf4j
public class AbstractTypeListController<T extends AbstractType<T>> extends AbstractListController<T> {

	private static final long serialVersionUID = 3351336696734127296L;

	public void newElement() throws Exception {
		log.info("New element {}", clazz);
		selectedElement = clazz.newInstance();
		elements.add(selectedElement);
	}
	
	public void onRowEdit(RowEditEvent<T> event) {
		T t = event==null?selectedElement:event.getObject();
		log.info("Saving {}", t);		
		t = elementService.save(t);
		showMessage("Saved",t);
		refresh();
	}

	public void onRowCancel(RowEditEvent<T> event) {
		refresh();
	}
	
	public void delete() {
		log.info("Deleting {}", selectedElement);
		elementService.delete(selectedElement);
		showMessage("Deleted",selectedElement);
		selectedElement = null;
		refresh();
	}
	
	@Override
	protected String nameOf(T entity) {
		String name = super.nameOf(entity);
		if (entity!=null) name += " [" + entity.getName() + "]";
		return name;
	}
}
