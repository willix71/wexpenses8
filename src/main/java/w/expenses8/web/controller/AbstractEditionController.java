package w.expenses8.web.controller;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.PrimeFaces;

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

	@SuppressWarnings("unchecked")
	public AbstractEditionController() {
		ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
		clazz = (Class<T>) parameterizedType.getActualTypeArguments()[0];
	}
	
	@PostConstruct
	public void initSelectedElement() {
		String id=((HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequest())).getParameter("id");
		if (id!=null) {
			setCurrentElementId(Long.parseLong(id));
		} else {
			String uid=((HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequest())).getParameter("uid");
			if (uid!=null) {
				setCurrentElementId(uid);
			}
		}
	}
	
	public void newElement() throws Exception {
		setCurrentElementId(null);
	}
	
	public void save() {
		currentElement = elementService.save(currentElement);
		PrimeFaces.current().ajax().addCallbackParam("isSaved",true);
	}
	
	public void delete() {
		elementService.delete(currentElement);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("deleted"));
		currentElement = null;
	}
	
	public abstract void setCurrentElementId(Object o); 
}