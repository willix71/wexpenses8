package w.expenses8.web.controller;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import w.expenses8.data.domain.model.Consolidation;
import w.expenses8.data.domain.service.IConsolidationService;

@Named
@ViewScoped
@Getter @Setter
public class ConsolidationEditionController extends AbstractEditionController<Consolidation> {

	private static final long serialVersionUID = 3351336696734127296L;

	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private IConsolidationService consolidationService;
	
	@Override
	public void setCurrentElementId(Object o) {
		this.currentElement = consolidationService.reload(o);
	}
	
	@Override
	public void setCurrentElement(Consolidation conso) {
		this.currentElement = consolidationService.reload(conso);
	}

}
