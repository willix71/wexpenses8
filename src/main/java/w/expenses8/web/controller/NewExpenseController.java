package w.expenses8.web.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.config.CurrencyValue;
import w.expenses8.data.core.model.DBable;
import w.expenses8.data.domain.criteria.ExpenseCriteria;
import w.expenses8.data.domain.model.ExchangeRate;
import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.model.ExpenseType;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.model.enums.TagType;
import w.expenses8.data.domain.service.IExchangeRateService;
import w.expenses8.data.domain.service.IExpenseService;
import w.expenses8.data.utils.CollectionHelper;
import w.expenses8.data.utils.ExpenseHelper;
import w.expenses8.web.controller.extra.EditionMode;
import w.expenses8.web.controller.extra.EditorReturnValue;
import w.expenses8.web.controller.extra.FacesHelper;
import w.expenses8.web.converter.DbableConverter;

@Slf4j
@Named
@ViewScoped
@Getter @Setter
public class NewExpenseController extends ExpenseController {
	
	private static final long serialVersionUID = 3351336696734127296L;

	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private IExpenseService expenseService;
	
	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private IExchangeRateService exchangeRateService;
	
	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private CurrencyValue currencyValue;
	
	private Payee payee;

	private ExpenseType expenseType;
	
	private LocalDate referenceDate;

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
	
	public boolean isPayeeSelected() { 
		return payee != null;
	}
	
	public void baseExpenseSelected() {
		log.debug("base expense selected {}",baseExpense);
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
		Expense newExpense = ExpenseHelper.build(expenseType, payee, currencyValue);
		
		log.info("created new expense {}",newExpense);
		openEditor(newExpense, EditionMode.EDIT);
	}
	
	public void newDuplicatedExpense() {
		Set<String> options = new HashSet<>(Arrays.asList(copyOptions));
		Expense newExpense = ExpenseHelper.build(
				payee, expenseType!=null?expenseType:baseExpense.getExpenseType(), options.contains("date")?baseExpense.getDate():null,options.contains("amount")?baseExpense.getCurrencyAmount():null, baseExpense.getCurrencyCode(), 
				baseExpense.getTransactions().stream().map(e->ExpenseHelper.buildTransactionEntry(e.getTags().stream().filter(t->t.getType()!=TagType.CONSOLIDATION).collect(Collectors.toList()),e.getFactor())).collect(Collectors.toList()));
		if (options.contains("reference")) {
			newExpense.setExternalReference(baseExpense.getExternalReference());
		}
		if (options.contains("documents")) {
			CollectionHelper.stream(baseExpense.getDocumentFiles()).forEach(d->newExpense.addDocumentFile(d));
		}
		if (!currencyValue.isDefaultCurrency(newExpense.getCurrencyCode())) {
			ExchangeRate xr = options.contains("rate")?exchangeRateService.copyExchangeRate(baseExpense.getExchangeRate()):exchangeRateService.buildExchangeRate(newExpense);
			xr.setDate(newExpense.getDate()==null?null:newExpense.getDate().toLocalDate());
			newExpense.setExchangeRate(xr);
		}
		log.info("duplicated new expense {}",newExpense);
		openEditor(newExpense, EditionMode.EDIT);
	}	

	@Override
	protected Map<String, List<String>> getDefaultEditionParam(DBable<?> e, EditionMode mode) {
		Map<String, List<String>> params = super.getDefaultEditionParam(e, mode);
		if (referenceDate!=null) {
			params.put(LocalDateCustomConverterController.referenceDateParam, Collections.singletonList(referenceDate.format(LocalDateCustomConverterController.referenceDateFormat)));
		}
		return params;
	}

	@Override
    public void onReturnFromEdition(SelectEvent<EditorReturnValue<DBable<?>>> event) {
		payee = null;
		expenseType = null;
		baseExpense = null;
		copyOptions = null;
		super.onReturnFromEdition(event);
	}
		
	public Converter<Expense> getExpenseConverter() {
		return converter;
	}
	
	private Converter<Expense> converter = new DbableConverter<Expense>() {
		@Override
		public Expense getAsObject(FacesContext fc, UIComponent uic, String uid) {
			return CollectionHelper.stream(lastPayeeExpenses).filter(e->uid.equals(e.getUid())).findFirst().orElseGet(null);
		}		
	};
	
	public void editPayee() {
		FacesHelper.openEditor("payee", payee, EditionMode.EDIT);
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
				payee = value.getElement();
			}
    	}
	}

}
