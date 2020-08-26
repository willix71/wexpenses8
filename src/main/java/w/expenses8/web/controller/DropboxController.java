package w.expenses8.web.controller;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import w.expenses8.data.domain.model.ExpenseType;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.model.PayeeType;
import w.expenses8.data.domain.model.enums.TagEnum;
import w.expenses8.data.domain.service.IExpenseTypeService;
import w.expenses8.data.domain.service.IPayeeService;
import w.expenses8.data.domain.service.IPayeeTypeService;

@Named
@ApplicationScoped
@SuppressWarnings("serial")
public class DropboxController implements Serializable {

	@Inject
	private IPayeeTypeService payeeTypeService;
	
	public List<PayeeType> completePayeeType(String prefix) {
		return payeeTypeService.findBySelectable(prefix);
	}
	
	@Inject
	private IExpenseTypeService expenseTypeService;
	
	public List<ExpenseType> completeExpenseType(String prefix) {
		return expenseTypeService.findBySelectable(prefix);
	}
	
	@Inject
	private IPayeeService payeeService;
	
	public List<Payee> completePayee(String text) {
		return payeeService.findByText(text);
	}
	
	List<TagEnum> tagTypes = Arrays.asList(TagEnum.values());
	
	public List<TagEnum> getTagTypes() {
		return tagTypes;
	}
}
