package w.expenses8.web.converter;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;

import w.expenses8.data.domain.model.enums.PayeeDisplayer;

@Named
@FacesConverter(value = "payeeDisplayerConverter", managed = true)
public class PayeeDisplayerConverter implements Converter<PayeeDisplayer> {

	@Override
	public PayeeDisplayer getAsObject(FacesContext fc, UIComponent uic, String name) {
		if (name != null && name.trim().length() > 0) {
			try {
				return PayeeDisplayer.valueOf(name);
			} catch (Exception e) {
				throw new ConverterException(
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid payee displayer."));
			}
		} else {
			return null;
		}
	}

	@Override
	public String getAsString(FacesContext fc, UIComponent uic, PayeeDisplayer object) {
		if (object != null) {
			return object.toString();
		} else {
			return null;
		}
	}
}
