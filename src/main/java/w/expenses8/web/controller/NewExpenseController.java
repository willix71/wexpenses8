package w.expenses8.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.domain.criteria.ExpenseCriteria;
import w.expenses8.data.domain.model.ExchangeRate;
import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.model.ExpenseType;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.model.enums.TagType;
import w.expenses8.data.domain.service.IExpenseService;
import w.expenses8.data.utils.CollectionHelper;
import w.expenses8.data.utils.ExpenseHelper;

@Slf4j
@Named
@ViewScoped
@Getter @Setter
@SuppressWarnings("serial")
public class NewExpenseController extends ExpenseController {
	
	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private IExpenseService expenseService;
	
	private Payee payee;

	private ExpenseType expenseType;

	private List<Expense> lastPayeeExpenses;
	
	private String[] copyOptions;
	
	private Expense baseExpense;
	
	public void setPayee(Payee p) {
		this.payee = p;
		this.copyOptions = null;
		this.lastPayeeExpenses = p == null?null:expenseService.findExpenses(ExpenseCriteria.with().payee(payee).build());
	}
	
	@Override
	protected void loadEntities() {
		log.info("loading recent expenses...");
		elements = expenseService.findLatestExpenses();
	}
	
	public void baseExpenseSelected() {
		log.info("base expense selected {}",baseExpense);
		List<String> potentialOptions = new ArrayList<String>();		
		if (baseExpense.getExchangeRate() != null) {
			potentialOptions.add("rate");
		}
		if (baseExpense.getExternalReference() != null) {
			for(Expense x: this.lastPayeeExpenses) {
				if (!x.equals(baseExpense) && baseExpense.getExternalReference().equals(x.getExternalReference())) {
					potentialOptions.add("reference");
				}
			}
		}
		copyOptions = potentialOptions.toArray(new String[potentialOptions.size()]);
	}

	public void newEmptyExpense() {
		Expense newExpense = ExpenseHelper.build(expenseType, payee);
		log.info("created new expense {}",newExpense);
		getEditionController().setCurrentElement(newExpense);
	}
	
	public void newDuplicatedExpense() {
		Set<String> options = new HashSet<>(Arrays.asList(copyOptions));
		Expense newExpense = ExpenseHelper.build(
				payee, expenseType!=null?expenseType:baseExpense.getExpenseType(), options.contains("date")?baseExpense.getDate():null,baseExpense.getCurrencyAmount(), baseExpense.getCurrencyCode(), 
				baseExpense.getTransactions().stream().map(e->ExpenseHelper.buildTransactionEntry(e.getTags().stream().filter(t->t.getType()!=TagType.CONSOLIDATION).collect(Collectors.toList()),e.getFactor())).collect(Collectors.toList()));
		if (options.contains("reference")) {
			newExpense.setExternalReference(baseExpense.getExternalReference());
		}
		if (options.contains("documents")) {
			CollectionHelper.stream(baseExpense.getDocumentFiles()).forEach(d->newExpense.addDocumentFile(d));
		}
		if (options.contains("rate") && baseExpense.getExchangeRate()!=null) {
			newExpense.setExchangeRate(ExchangeRate.with()
					.date(newExpense.getDate()==null?null:newExpense.getDate().toLocalDate())
					.institution(baseExpense.getExchangeRate().getInstitution())
					.fromCurrencyCode(baseExpense.getExchangeRate().getFromCurrencyCode())
					.toCurrencyCode(baseExpense.getExchangeRate().getToCurrencyCode())
					.rate(baseExpense.getExchangeRate().getRate())
					.fee(baseExpense.getExchangeRate().getFee())
					.fixFee(baseExpense.getExchangeRate().getFixFee())
					.build());
		}
		log.info("duplicated new expense {}",newExpense);
		getEditionController().setCurrentElement(newExpense);
	}	

	@Override
	public void saveElement() {
		super.saveElement();
		payee = null;
		expenseType = null;
		baseExpense = null;
		copyOptions = null;
	}
}
