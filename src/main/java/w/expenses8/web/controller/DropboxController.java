package w.expenses8.web.controller;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import w.expenses8.data.domain.model.PayeeType;
import w.expenses8.data.domain.service.IPayeeTypeService;

@Named
@ApplicationScoped
public class DropboxController {

	@Inject
	private IPayeeTypeService payeeTypeService;
	
	public List<PayeeType> completePayeeType(String prefix) {
		return payeeTypeService.findBySelectable(prefix);
	}
}
