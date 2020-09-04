package w.expenses8.web.controller;

import java.time.Year;
import java.time.YearMonth;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.domain.criteria.ExpenseCriteria;
import w.expenses8.data.domain.model.DocumentFile;
import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.model.TransactionEntry;
import w.expenses8.data.domain.service.IExpenseService;

@Slf4j
@Named
@ViewScoped
@Getter @Setter
public class ExpenseController extends AbstractListController<Expense> {

	private static final long serialVersionUID = 3351336696734127296L;

	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private IExpenseService expenseService;

	@Inject
	private ExpenseControllerAssistant assistant;
	
	private ExpenseCriteria criteria = ExpenseCriteria.from(YearMonth.now().atDay(1));
	
	private DocumentFile selectedDocumentFile;
	private TransactionEntry selectedTransactionEntry;

	public void resetMonth() {
		criteria = ExpenseCriteria.from(YearMonth.now().atDay(1));
		loadEntities();
	}
	public void resetYear() {
		criteria = ExpenseCriteria.from(Year.now().atDay(1));
		loadEntities();
	}
	public void resetAll() {
		criteria = ExpenseCriteria.from(null);
		loadEntities();
	}
	
	@Override
	protected void loadEntities() {
		log.info("loading expenses with {}", criteria);
		elements = expenseService.findExpenses(criteria);
	}

	@Override
	public void setSelectedElement(Expense selectedElement) {
		super.setSelectedElement(selectedElement);
		assistant.setCurrentExpense(selectedElement);
	}
	
	public void newDocumentFile() {
		selectedElement.addDocumentFile(new DocumentFile(selectedElement.getDate()==null?null:selectedElement.getDate().toLocalDate(),null));
		selectedElement.updateDocumentCount();
	}
	
	public void deleteDocumentFile() {
		if (selectedDocumentFile!=null) {
			log.info("Deleting document file {} named {}", selectedDocumentFile, selectedDocumentFile.getFileName());
			selectedElement.getDocumentFiles().remove(selectedDocumentFile);
			selectedDocumentFile.setExpense(null);
			selectedElement.updateDocumentCount();
		}
	}
	
	public void newTransactionEntry() {
		selectedElement.addTransaction(new TransactionEntry());
	}
	
	public void deleteTransactionEntry() {
		if (selectedTransactionEntry!=null) {
			log.info("Deleting transaction entry {} for {}", selectedTransactionEntry, selectedTransactionEntry.getTags());
			selectedElement.getTransactions().remove(selectedTransactionEntry);
			selectedTransactionEntry.setExpense(null);
		}
	}
}
