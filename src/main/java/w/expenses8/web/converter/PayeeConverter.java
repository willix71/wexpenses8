package w.expenses8.web.converter;

import javax.faces.convert.FacesConverter;
import javax.inject.Named;

import w.expenses8.data.domain.model.Payee;

@Named
@FacesConverter(value = "payeeConverter", managed = true)
public class PayeeConverter extends DbableConverter<Payee> { }