package w.expenses8.web.controller;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.domain.criteria.ExpenseCriteria;
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
	
	private Expense baseExpense;
	
	private Expense newExpense;
	
	public List<Expense> getLastPayeeExpenses() {
		return expenseService.findExpenses(ExpenseCriteria.with().payee(payee).build());
	}
	
	public void createNewExpense() {
		newExpense = ExpenseHelper.build(expenseType, payee);
		log.info("createNewExpense {}",newExpense);
		editionController.setCurrentElement(newExpense);
	}
	
	public void copyNewExpense() {
		newExpense = ExpenseHelper.build(
				payee, expenseType!=null?expenseType:baseExpense.getExpenseType(), baseExpense.getCurrencyAmount(), baseExpense.getCurrencyCode(), 
				baseExpense.getTransactions().stream().map(e->ExpenseHelper.buildTransactionEntry(e.getTags().stream().filter(t->t.getType()!=TagType.CONSOLIDATION).collect(Collectors.toList()),e.getFactor())).collect(Collectors.toList()));
		log.info("copy new expense {}",newExpense);
		editionController.setCurrentElement(newExpense);
	}	
	
	public void reset() {
		payee = null;
		expenseType = null;
		baseExpense = null;
		newExpense = null;
	}

	public void reselect() {
		baseExpense = null;
		newExpense = null;
	}
}
