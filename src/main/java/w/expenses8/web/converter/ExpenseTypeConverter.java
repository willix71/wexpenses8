package w.expenses8.web.converter;

import javax.faces.convert.FacesConverter;
import javax.inject.Named;

import w.expenses8.data.domain.model.ExpenseType;

@Named
@FacesConverter(value = "expenseTypeConverter", managed = true)
public class ExpenseTypeConverter extends DbableConverter<ExpenseType> { }