package w.expenses8.web.controller;

import java.time.Year;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.domain.criteria.TransactionEntryCriteria;
import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.model.TransactionEntry;
import w.expenses8.data.domain.service.IExpenseService;
import w.expenses8.data.domain.service.ITransactionEntryService;

@Slf4j
@Named
@ViewScoped
@Getter @Setter
public class TransactionEntryController extends AbstractListController<TransactionEntry> {

	private static final long serialVersionUID = 3351336696734127296L;
	
	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private IExpenseService expenseService;
	
	private Expense selectedExpense;
	
	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private ITransactionEntryService transactionEntryService;
	
	private TransactionEntryCriteria criteria = TransactionEntryCriteria.from(Year.now().getValue());
	
	public void reset() {
		criteria = TransactionEntryCriteria.from(Year.now().getValue());
	}

	@Override
	public void setSelectedElement(TransactionEntry selectedElement) {
		super.setSelectedElement(selectedElement);
		if (selectedElement!=null) {
			selectedExpense = expenseService.reload(selectedElement.getExpense());
		}
	}
	
	@Override
	protected void loadEntities() {
		log.info("loading transaction entries with {}", criteria);
		elements = transactionEntryService.findTransactionEntrys(criteria);
	}
	
	public void deleteExpense() {
		expenseService.delete(selectedElement.getExpense());
		showMessage(nameOf(selectedElement) + " deleted");
		refresh();
		selectedElement = null;
	}
	
	public void saveExpense() {
		expenseService.save(selectedExpense);
		refresh();
		PrimeFaces.current().ajax().addCallbackParam("isSaved",true);
	}
}
