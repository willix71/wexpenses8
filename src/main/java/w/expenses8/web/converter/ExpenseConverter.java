package w.expenses8.web.converter;

import javax.faces.convert.FacesConverter;
import javax.inject.Named;

import w.expenses8.data.domain.model.Expense;

@Named
@FacesConverter(value = "expenseConverter", managed = true)
public class ExpenseConverter extends DbableConverter<Expense> { }