package w.expenses8.web.controller;

import java.io.Serializable;
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
import w.expenses8.data.utils.ExpenseHelper;

@Slf4j
@Named
@ViewScoped
@Getter @Setter
@SuppressWarnings("serial")
public class NewExpenseController implements Serializable {
	
	@Inject
	private ExpenseEditionController editionController;

	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private IExpenseService expenseService;
	
	private Payee payee;

	private ExpenseType expenseType;

	private List<Expense> lastPayeeExpenses;
	
	private String[] copyOptions;
	
	private Expense baseExpense;
	
	private Expense newExpense;
	
	public void setPayee(Payee p) {
		this.payee = p;
		this.copyOptions = null;
		this.lastPayeeExpenses = p == null?null:expenseService.findExpenses(ExpenseCriteria.with().payee(payee).build());
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

	public void createNewExpense() {
		newExpense = ExpenseHelper.build(expenseType, payee);
		log.info("createNewExpense {}",newExpense);
		editionController.setCurrentElement(newExpense);
	}
	
	public void copyNewExpense() {
		Set<String> options = new HashSet<>(Arrays.asList(copyOptions));
		newExpense = ExpenseHelper.build(
				payee, expenseType!=null?expenseType:baseExpense.getExpenseType(), options.contains("date")?baseExpense.getDate():null,baseExpense.getCurrencyAmount(), baseExpense.getCurrencyCode(), 
				baseExpense.getTransactions().stream().map(e->ExpenseHelper.buildTransactionEntry(e.getTags().stream().filter(t->t.getType()!=TagType.CONSOLIDATION).collect(Collectors.toList()),e.getFactor())).collect(Collectors.toList()));
		if (options.contains("reference")) {
			newExpense.setExternalReference(baseExpense.getExternalReference());
		}
		if (options.contains("documents")) {
			newExpense.setDocumentFiles(new HashSet<>(baseExpense.getDocumentFiles()));
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
		log.info("copy new expense {}",newExpense);
		editionController.setCurrentElement(newExpense);
	}	
	
	public void reset() {
		payee = null;
		expenseType = null;
		baseExpense = null;
		newExpense = null;
		copyOptions = null;
	}

	public void reselect() {
		baseExpense = null;
		newExpense = null;
		copyOptions = null;
	}
}
