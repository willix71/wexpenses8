package w.expenses8.web.controller;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.MenuModel;

import w.expenses8.data.domain.model.Consolidation;

@Named
@ViewScoped
public class ConsolidationController extends AbstractListEditionController<Consolidation,Consolidation> {

	private static final long serialVersionUID = 3351336696734127296L;

	@Inject
	private ConsolidationEditionController consolidationEditionController;

	@Override
	protected MenuModel buildMenuModel() {
		MenuModel menuModel = super.buildMenuModel();
		
		DefaultMenuItem item = DefaultMenuItem.builder()
				.value("Next")
				.icon("pi pi-step-forward")
				.update("wexEditionForm:wexDbableEditDialog:wexEditionPanel")
				.oncomplete("PF('w_wexEditionDialog').show()")
				.command("#{controller.setNextElement}"	)
				.build();
		menuModel.getElements().add(3,item);
		
		return menuModel;
	}
	
	public void setNextElement() {
		Consolidation last = convert(getSelectedElement());
		Consolidation next = Consolidation.with().date(last.getDate().plusMonths(1)).institution(last.getInstitution()).openingValue(last.getClosingValue()).build();
		getEditionController().setCurrentElement(next);
		PrimeFaces.current().resetInputs("wexEditionForm");
	}
	
	@Override
	protected AbstractEditionController<Consolidation> getEditionController() {
		return consolidationEditionController;
	}

}
