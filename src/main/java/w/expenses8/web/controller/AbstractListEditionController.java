package w.expenses8.web.controller;

import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

import lombok.Getter;
import lombok.Setter;
import w.expenses8.data.core.model.DBable;

@Getter @Setter
public abstract class AbstractListEditionController<T extends DBable<T>, E extends DBable<E>> extends AbstractListController<T> {

	private static final long serialVersionUID = 3351336696734127296L;
	
	private boolean expandAllRows = false;
	
	private MenuModel menuModel;
	
	public AbstractListEditionController(Class<T> clazz) {
		super(clazz);
		buildMenuModel();
	}

	protected abstract AbstractEditionController<E> getEditionController();
	
	protected MenuModel buildMenuModel() {
		menuModel = new DefaultMenuModel();
		
		DefaultMenuItem item = DefaultMenuItem.builder()
                .value("View")
                .icon("pi pi-eye")
                .update("wexDetailPanel")
                .oncomplete("PF('w_wexDetailDialog').show()")
                .command("#{controller.setViewElement}"	)
                .build();
		menuModel.getElements().add(item);

		item = DefaultMenuItem.builder()
				.value("Edit")
				.icon("pi pi-pencil")
				.update("wexEditionForm:wexDbableEditDialog:wexEditionPanel")
				.oncomplete("PF('w_wexEditionDialog').show()")
				.command("#{controller.setEditionElement}"	)
				.build();
		menuModel.getElements().add(item);

		item = DefaultMenuItem.builder()
				.value("New")
				.icon("pi pi-plus")
				.update("wexEditionForm:wexDbableEditDialog:wexEditionPanel")
				.oncomplete("PF('w_wexEditionDialog').show()")
				.command("#{controller.setNewElement}"	)
				.build();
		menuModel.getElements().add(item);

		item = DefaultMenuItem.builder()
                .value("Delete")
                .icon("pi pi-trash")
                .update("wexDeletePanel")
                .oncomplete("PF('w_wexDeleteDialog').show()")
                .command("#{controller.setViewElement}"	)
                .build();
		menuModel.getElements().add(item);

		item = DefaultMenuItem.builder()
				.value("Refresh")
				.icon("pi pi-replay")
				.update("wexTable wexTableForm:growl")
				.command("#{controller.refresh}")
				.build();
		menuModel.getElements().add(item);
			
		return menuModel;
	}
	
	public String getRowStyleClass(T element) {
		return "";
	}
	
	public void setViewElement() {
		getEditionController().setCurrentElement(convert(getSelectedElement()));
	}

	public void setEditionElement() {
		getEditionController().setCurrentElement(convert(getSelectedElement()));
		PrimeFaces.current().resetInputs("wexEditionForm");
	}
	
	public void onRowDoubleClick(final SelectEvent<T> event) {
		getEditionController().setCurrentElement( convert(event.getObject()));
		PrimeFaces.current().resetInputs("wexEditionForm");
	}
	
	public void setNewElement() {
		getEditionController().setCurrentElement( null);
		PrimeFaces.current().resetInputs("wexEditionForm");
	}

	public void saveElement() {
		getEditionController().save();
		loadEntities();
	}

	public void deleteElement() {
		getEditionController().delete();
	}
	
	@SuppressWarnings("unchecked")
	public E convert(T t) {
		return (E) t;
	}
}
