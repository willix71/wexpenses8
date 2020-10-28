package w.expenses8.web.controller;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import w.expenses8.data.domain.model.Consolidation;

@Named
@ViewScoped
@SuppressWarnings("serial")
public class ConsolidationController extends AbstractListController<Consolidation> {

	public String prepareNextConsolidation() throws IOException {
		Consolidation selectedConso = getSelectedElement();
		if (selectedConso==null) {
			return null;
		} else {
			Consolidation nextConso = Consolidation.with().date(selectedConso.getDate().plusMonths(1)).institution(selectedConso.getInstitution()).openingValue(selectedConso.getClosingValue()).build();
			FacesContext.getCurrentInstance().getExternalContext().getFlash().put(ConsolidationEditionController.NEXT_CONSOLIDATION_FLASH_ID, nextConso);
			FacesContext.getCurrentInstance().getExternalContext().redirect("newConsolidation.xhtml");
			return null;
		}
	}
	
	public String prepareEditConsolidation() throws IOException {
		Consolidation selectedConso = getSelectedElement();
		if (selectedConso==null) {
			return null;
		} else {
			FacesContext.getCurrentInstance().getExternalContext().redirect("newConsolidation.xhtml?id="+selectedConso.getId());
			return null;
		}
	}
}
