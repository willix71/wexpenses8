package w.expenses8.web.controller;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import w.expenses8.data.config.WexpensesProperties;
import w.expenses8.data.domain.model.ExpenseType;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.model.PayeeType;
import w.expenses8.data.domain.model.Tag;
import w.expenses8.data.domain.model.enums.TagType;
import w.expenses8.data.domain.model.enums.TransactionFactor;
import w.expenses8.data.domain.service.IExpenseTypeService;
import w.expenses8.data.domain.service.IPayeeService;
import w.expenses8.data.domain.service.IPayeeTypeService;
import w.expenses8.data.domain.service.ITagService;

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
	
	@Inject
	private ITagService tagService;
	
	public List<Tag> completeTag(String text) {
		return tagService.findByText(text);
	}
	
	List<TagType> tagTypes = Arrays.asList(TagType.values());
	
	public List<TagType> getTagTypes() {
		return tagTypes;
	}
	
	List<TransactionFactor> transactionFactors = Arrays.asList(TransactionFactor.IN, TransactionFactor.OUT);
	
	public List<TransactionFactor> getTransactionFactors() {
		return transactionFactors;
	}
	
	@Inject
	WexpensesProperties wproperties;
	
	public List<String> getCountries() {
		return wproperties.getCountries();
	}
	
	public List<String> getCurrencies() {
		return wproperties.getCurrencies();
	}
}
