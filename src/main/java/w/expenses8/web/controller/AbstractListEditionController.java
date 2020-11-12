package w.expenses8.web.controller;

import java.util.Map;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.menu.MenuModel;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.core.model.DBable;
import w.expenses8.web.controller.extra.EditionMode;
import w.expenses8.web.controller.extra.EditorReturnValue;
import w.expenses8.web.controller.extra.FacesHelper;

@Slf4j
@Getter @Setter
public abstract class AbstractListEditionController<T extends DBable<T>> extends AbstractListController<T> {

	private static final long serialVersionUID = 3351336696734127296L;
	
	private boolean expandAllRows = false;
	
	private MenuModel menuModel;
	
	public AbstractListEditionController() {
		super();
	}

	public AbstractListEditionController(Class<T> clazz) {
		super(clazz);
	}
	
	public String getRowStyleClass(T element) {
		return "";
	}
	
	public void openViewElement() {
		openEditor(convert(getSelectedElement()),EditionMode.VIEW);
	}

	public void openDeleteElement() {
		openEditor(convert(getSelectedElement()),EditionMode.DELETE);
	}
	
	public void openEditElement() {
		openEditor(convert(getSelectedElement()),EditionMode.EDIT);
	}
	
	public void openRowDoubleClick(final SelectEvent<T> event) {
		openEditor(convert(event.getObject()),EditionMode.EDIT);
	}
	
	public void openNewElement() {
		openEditor(null,EditionMode.EDIT);
	}
	
	protected DBable<?> convert(T t) {
		return t;
	}
	
	protected String getEditorsPage() {
		return clazz.getSimpleName().toLowerCase();
	}
		
	protected Map<String,Object> getEditorDialogOptions() {
	    return FacesHelper.getDefaultDialogOptions();
	}
	
	public void openEditor(DBable<?> e, EditionMode mode) {
		log.info("opening editor for {} in mode {}", e, mode);
		FacesHelper.openEditor(e, mode, getEditorsPage(), getEditorDialogOptions());
	}
	
    public void onReturnFromEdition(SelectEvent<EditorReturnValue<DBable<?>>> event) {
    	loadEntities();
    	EditorReturnValue<DBable<?>> value = event.getObject();
    	if (value!=null) {
    		showMessage(value.getEvent(), value.getElement());
    	}
    }
}
