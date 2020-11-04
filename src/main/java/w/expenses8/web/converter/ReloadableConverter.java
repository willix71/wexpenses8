package w.expenses8.web.converter;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;

import w.expenses8.data.core.model.DBable;
import w.expenses8.data.core.service.IReloadableService;

public class ReloadableConverter<T extends DBable<?>> implements Converter<T> {

	@Inject
	private IReloadableService<T> service;

	@Override
	public T getAsObject(FacesContext fc, UIComponent uic, String uid) {
		if (uid != null && uid.trim().length() > 0) {
			try {
				return service.reload(uid);
			} catch (NumberFormatException e) {
				throw new ConverterException(
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid DBable."));
			}
		} else {
			return null;
		}
	}

	@Override
	public String getAsString(FacesContext fc, UIComponent uic, T object) {
		if (object != null) {
			return object.getUid();
		} else {
			return null;
		}
	}
}
