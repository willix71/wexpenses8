package w.expenses8.web.controller;

import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.domain.criteria.PayeeCriteria;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.service.IPayeeService;

@Slf4j
@Named
@ViewScoped
@Getter @Setter
public class PayeeController {

	@Inject
	private IPayeeService payeeService;
	
	private PayeeCriteria criteria = new PayeeCriteria();
	private List<Payee> payees;
	private Payee selectedPayee;
    
	public List<Payee> getPayees() {
		if (payees == null) {
			refresh();
		}
		return payees;
	}
	
	public void refresh() {
		log.debug("Resetting payees matching {}", criteria);
		payees = payeeService.findPayees(criteria);
	}
	
	public void reset() {
		log.debug("Resetting payee criteria");
		criteria.setText(null);
		refresh();
	}
	
	public void save() {
		int index = payees.indexOf(selectedPayee);
		Payee newP = payeeService.save(selectedPayee);
		if (index<0) {
			payees.add(newP);
		} else {
			payees.set(index, newP);
		}
	}

	public void newPayee() {
		selectedPayee = new Payee();
	}
	
	public void delete() {
		int index = payees.indexOf(selectedPayee);
		payeeService.delete(selectedPayee);
		payees.remove(index);
	}
}
