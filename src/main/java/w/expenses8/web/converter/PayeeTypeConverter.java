package w.expenses8.web.converter;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;

import w.expenses8.data.domain.model.PayeeType;
import w.expenses8.data.domain.service.IPayeeTypeService;

@Named
@FacesConverter(value = "payeeTypeConverter", managed = true)
public class PayeeTypeConverter implements Converter<PayeeType> {

	@Inject
	private IPayeeTypeService payeeTypeService;

	@Override
	public PayeeType getAsObject(FacesContext fc, UIComponent uic, String value) {
		if (value != null && value.trim().length() > 0) {
			try {
				return payeeTypeService.findByName(value);
			} catch (NumberFormatException e) {
				throw new ConverterException(
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid payee type."));
			}
		} else {
			return null;
		}
	}

	@Override
	public String getAsString(FacesContext fc, UIComponent uic, PayeeType object) {
		if (object != null) {
			return object.getName();
		} else {
			return null;
		}
	}
}