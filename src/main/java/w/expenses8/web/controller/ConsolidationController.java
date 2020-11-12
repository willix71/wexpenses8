package w.expenses8.web.controller;

import java.time.LocalDate;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import w.expenses8.data.domain.model.Consolidation;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.web.controller.extra.EditionMode;

@Named
@ViewScoped
public class ConsolidationController extends AbstractListEditionController<Consolidation> {

	private static final long serialVersionUID = 3351336696734127296L;

	@Override
	protected Map<String, Object> getEditorDialogOptions() {
		Map<String, Object> options = super.getEditorDialogOptions();
	    options.put("contentHeight", "2000");
	    options.put("minHeight", "2000");
	    options.put("height", "2000");
	    return options;
	}

	public boolean isSelectedConsolidationNextable() {
		Consolidation conso = getSelectedElement();
		if (conso==null) return false;
		
		Payee p = conso.getInstitution();
		LocalDate d = conso.getDate().plusMonths(1);
		
		return getElements().stream().filter(c->c.getInstitution().equals(p) 
				&& c.getDate().getMonthValue() == d.getMonthValue()
				&& c.getDate().getYear() == d.getYear()
				).findFirst().orElse(null) == null;
	}
	
	public void openNextConsolidation() {
		Consolidation last = getSelectedElement();
		Consolidation next = Consolidation.with().date(last.getDate().plusMonths(1)).institution(last.getInstitution()).openingValue(last.getClosingValue()).build();
		openEditor(next, EditionMode.EDIT);
	}
}
