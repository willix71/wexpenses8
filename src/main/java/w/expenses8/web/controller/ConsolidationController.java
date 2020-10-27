package w.expenses8.web.controller;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import w.expenses8.data.domain.model.Consolidation;

@Named
@ViewScoped
@SuppressWarnings("serial")
public class ConsolidationController extends AbstractListController<Consolidation> {

	public String prepareNextConsolidation() {
		Consolidation selectedConso = getSelectedElement();
		if (selectedConso==null) {
			return null;
		} else {
			Consolidation nextConso = Consolidation.with().date(selectedConso.getDate().plusMonths(1)).institution(selectedConso.getInstitution()).openingValue(selectedConso.getClosingValue()).build();
			FacesContext.getCurrentInstance().getExternalContext().getFlash().put(ConsolidationEditionController.NEXT_CONSOLIDATION_FLASH_ID, nextConso);
			return "newConsolidation.xhtml";
		}
	}
}
