package w.expenses8.web.controller;

import javax.faces.context.FacesContext;
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

	static final String NEXT_CONSOLIDATION_FLASH_ID = "_NEXT_CONSOLIDATION_";
	
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

	@Override
	protected Object getInitialElementId() {
		Object id = super.getInitialElementId();
		if (id == null) {
			id = FacesContext.getCurrentInstance().getExternalContext().getFlash().get(NEXT_CONSOLIDATION_FLASH_ID);
			FacesContext.getCurrentInstance().getExternalContext().getFlash().put(NEXT_CONSOLIDATION_FLASH_ID,null);
		}
		return id;
	}

}
