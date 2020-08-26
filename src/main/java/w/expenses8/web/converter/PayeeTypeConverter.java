package w.expenses8.web.converter;

import javax.faces.convert.FacesConverter;
import javax.inject.Named;

import w.expenses8.data.domain.model.PayeeType;

@Named
@FacesConverter(value = "payeeTypeConverter", managed = true)
public class PayeeTypeConverter extends DbableConverter<PayeeType> { }