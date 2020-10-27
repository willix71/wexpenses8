package w.expenses8.web.controller;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import w.expenses8.data.core.model.DBable;
import w.expenses8.data.core.service.IGenericService;

@Getter @Setter
public abstract class AbstractEditionController<T extends DBable<T>> implements Serializable {

	private static final long serialVersionUID = 3351336696734127296L;

	protected final Class<T> clazz;
	
	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	protected IGenericService<T, ?> elementService;

	protected T currentElement;

	private boolean edition = true;
	
	@SuppressWarnings("unchecked")
	public AbstractEditionController() {
		ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
		clazz = (Class<T>) parameterizedType.getActualTypeArguments()[0];
	}
		
	public void onRowDoubleClick(final SelectEvent<T> event) {
		T t = event.getObject();
		setCurrentElement(t);
	}
	
	@PostConstruct
	public void initSelectedElementId() {		
		setCurrentElementId(getInitialElementId());
	}
	
	protected Object getInitialElementId() {
		String id=((HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequest())).getParameter("id");
		if (id!=null) {
			return Long.parseLong(id);
		} else {
			String uid=((HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequest())).getParameter("uid");
			if (uid!=null) {
				return uid;
			}
		}
		return null;
	}
	
	public void setEdition(boolean edit) {
		if (this.edition && !edit) {
			// reset the 'error' fields			
			PrimeFaces.current().resetInputs("wexEditionForm");
			// reload the element
			setCurrentElement(currentElement); // force reload
		}
		
		this.edition = edit;
	}
	
	public void newElement() throws Exception {
		setCurrentElementId(null);
	}
	
	public void save() {
		currentElement = elementService.save(currentElement);
		PrimeFaces.current().ajax().addCallbackParam("isSaved",true);
		edition=false; // switch back to view mode after save
	}
	
	public void delete() {
		elementService.delete(currentElement);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("deleted"));
		currentElement = null;
	}
	
	public abstract void setCurrentElementId(Object o); 
}
