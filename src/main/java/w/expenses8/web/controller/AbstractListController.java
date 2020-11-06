package w.expenses8.web.controller;

import java.util.List;

import javax.inject.Inject;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.core.model.DBable;
import w.expenses8.data.core.service.IGenericService;

@Slf4j
@NoArgsConstructor
public class AbstractListController<T extends DBable<T>> extends AbstractController<T> {

	private static final long serialVersionUID = 3351336696734127296L;

	@Inject
	protected IGenericService<T, Long> elementService;
	
	protected List<T> elements;
	protected T selectedElement;
	
	public AbstractListController(Class<T> clazz) {
		super(clazz);
	}

	public List<T> getElements() {
		if (elements == null) {
			loadEntities();
		}
		return elements;
	}
	
	public T getSelectedElement() {
		return selectedElement;
	}

	public void setSelectedElement(T selectedElement) {
		log.debug("Selecting {}", selectedElement);
		this.selectedElement = selectedElement;
	}

	public void refresh() {
		log.info("Refreshing {}", clazz.getSimpleName());
		loadEntities();
		showMessage("Refreshed",null);
	}
	
	protected void loadEntities() {
		elements = elementService.loadAll();
	}

}
