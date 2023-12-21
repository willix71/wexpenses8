package w.expenses8.web.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.SelectEvent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.config.CurrencyValue;
import w.expenses8.data.domain.model.DocumentFile;
import w.expenses8.data.domain.model.ExchangeRate;
import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.model.TransactionEntry;
import w.expenses8.data.domain.service.ICountryService;
import w.expenses8.data.domain.service.IExchangeRateService;
import w.expenses8.data.domain.service.IExpenseService;
import w.expenses8.data.utils.CollectionHelper;
import w.expenses8.data.utils.ExpenseHelper;
import w.expenses8.data.utils.TransactionsSums;
import w.expenses8.web.controller.extra.EditionMode;
import w.expenses8.web.controller.extra.EditorReturnValue;
import w.expenses8.web.controller.extra.FacesHelper;

@Slf4j
@Named
@ViewScoped
@Getter @Setter
public class ExpenseEditionController extends AbstractEditionController<Expense> {

	private static final long serialVersionUID = 3351336696734127296L;

	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private ICountryService countryService;
	
	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private IExchangeRateService exchangeRateService;

	@Inject
	private DocumentFileSelector documentFileSelector;
	
	@Inject
	private CurrencyValue currencyValue;
	
	private LocalDate currentDate;
	private int localHours;
	private int localMinutes;
	private LocalDate previousDate;
	private BigDecimal previousAmount;
	private Map<String,ExchangeRate> potentialExchangeRates = new HashMap<String, ExchangeRate>();
	
	private DocumentFile selectedDocumentFile;
	
	private TransactionEntry selectedTransactionEntry;
	
	private TransactionsSums transactionsSums = new TransactionsSums();
	
	@Override
	protected void initCurrentElement() {
		this.potentialExchangeRates.clear();

		if (currentElement != null) {
			if (currentElement.getDate()!=null) {
				this.currentDate = currentElement.getDate().toLocalDate();
				LocalTime currentTime = currentElement.getDate().toLocalTime();
				this.localHours = currentTime.getHour();
				this.localMinutes = currentTime.getMinute();
			}
			this.previousDate = this.currentDate;
			
			this.previousAmount = currentElement.getCurrencyAmount();
			
			if (currentElement.getExchangeRate()!=null) {
				this.potentialExchangeRates.put(currentElement.getExchangeRate().getFromCurrencyCode(), currentElement.getExchangeRate());
			}
			transactionsSums.compute(currentElement.getTransactions());
		}
		documentFileSelector.reset(currentElement, null);
	}
	
	@Override
	protected Object getInitialElementId() {
		Object o = super.getInitialElementId();
		if (o!=null) {
			return o;
		}
		String tid=((HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequest())).getParameter("tid");
		return tid==null?null:((IExpenseService) elementService).findExpenseIdByTransactionEntryId(Long.parseLong(tid));
	}

	@Override
	protected boolean hasValidationWarnings() {
		boolean hasValidationWarnings = super.hasValidationWarnings();
		
		DocumentFile newDocFile = documentFileSelector.getCurrentDocumentFile();
		if (newDocFile!=null && (CollectionHelper.isEmpty(this.currentElement.getDocumentFiles()) || !this.currentElement.getDocumentFiles().contains(newDocFile))) {
			log.warn("DocumentFile has not been added");
			FacesContext.getCurrentInstance().addMessage("ValidationErrors", new FacesMessage(FacesMessage.SEVERITY_WARN, "documentFiles", "DocumentFile has not been added"));
			hasValidationWarnings = true;
		}

		List<Expense> similar = ((IExpenseService) elementService).findSimiliarExpenses(this.currentElement);
		if (!CollectionHelper.isEmpty(similar)) {
			log.warn("Similar expenses have been found");
			FacesContext.getCurrentInstance().addMessage("ValidationErrors", new FacesMessage(FacesMessage.SEVERITY_WARN, null, similar.size() + " similar expenses have been found"));
			hasValidationWarnings = true;
		}
		
		return hasValidationWarnings;
	}
	
	public void handlePayeeChange(SelectEvent<Payee> event) {
		if (this.currentElement.getPayee()!=null) {
			String newCurrencyCode = countryService.getCurrency(this.currentElement.getPayee().getCountryCode());
			if (newCurrencyCode!=null && !newCurrencyCode.equals(this.currentElement.getCurrencyCode())) {
				this.currentElement.setCurrencyCode(newCurrencyCode);
				onCurrencyChange();
			}
		}
	}
	
	public void onLocalDateChange(SelectEvent<LocalDate> event) {
		log.debug("localDate has changed");
		this.currentElement.setDate(LocalDateTime.of(currentDate, LocalTime.of(localHours, localMinutes)));
		currentElement.updateDate(previousDate);
		previousDate = currentDate;
	}
	
	public void onCurrencyChange() {
		String newCurrencyCode = currentElement.getCurrencyCode();
		log.debug("onCurrencyChange {}", newCurrencyCode);
		if (currencyValue.isDefaultCurrency(newCurrencyCode)) {
			currentElement.setExchangeRate(null);
		} else {
			ExchangeRate potentialExchangeRate = potentialExchangeRates.get(newCurrencyCode);
			if (potentialExchangeRate!=null) {
				currentElement.setExchangeRate(potentialExchangeRate);
			} else {
				ExchangeRate newExchangeRate = exchangeRateService.buildExchangeRate(currentElement);
				currentElement.setExchangeRate(newExchangeRate);

				potentialExchangeRates.put(newCurrencyCode, newExchangeRate);
			}
		}
	}
	
	public void onAmountChange() {
		log.debug("onAmountChange old {} new {}",previousAmount, currentElement.getCurrencyAmount());
		currentElement.updateAmountValues(previousAmount, currencyValue.getPrecision());
		transactionsSums.compute(currentElement.getTransactions());
		previousAmount = currentElement.getCurrencyAmount();
	}
	
	public void onTransactionsAmountChange() {
		currentElement.getTransactions().stream().forEach(t->t.updateValue(currencyValue.getPrecision()));
		transactionsSums.compute(currentElement.getTransactions());
	}
	
	public void onTransactionAmountChange(TransactionEntry entry) {
		entry.updateValue(currencyValue.getPrecision());
		transactionsSums.compute(currentElement.getTransactions());
	}
	
	public void newTransactionEntry() {
		currentElement.addTransaction(new TransactionEntry());
	}

	public void splitTransactionEntry() {
		if (selectedTransactionEntry!=null) {
			TransactionEntry newEntry = ExpenseHelper.buildTransactionEntry(
					selectedTransactionEntry.getAccountingDate(),
					selectedTransactionEntry.getAccountingYear(),
					selectedTransactionEntry.getTags(),
					selectedTransactionEntry.getFactor(),
					selectedTransactionEntry.getConsolidation()
			);
			currentElement.addTransaction(newEntry);
			this.currentElement.updateValues(currencyValue.getPrecision());
			transactionsSums.compute(currentElement.getTransactions());
		}
			
	}
	
	public void deleteTransactionEntry() {
		if (selectedTransactionEntry!=null) {
			log.debug("Deleting transaction entry {} for {}", selectedTransactionEntry, selectedTransactionEntry.getTags());
			currentElement.removeTransaction(selectedTransactionEntry);
			transactionsSums.compute(currentElement.getTransactions());
		}
	}
	
	public void addDocumentFile() {
		DocumentFile docFile = documentFileSelector.getCurrentDocumentFile();
		if (docFile != null) {
			currentElement.addDocumentFile(docFile);		
			currentElement.updateDocumentCount();
			documentFileSelector.reset(currentElement, null);
		}
	}
	
	public void deleteDocumentFile() {
		if (selectedDocumentFile!=null) {
			log.debug("Deleting document file {} named {}", selectedDocumentFile, selectedDocumentFile.getFileName());
			currentElement.removeDocumentFile(selectedDocumentFile);
			currentElement.updateDocumentCount();
		}
	}

	public void editPayee() {
		FacesHelper.openEditor("payee", currentElement.getPayee(), EditionMode.EDIT);
	}
	
	public void newPayee() {
		FacesHelper.openEditor("payee", null, EditionMode.EDIT);
	}
	
	public void onPayeeReturn(SelectEvent<EditorReturnValue<Payee>> event) {
    	if (event==null) {
    		log.error("Fuck no event again!!!");
    	} else {
			EditorReturnValue<Payee> value = event.getObject();
			if (value!=null) {
				currentElement.setPayee(value.getElement());
			}
    	}
	}

	@Override
	public void save() {
		this.currentElement.setDate(LocalDateTime.of(currentDate, LocalTime.of(localHours, localMinutes)));
		super.save();
	}
}
