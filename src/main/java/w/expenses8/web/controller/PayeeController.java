package w.expenses8.web.controller;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import w.expenses8.data.domain.model.Payee;

@Named
@ViewScoped
public class PayeeController extends AbstractListEditionController<Payee,Payee>{

	private static final long serialVersionUID = 3351336696734127296L;

	@Inject 
	private PayeeEditionController payeeEditionController;

	@Override
	protected AbstractEditionController<Payee> getEditionController() {
		return payeeEditionController;
	}
}
