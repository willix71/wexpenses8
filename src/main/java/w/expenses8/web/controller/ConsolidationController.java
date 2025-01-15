package w.expenses8.web.controller;

import java.time.LocalDate;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import w.expenses8.data.domain.model.Consolidation;
import w.expenses8.data.domain.model.DocumentFile;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.service.IDocumentFileService;
import w.expenses8.web.controller.extra.EditionMode;

@Named
@ViewScoped
public class ConsolidationController extends AbstractListEditionController<Consolidation> {

	private static final long serialVersionUID = 3351336696734127296L;

	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private IDocumentFileService documentFileService;
	
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
	
	public MenuModel getDocumentFileMenu(DocumentFile file) {
		MenuModel model= new DefaultMenuModel();
		if (file != null) {
			model.getElements().add(DefaultMenuItem.builder()
					.value(file.getFileName())
					.icon("pi pi-circle-on")
					.url(documentFileService.getUrl(file))
					.target("_blank")
					.build());
		}
		return model;
	}
}
