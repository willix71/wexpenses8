package w.expenses8.web.controller;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import lombok.AllArgsConstructor;
import w.expenses8.data.core.model.DBable;

@AllArgsConstructor
public class AbstractController<T extends DBable<T>> implements Serializable{
	
	private static final long serialVersionUID = 3351336696734127296L;
	
	protected final Class<T> clazz;
	
	@SuppressWarnings("unchecked")
	public AbstractController() {
		clazz = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	protected String getClassName() {
		return clazz.getSimpleName();
	}
	
	protected String nameOf(DBable<?> entity) {
		if (entity == null) {
			return clazz.getSimpleName();
		} else {
			return entity.getClass().getSimpleName()  + " " + entity.getFullId();
		}
	}
	
	protected void showMessage(String event, T element) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, event, nameOf(element)));
	}
}
