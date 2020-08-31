package w.expenses8.web.controller;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import w.expenses8.data.domain.model.Payee;

@Named
@ViewScoped
@SuppressWarnings("serial")
public class PayeeController extends AbstractListController<Payee>{

	@Override
	protected String nameOf(Payee entity) {
		return entity == null?super.nameOf(entity):super.nameOf(entity) + " [" + entity.getName() + "]";
	}
}
