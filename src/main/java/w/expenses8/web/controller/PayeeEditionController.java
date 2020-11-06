package w.expenses8.web.controller;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import w.expenses8.data.domain.model.Payee;

@Named
@ViewScoped
@Getter @Setter
public class PayeeEditionController extends AbstractEditionController<Payee> {

	private static final long serialVersionUID = 3351336696734127296L;
}
