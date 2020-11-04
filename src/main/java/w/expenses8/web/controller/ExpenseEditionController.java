package w.expenses8.web.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

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
import w.expenses8.data.domain.service.IExpenseService;
import w.expenses8.data.domain.service.IPayeeService;
import w.expenses8.data.domain.service.impl.CountryService;
import w.expenses8.data.utils.ExpenseHelper;
import w.expenses8.data.utils.TransactionsSums;

@Slf4j
@Named
@ViewScoped
@Getter @Setter
public class ExpenseEditionController extends AbstractEditionController<Expense> {

	private static final long serialVersionUID = 3351336696734127296L;

	@Inject
	private DocumentFileSelector documentFileSelector;
	
	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private CountryService countryService;
	
	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private IExpenseService expenseService;
	
	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private IPayeeService payeeService;
	
	@Inject
	private CurrencyValue currencyValue;
	
	private LocalDateTime currentDate;
	private BigDecimal currentAmount;
	private Map<String,ExchangeRate> potentialExchangeRates = new HashMap<String, ExchangeRate>();
	
	private DocumentFile selectedDocumentFile;
	private TransactionEntry selectedTransactionEntry;
	
	private TransactionsSums transactionsSums = new TransactionsSums();
	
	@Override
	public void setCurrentElementId(Object o) {
		this.currentElement = expenseService.reload(o);
		initCurrentElement();
	}
	
	@Override
	public void setCurrentElement(Expense expense) {
		this.currentElement = expenseService.reload(expense);
		initCurrentElement();
	}
	
	public void initCurrentElement() {
		this.potentialExchangeRates.clear();

		if (currentElement != null) {
			this.currentDate = currentElement.getDate();
			this.currentAmount = currentElement.getCurrencyAmount();
			
			if (currentElement.getExchangeRate()!=null) {
				this.potentialExchangeRates.put(currentElement.getExchangeRate().getFromCurrencyCode(), currentElement.getExchangeRate());
			}
			transactionsSums.compute(currentElement.getTransactions());
		}
		documentFileSelector.reset();
	}
	
	@Override
	public void save() {
		// check for similar expense
		if (expenseService.findSimiliarExpenses(getCurrentElement()).size() != 0) {
			
		}
		try {
			super.save();
		} catch(ConstraintViolationException ex) {
			for(ConstraintViolation<?> viol: ex.getConstraintViolations()) {
				log.warn("ConstraintViolation for {} : {}", viol.getPropertyPath(), viol.getMessage());
				FacesContext.getCurrentInstance().addMessage("ValidationErrors", new FacesMessage(FacesMessage.SEVERITY_ERROR, viol.getPropertyPath().toString(), viol.getMessage()));
			}
		}
	}
	
	public void onAccountRowDoubleClick(final SelectEvent<TransactionEntry> event) {
		TransactionEntry t = event.getObject();
		setCurrentElement(t.getExpense());
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
	
	public void handleDateChange(SelectEvent<LocalDateTime> event) {
		LocalDateTime newdate = event.getObject();
		log.debug("handleDateChange old {} new {}",currentDate, newdate);
		currentElement.updateDate(currentDate);
		currentDate = newdate;
	}
	
	public void onDateChange() {
		log.debug("onDateChange old {} new {}",currentDate, currentElement.getDate());
		currentElement.updateDate(currentDate);
		currentDate = currentElement.getDate();
	}
	
	public void onCurrencyChange() {
		String newCurrencyCode = currentElement.getCurrencyCode();
		log.debug("onCurrencyChange {}", newCurrencyCode);
		if (currencyValue.getCode().equals(newCurrencyCode)) {
			currentElement.setExchangeRate(null);
		} else {
			ExchangeRate potentialExchangeRate = potentialExchangeRates.get(newCurrencyCode);
			if (potentialExchangeRate!=null) {
				currentElement.setExchangeRate(potentialExchangeRate);
			} else {
				Long institutionId = currentElement.getTransactions().stream().flatMap(f->f.getTags().stream()).filter(f->f.getInstitution()!=null && f.getInstitution().getId()!=null).map(f->f.getInstitution().getId()).findFirst().orElse(null);
				Payee institution = institutionId==null?null:payeeService.load(institutionId);
				
				// new expense rate
				ExchangeRate newExchangeRate = ExchangeRate.with()
						.institution(institution)
						.date(currentElement.getDate()==null?null:currentElement.getDate().toLocalDate())
						.fromCurrencyCode(newCurrencyCode)
						.toCurrencyCode(currencyValue.getCode())
						.rate(1.2)
						.build();
				
				potentialExchangeRates.put(newCurrencyCode, newExchangeRate);
				currentElement.setExchangeRate(newExchangeRate);
			}
		}
	}
	
	public void onAmountChange() {
		log.debug("onAmountChange old {} new {}",currentAmount, currentElement.getCurrencyAmount());
		currentElement.updateAmountValues(currentAmount, currencyValue.getPrecision());
		transactionsSums.compute(currentElement.getTransactions());
		currentAmount = currentElement.getCurrencyAmount();
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
			documentFileSelector.reset();
		}
	}
	
	public void deleteDocumentFile() {
		if (selectedDocumentFile!=null) {
			log.debug("Deleting document file {} named {}", selectedDocumentFile, selectedDocumentFile.getFileName());
			currentElement.removeDocumentFile(selectedDocumentFile);
			currentElement.updateDocumentCount();
		}
	}

}
