package w.expenses8.web.controller;

import java.time.Instant;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.primefaces.PrimeFaces;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import w.expenses8.WexpensesConstants;
import w.expenses8.data.core.model.DBable;
import w.expenses8.data.core.service.IGenericService;
import w.expenses8.data.core.service.IReloadableService;
import w.expenses8.data.domain.validation.Warning;
import w.expenses8.web.controller.extra.EditionMode;
import w.expenses8.web.controller.extra.EditorReturnValue;
import w.expenses8.web.controller.extra.FacesHelper;

@Slf4j
@Getter @Setter
public abstract class AbstractEditionController<T extends DBable<T>> extends AbstractController<T> {

	private static final long serialVersionUID = 3351336696734127297L;
	
	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	protected IGenericService<T, ?> elementService;
	
	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	protected IReloadableService<T> reloadableService;
	
	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	protected Validator validator;
	
	protected T currentElement;

	private EditionMode mode = EditionMode.VIEW;
	
	private boolean inDialog = false;
	
	private Instant lastWarningTime;
	
	public AbstractEditionController() {
		String pMode=((HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequest())).getParameter("mode");
		if (pMode!=null) {
			inDialog = true;
			mode=EditionMode.valueOf(pMode);
		}
	}
	
	@PostConstruct
	public void initSelectedElementId() {
		Object id = getInitialElementId();
		// only set the initial element if we are sure it has to be set
		if (id != null) resetCurrentElement(id);
	}
	
	protected Object getInitialElementId() {
		String id=((HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequest())).getParameter("id");
		if (id!=null) {
			if (id.equalsIgnoreCase("new")) {
				log.info("Displaying new element");
				return WexpensesConstants.NEW_INSTANCE;
			} else if (id.equalsIgnoreCase("flash")) {
				log.info("Displaying retrieved element");
				return FacesHelper.retrieveElement();
			} else {
				log.info("Displaying element by id");
				return Long.parseLong(id);
			}
		} else {
			String uid=((HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequest())).getParameter("uid");
			if (uid!=null) {
				log.info("Displaying element by uid");
				return uid;
			}
		}
		return null;
	}
	
	public boolean isEditable() {
		return mode==EditionMode.EDIT;
	}
	
	public void setEditable(boolean editable) {
		mode=editable?EditionMode.EDIT:EditionMode.VIEW;
	}
	
	public boolean isViewMode() {
		return inDialog && mode==EditionMode.VIEW;
	}
	
	public boolean isDeleteMode() {
		return inDialog && mode==EditionMode.DELETE;
	}
	
	public boolean isEditMode() {
		return inDialog && mode==EditionMode.EDIT;
	}
	
	public boolean isPageMode() {
		return !inDialog;
	}

	public void closeDialog() {
		returnFromDialog(null);		
	}
	
	public void saveAndCloseDialog() {
		if (isValid()) {
			save();
			returnFromDialog("Saved");
		}
	}
	
	public void validateAndSave() {
		if (isValid()) {
			save();
		}
	}

	public boolean isValid() {
		if (currentElement != null) {
			if (hasValidationErrors()) return false;
			if (hasValidationWarnings()) return false;
		}
		return true;
	}
	
	protected boolean hasValidationErrors() { 
		Set<ConstraintViolation<T>> violations = validator.validate(currentElement);
		if (!violations.isEmpty()) {
			for(ConstraintViolation<?> viol: violations) {
				log.warn("ConstraintViolation errors for {} : {}", viol.getPropertyPath(), viol.getMessage());
				FacesContext.getCurrentInstance().addMessage("ValidationErrors", new FacesMessage(FacesMessage.SEVERITY_ERROR, viol.getPropertyPath().toString(), viol.getMessage()));
			}
			return true;
		}
		return false;
	}
	
	protected boolean hasValidationWarnings() {
		Instant now = Instant.now();
		if (lastWarningTime==null || now.isAfter(lastWarningTime.plusSeconds(5))) {
			lastWarningTime = now;
			Set<ConstraintViolation<T>>  violations = validator.validate(currentElement, Warning.class);
			if (!violations.isEmpty()) {
				for(ConstraintViolation<?> viol: violations) {
					log.warn("ConstraintViolation warnings for {} : {}", viol.getPropertyPath(), viol.getMessage());
					FacesContext.getCurrentInstance().addMessage("ValidationErrors", new FacesMessage(FacesMessage.SEVERITY_WARN, viol.getPropertyPath().toString(), viol.getMessage()));
				}
				return true;
			}
		}
		return false;
	}
	
	public void save() {
		currentElement = elementService.save(currentElement);
		saved();
	}
	
	protected void saved() {
		mode=EditionMode.VIEW; // switch back to view mode after save
		showMessage("Saved", currentElement);
	}

	public void deleteAndCloseDialog() {
		delete();
		returnFromDialog("Deleted");
	}
	
	public void delete() {
		elementService.delete(currentElement);
		deleted();
	}
	
	protected void deleted() {
		showMessage("Deleted", currentElement);
	}
	
	public void setCurrentElement(T t) {
		resetCurrentElement(t);
	}
	
	protected void resetCurrentElement(Object o) {
		log.info("Setting editor for {}",o);
		this.currentElement = reloadableService.reload(o);
		this.lastWarningTime = null;
		initCurrentElement();
	}
	
	protected void initCurrentElement() {}
	
	protected void returnFromDialog(String event) {
		EditorReturnValue<T> value = event==null?null:new EditorReturnValue<T>(event, this.currentElement);
		PrimeFaces.current().dialog().closeDynamic(value);
	}
}
