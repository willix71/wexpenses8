package w.expenses8.web.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.domain.criteria.ExpenseCriteria;
import w.expenses8.data.domain.model.DocumentFile;
import w.expenses8.data.domain.model.ExchangeRate;
import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.model.enums.TagType;
import w.expenses8.data.domain.service.IDocumentFileService;
import w.expenses8.data.domain.service.IExpenseService;
import w.expenses8.data.utils.ExcelExporter;
import w.expenses8.data.utils.ExpenseHelper;
import w.expenses8.web.controller.extra.EditionMode;

@Slf4j
@Named
@ViewScoped
@Getter @Setter
public class ExpenseController extends AbstractListEditionController<Expense> {

	private static final long serialVersionUID = 3351336696734127296L;
	
	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private IExpenseService expenseService;
	
	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private IDocumentFileService documentFileService;
	
	private ExpenseCriteria criteria = ExpenseCriteria.from(Year.now().atDay(1));

	public ExpenseController() {
		super(Expense.class);
	}
	
	public void resetAll() {
		criteria.clear();
		loadEntities();
	}
	
	public void toExcel() throws IOException {
		log.info("Generating excel export with {}", criteria);
		downloadExcel("Expenses.xls");
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
		ExcelExporter.exportExcelExpenses(expenseService.findExpenses(criteria),output);

		// Important! Otherwise Faces will attempt to render the response which
		// obviously will fail since it's already written with a file and closed.
		facesContext.responseComplete(); 
	}
	
	public void duplicateExpense() {
		Expense baseExpense = getSelectedElement();
		
		ExchangeRate copyXR = baseExpense.getExchangeRate()==null?null:baseExpense.getExchangeRate().duplicate();
		Expense copy = ExpenseHelper.build(
				baseExpense.getPayee(), baseExpense.getExpenseType(), baseExpense.getDate(),baseExpense.getCurrencyAmount(), baseExpense.getCurrencyCode(), copyXR,
				baseExpense.getTransactions().stream().map(e->ExpenseHelper.buildTransactionEntry(e.getTags().stream().filter(t->t.getType()!=TagType.CONSOLIDATION).collect(Collectors.toList()),e.getFactor())).collect(Collectors.toList()));

		copy.setExternalReference(baseExpense.getExternalReference());
		copy.setDescription(baseExpense.getDescription());

		openEditor(copy, EditionMode.EDIT);
	}
	
	@Override
	protected void loadEntities() {
		log.info("loading expenses with {}", criteria);
		elements = expenseService.findExpenses(criteria);
	}
	
	@Override
	public String getRowStyleClass(Expense element) {
		return element!=null && element.getDate().isAfter(LocalDateTime.now()) ? "wex-expense-futur" : "wex-expense-past";
	}
	
	public MenuModel getDocumentFileMenu(Collection<DocumentFile> files) {
		MenuModel model= new DefaultMenuModel();
		if (files != null) {
			for(DocumentFile file:files) {
				model.getElements().add(DefaultMenuItem.builder()
						//.label(file.getFileName())
						.value(file.getFileName())
						.icon("pi pi-circle-on")
						.url(documentFileService.getUrl(file))
						.target("_blank")
						.build());
			}
		}
		return model;
	}
}
