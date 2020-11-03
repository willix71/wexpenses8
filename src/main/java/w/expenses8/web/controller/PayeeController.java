package w.expenses8.web.controller;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import w.expenses8.data.domain.model.Payee;

@Named
@ViewScoped
@SuppressWarnings("serial")
public class PayeeController extends AbstractListEditionController<Payee,Payee>{

	@Inject 
	private PayeeEditionController payeeEditionController;
	
	public PayeeController() {
		super(Payee.class);
	}

	@Override
	protected AbstractEditionController<Payee> getEditionController() {
		return payeeEditionController;
	}

	@Override
	protected String nameOf(Payee entity) {
		return entity == null?super.nameOf(entity):super.nameOf(entity) + " [" + entity.getName() + "]";
	}
}
