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
import w.expenses8.data.domain.service.IExpenseService;

@Slf4j
@Named
@ViewScoped
@Getter @Setter
public class ExpenseEditionController extends AbstractEditionController<Expense> {

	private static final long serialVersionUID = 3351336696734127296L;

	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private IExpenseService expenseService;
	
	@Inject
	private CurrencyValue currencyValue;
	
	private LocalDateTime currentDate;
	private BigDecimal currentAmount;
	private Map<String,ExchangeRate> potentialExchangeRates = new HashMap<String, ExchangeRate>();
	
	private DocumentFile selectedDocumentFile;
	private TransactionEntry selectedTransactionEntry;
	
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
		}	
	}
	
	public void handleDateChange(SelectEvent<LocalDateTime> event) {
		LocalDateTime newdate = event.getObject();
		log.info("handleDateChange old {} new {}",currentDate, newdate);
		currentElement.updateDate(currentDate);
		currentDate = newdate;
	}
	
	public void onDateChange() {
		log.info("onDateChange old {} new {}",currentDate, currentElement.getDate());
		currentElement.updateDate(currentDate);
		currentDate = currentElement.getDate();
	}
	
	public void onCurrencyChange() {
		String newCurrencyCode = currentElement.getCurrencyCode();
		log.debug("onCurrencyChange {}",newCurrencyCode);
		if (currencyValue.getCode().equals(newCurrencyCode)) {
			currentElement.setExchangeRate(null);
		} else {
			ExchangeRate potentialExchangeRate = potentialExchangeRates.get(newCurrencyCode);
			if (potentialExchangeRate!=null) {
				currentElement.setExchangeRate(potentialExchangeRate);
			} else {
				Payee institution = null; // lookup institution
				// new expense rate
				ExchangeRate newExchangeRate = ExchangeRate.with()
						.institution(institution)
						.date(currentElement.getDate().toLocalDate())
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
		log.info("onAmountChange old {} new {}",currentAmount, currentElement.getCurrencyAmount());
		currentElement.updateAmountValues(currentAmount, currencyValue.getPrecision());
		currentAmount = currentElement.getCurrencyAmount();
	}
	
	public void newDocumentFile() {
		currentElement.addDocumentFile(new DocumentFile(currentElement.getDate()==null?null:currentElement.getDate().toLocalDate(),null));
		currentElement.updateDocumentCount();
	}
	
	public void deleteDocumentFile() {
		if (selectedDocumentFile!=null) {
			log.info("Deleting document file {} named {}", selectedDocumentFile, selectedDocumentFile.getFileName());
			currentElement.getDocumentFiles().remove(selectedDocumentFile);
			selectedDocumentFile.setExpense(null);
			currentElement.updateDocumentCount();
		}
	}
	
	public void newTransactionEntry() {
		currentElement.addTransaction(new TransactionEntry());
	}
	
	public void deleteTransactionEntry() {
		if (selectedTransactionEntry!=null) {
			log.info("Deleting transaction entry {} for {}", selectedTransactionEntry, selectedTransactionEntry.getTags());
			currentElement.getTransactions().remove(selectedTransactionEntry);
			selectedTransactionEntry.setExpense(null);
		}
	}
}
