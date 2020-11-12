package w.expenses8.web.controller;

import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import w.expenses8.data.domain.model.Consolidation;
import w.expenses8.web.controller.extra.EditionMode;

@Named
@ViewScoped
public class ConsolidationController extends AbstractListEditionController<Consolidation,Consolidation> {

	private static final long serialVersionUID = 3351336696734127296L;

	@Override
	protected Map<String, Object> getEditorDialogOptions() {
		Map<String, Object> options = super.getEditorDialogOptions();
	    options.put("contentHeight", "2000");
	    options.put("minHeight", "2000");
	    options.put("height", "2000");
	    return options;
	}

	public void openNextConsolidation() {
		Consolidation last = convert(getSelectedElement());
		Consolidation next = Consolidation.with().date(last.getDate().plusMonths(1)).institution(last.getInstitution()).openingValue(last.getClosingValue()).build();
		openEditor(next, EditionMode.EDIT);
	}
}
