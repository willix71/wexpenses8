package w.expenses8.web.controller;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import w.expenses8.data.domain.model.Consolidation;

@Named
@ViewScoped
@SuppressWarnings("serial")
public class ConsolidationController extends AbstractListEditionController<Consolidation,Consolidation> {

	@Inject
	private ConsolidationEditionController consolidationEditionController;

	public ConsolidationController() {
		super(Consolidation.class);
	}

	@Override
	protected AbstractEditionController<Consolidation> getEditionController() {
		return consolidationEditionController;
	}

}
