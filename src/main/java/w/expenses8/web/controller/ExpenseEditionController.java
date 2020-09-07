package w.expenses8.web.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.config.CurrencyValue;
import w.expenses8.data.core.model.DBable;
import w.expenses8.data.domain.model.DocumentFile;
import w.expenses8.data.domain.model.ExchangeRate;
import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.model.TransactionEntry;
import w.expenses8.data.domain.service.IExpenseService;
import w.expenses8.data.utils.ExpenseHelper;

@Slf4j
@Named
@ViewScoped
@Getter @Setter
public class ExpenseEditionController implements Serializable {

	private static final long serialVersionUID = 3351336696734127296L;

	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private IExpenseService expenseService;
	
	@Inject
	private CurrencyValue currencyValue;

	private Expense currentExpense;
	
	private Date myDate = new Date();
	
	private LocalDateTime currentDate;
	private BigDecimal currentAmount;
	private Map<String,ExchangeRate> potentialExchangeRates = new HashMap<String, ExchangeRate>();
	
	private DocumentFile selectedDocumentFile;
	private TransactionEntry selectedTransactionEntry;
	
	@PostConstruct
	public void initSelectedElement() {
		String id=((HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequest())).getParameter("id");
		if (id!=null) {
			setCurrentExpense(ExpenseHelper.build(DBable.with().id(Long.parseLong(id)).build()));
		} else {
			String uid=((HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequest())).getParameter("uid");
			if (uid!=null) {
				setCurrentExpense(expenseService.loadByUid(uid));
			}
		}
	}
	
	public void save() {
		currentExpense = expenseService.save(currentExpense);
		PrimeFaces.current().ajax().addCallbackParam("isSaved",true);
	}
	
	public void newExpense() {
		setCurrentExpense(ExpenseHelper.build());
	}
	
	public void delete() {
		expenseService.delete(currentExpense);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Expense deleted"));
		currentExpense = null;
	}
	
	public void setCurrentExpense(Expense expense) {
		this.potentialExchangeRates.clear();

		this.currentExpense = expense.isNew()?expense:expenseService.reload(expense);
		if (currentExpense != null) {
			this.currentDate = currentExpense.getDate();
			this.currentAmount = currentExpense.getCurrencyAmount();
			
			if (currentExpense.getExchangeRate()!=null) {
				this.potentialExchangeRates.put(currentExpense.getExchangeRate().getFromCurrencyCode(), currentExpense.getExchangeRate());
			}
		}
	}
	
	public void handleDateChange(SelectEvent<LocalDateTime> event) {
		LocalDateTime newdate = event.getObject();
		log.info("handleDateChange old {} new {}",currentDate, newdate);
		currentExpense.updateDate(currentDate);
		currentDate = newdate;
	}
	
	public void onDateChange() {
		log.info("onDateChange old {} new {}",currentDate, currentExpense.getDate());
		currentExpense.updateDate(currentDate);
		currentDate = currentExpense.getDate();
	}
	
	public void onCurrencyChange() {
		String newCurrencyCode = currentExpense.getCurrencyCode();
		log.debug("onCurrencyChange {}",newCurrencyCode);
		if (currencyValue.getCode().equals(newCurrencyCode)) {
			currentExpense.setExchangeRate(null);
		} else {
			ExchangeRate potentialExchangeRate = potentialExchangeRates.get(newCurrencyCode);
			if (potentialExchangeRate!=null) {
				currentExpense.setExchangeRate(potentialExchangeRate);
			} else {
				Payee institution = null; // lookup institution
				// new expense rate
				ExchangeRate newExchangeRate = ExchangeRate.with()
						.institution(institution)
						.date(currentExpense.getDate().toLocalDate())
						.fromCurrencyCode(newCurrencyCode)
						.toCurrencyCode(currencyValue.getCode())
						.rate(1.2)
						.build();
				
				potentialExchangeRates.put(newCurrencyCode, newExchangeRate);
				currentExpense.setExchangeRate(newExchangeRate);
			}
		}
	}
	
	public void onAmountChange() {
		log.info("onAmountChange old {} new {}",currentAmount, currentExpense.getCurrencyAmount());
		currentExpense.updateAmountValues(currentAmount, currencyValue.getPrecision());
		currentAmount = currentExpense.getCurrencyAmount();
	}
	
	public void newDocumentFile() {
		currentExpense.addDocumentFile(new DocumentFile(currentExpense.getDate()==null?null:currentExpense.getDate().toLocalDate(),null));
		currentExpense.updateDocumentCount();
	}
	
	public void deleteDocumentFile() {
		if (selectedDocumentFile!=null) {
			log.info("Deleting document file {} named {}", selectedDocumentFile, selectedDocumentFile.getFileName());
			currentExpense.getDocumentFiles().remove(selectedDocumentFile);
			selectedDocumentFile.setExpense(null);
			currentExpense.updateDocumentCount();
		}
	}
	
	public void newTransactionEntry() {
		currentExpense.addTransaction(new TransactionEntry());
	}
	
	public void deleteTransactionEntry() {
		if (selectedTransactionEntry!=null) {
			log.info("Deleting transaction entry {} for {}", selectedTransactionEntry, selectedTransactionEntry.getTags());
			currentExpense.getTransactions().remove(selectedTransactionEntry);
			selectedTransactionEntry.setExpense(null);
		}
	}
}
