package w.expenses8.web.controller;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.primefaces.PrimeFaces;
import org.primefaces.event.RowEditEvent;

import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.core.model.DBable;
import w.expenses8.data.core.service.IGenericService;

@Slf4j
public class AbstractListController<T extends DBable<T>> implements Serializable {

	private static final long serialVersionUID = 3351336696734127296L;

	protected final Class<T> clazz;
	
	@Inject
	protected IGenericService<T, ?> elementService;
	
	protected List<T> elements;
	protected T selectedElement;
    
	@SuppressWarnings("unchecked")
	public AbstractListController() {
		ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
		clazz = (Class<T>) parameterizedType.getActualTypeArguments()[0];
	}
	
	public AbstractListController(Class<T> clazz) {
		this.clazz = clazz;
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
		log.info("Selecting {}", selectedElement);
		this.selectedElement = selectedElement;
	}

	public void refresh() {
		loadEntities();
		showMessage(nameOf(null) + " refreshed");
	}
	
	public void newElement() throws Exception {
		selectedElement = clazz.newInstance();
	}
	
	public void onRowEdit(RowEditEvent event) {
		@SuppressWarnings("unchecked")
		T t = (T) event.getObject();
		t = elementService.save(selectedElement);
		showMessage(nameOf(t) + "' saved");
		refresh();
	}

	public void onRowCancel(RowEditEvent event) {
		refresh();
	}
	
	public void save() {
		int index = elements.indexOf(selectedElement);
		T newP = elementService.save(selectedElement);
		if (index<0) {
			elements.add(newP);
		} else {
			elements.set(index, newP);
		}
		PrimeFaces.current().ajax().addCallbackParam("isSaved",true);
	}
	
	public void delete() {
		elementService.delete(selectedElement);
		showMessage(nameOf(selectedElement) + "' deleted");
		refresh();
		selectedElement = null;
	}

	protected void showMessage(String text) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(text));
	}
	
	protected void loadEntities() {
		elements = elementService.loadAll();
	}

	protected String nameOf(T entity) {
		return clazz.getSimpleName();
	}
}
