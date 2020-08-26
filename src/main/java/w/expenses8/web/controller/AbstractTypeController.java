package w.expenses8.web.controller;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.primefaces.event.RowEditEvent;

import lombok.Getter;
import lombok.Setter;
import w.expenses8.data.core.model.AbstractType;
import w.expenses8.data.core.service.IGenericService;

@Getter @Setter
@SuppressWarnings("serial")
public class AbstractTypeController<T extends AbstractType<T>> implements Serializable {

	private final Class<T> clazz;
	
	@Inject
	private IGenericService<T, ?> typeService;
	
	private List<T> types;
	private T selectedType;
    
	@SuppressWarnings("unchecked")
	public AbstractTypeController() {
		ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
		 clazz = (Class<T>) parameterizedType.getActualTypeArguments()[0];
	}
	
	public List<T> getTypes() {
		if (types == null) {
			refresh();
		}
		return types;
	}
	
	public void refresh() {
		types = typeService.loadAll();
		FacesMessage msg = new FacesMessage(clazz.getSimpleName() + " refreshed");
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	public void save() {
		int index = types.indexOf(selectedType);
		T newP = typeService.save(selectedType);
		if (index<0) {
			types.add(newP);
		} else {
			types.set(index, newP);
		}
	}

	public void onRowEdit(RowEditEvent event) {
		@SuppressWarnings("unchecked")
		T type = (T) event.getObject();
		type = typeService.save(selectedType);
		FacesMessage msg = new FacesMessage(clazz.getSimpleName() + " '" + type.getName() + "' saved");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		refresh();
	}

	public void onRowCancel(RowEditEvent event) {
		refresh();
	}
	
	public void newType() throws Exception {
		selectedType = clazz.newInstance();
		types.add(selectedType);
	}
	
	public void delete() {
		typeService.delete(selectedType);
		FacesMessage msg = new FacesMessage(clazz.getSimpleName() + " '" + selectedType.getName() + "' deleted");
		FacesContext.getCurrentInstance().addMessage(null, msg);
		refresh();
		selectedType = null;
	}
}
