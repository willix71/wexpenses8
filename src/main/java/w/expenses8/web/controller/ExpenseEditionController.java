package w.expenses8.web.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

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
import w.expenses8.data.utils.ExpenseHelper;
import w.expenses8.data.utils.TransactionsSums;

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
	
	private LocalDateTime previousDate;
	private BigDecimal previousAmount;
	private Map<String,ExchangeRate> potentialExchangeRates = new HashMap<String, ExchangeRate>();
	
	private DocumentFile selectedDocumentFile;
	
	private TransactionEntry selectedTransactionEntry;
	
	private TransactionsSums transactionsSums = new TransactionsSums();
	
	@Override
	protected void initCurrentElement() {
		this.potentialExchangeRates.clear();

		if (currentElement != null) {
			this.previousDate = currentElement.getDate();
			this.previousAmount = currentElement.getCurrencyAmount();
			
			if (currentElement.getExchangeRate()!=null) {
				this.potentialExchangeRates.put(currentElement.getExchangeRate().getFromCurrencyCode(), currentElement.getExchangeRate());
			}
			transactionsSums.compute(currentElement.getTransactions());
		}
		documentFileSelector.reset();
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
		log.debug("handleDateChange old {} new {}",previousDate, newdate);
		currentElement.updateDate(previousDate);
		previousDate = newdate;
	}
	
	public void onDateChange() {
		log.debug("onDateChange old {} new {}",previousDate, currentElement.getDate());
		currentElement.updateDate(previousDate);
		previousDate = currentElement.getDate();
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
