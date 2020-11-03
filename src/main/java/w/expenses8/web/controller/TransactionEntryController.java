package w.expenses8.web.controller;

import java.math.BigDecimal;
import java.time.Year;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

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
public class TransactionEntryController extends AbstractListEditionController<TransactionEntry, Expense> {

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
	

	public TransactionEntryController() {
		super(TransactionEntry.class);
	}

	@Override
	protected AbstractEditionController<Expense> getEditionController() {
		return expenseEditionController;
	}
	
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
	public Expense convert(TransactionEntry t) {
		return t.getExpense();
	}
}
