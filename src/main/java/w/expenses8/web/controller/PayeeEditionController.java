package w.expenses8.web.controller;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.service.IPayeeService;

@Named
@ViewScoped
@Getter @Setter
public class PayeeEditionController extends AbstractEditionController<Payee> {

	private static final long serialVersionUID = 3351336696734127296L;

	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private IPayeeService payeeService;
	
	@Override
	public void setCurrentElementId(Object o) {
		this.currentElement = payeeService.reload(o);
	}
	
	@Override
	public void setCurrentElement(Payee payee) {
		this.currentElement = payeeService.reload(payee);
	}
}
