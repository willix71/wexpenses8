package w.expenses8.web.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.Year;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.core.model.DBable;
import w.expenses8.data.domain.criteria.TransactionEntryCriteria;
import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.model.TransactionEntry;
import w.expenses8.data.domain.model.enums.TransactionFactor;
import w.expenses8.data.domain.service.IExpenseService;
import w.expenses8.data.domain.service.ITransactionEntryService;
import w.expenses8.data.utils.ConsolidationHelper;
import w.expenses8.data.utils.ExcelExporter;

@Slf4j
@Named
@ViewScoped
@Getter @Setter
public class TransactionEntryController extends AbstractListEditionController<TransactionEntry> {

	private static final long serialVersionUID = 3351336696734127296L;
	
	@Inject 
	private ExpenseEditionController expenseEditionController;
	
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
		loadEntities();
	}

	@Override
	public String getEditorsPage() {
		return "expense";
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
		elements = transactionEntryService.findTransactionEntries(criteria);
		
		BigDecimal liveBalance = BigDecimal.ZERO;
		BigDecimal opposite = BigDecimal.valueOf(-1);
		for(TransactionEntry e:elements) {
			switch(e.getFactor()) {
			case IN:
				liveBalance = liveBalance.add(e.getAccountingValue());
				break;
			case OUT:
				liveBalance = liveBalance.add(opposite.multiply(e.getAccountingValue()));
				break;
			case SUM:
				liveBalance = e.getAccountingValue();
				break;
			}
			e.setLiveBalance(liveBalance);
		}
	}
	
	@Override
	public DBable<?> convert(TransactionEntry t) {
		return t.getExpense();
	}
	
	public BigDecimal getTotalIn() {
		return ConsolidationHelper.sum(elements, TransactionFactor.IN);
	}
	
	public BigDecimal getTotalOut() {
		return ConsolidationHelper.sum(elements, TransactionFactor.OUT);
	}
	
	public BigDecimal getLastBalance() {
		return ConsolidationHelper.sum(elements);
	}
	
	public void toExcel() throws IOException {
		log.info("Generating excel export with {}", criteria);
		downloadExcel("Accounts.xls");
	}
    
	public void downloadExcel(String fileName) throws IOException {
		// inspired from https://stackoverflow.com/questions/9391838/how-to-provide-a-file-download-from-a-jsf-backing-bean

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		// Some Faces component library or some Filter might have set some headers in
		// the buffer beforehand. We want to get rid of them, else it may collide.
		externalContext.responseReset();
		// Check https://www.iana.org/assignments/media-types for all types.
		externalContext.setResponseContentType("application/vnd.ms-excel"); 
		// externalContext.setResponseContentLength(contentLength); // Unknown
		externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

		OutputStream output = externalContext.getResponseOutputStream();
		ExcelExporter.exportExcelTransactionEntries(transactionEntryService.findTransactionEntries(criteria),output);

		// Important! Otherwise Faces will attempt to render the response which
		// obviously will fail since it's already written with a file and closed.
		facesContext.responseComplete(); 
	}
}
