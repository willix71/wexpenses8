package w.expenses8.web.controller;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import w.expenses8.data.domain.model.ExpenseType;

@Named
@ViewScoped
@SuppressWarnings("serial")
public class ExpenseTypeController extends AbstractTypeListController<ExpenseType> { }