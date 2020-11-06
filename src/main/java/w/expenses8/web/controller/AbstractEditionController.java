package w.expenses8.web.controller;

import java.time.Instant;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.primefaces.PrimeFaces;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import w.expenses8.WexpensesConstants;
import w.expenses8.data.core.model.DBable;
import w.expenses8.data.core.service.IGenericService;
import w.expenses8.data.core.service.IReloadableService;
import w.expenses8.data.domain.validation.Warning;
import w.expenses8.data.utils.ValidationHelper;

@Slf4j
@Getter @Setter @NoArgsConstructor
public abstract class AbstractEditionController<T extends DBable<T>> extends AbstractController<T> {

	private static final long serialVersionUID = 3351336696734127296L;

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

	private boolean edition = true;
	
	private Instant lastWarningTime;
	
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
				return WexpensesConstants.NEW_INSTANCE;
			} else {
				return Long.parseLong(id);
			}
		} else {
			String uid=((HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequest())).getParameter("uid");
			if (uid!=null) {
				return uid;
			}
		}
		return null;
	}
	
	public void setEdition(boolean edit) {
		if (this.edition && !edit) {
			// reset the 'error' fields			
			PrimeFaces.current().resetInputs("wexEditionForm");
			// reload the element
			setCurrentElement(currentElement); // force reload
		}
		
		this.edition = edit;
	}
	
	public void newElement() throws Exception {
		resetCurrentElement(null);
	}
	
	public void validateAndSave() {
		if (isValid()) {
			save();
		}
	}

	public boolean isValid() {
		if (currentElement != null) {
			try {
				ValidationHelper.validate(validator, currentElement);
			} catch(ConstraintViolationException ex) {
				for(ConstraintViolation<?> viol: ex.getConstraintViolations()) {
					log.warn("ConstraintViolation errors for {} : {}", viol.getPropertyPath(), viol.getMessage());
					FacesContext.getCurrentInstance().addMessage("ValidationErrors", new FacesMessage(FacesMessage.SEVERITY_ERROR, viol.getPropertyPath().toString(), viol.getMessage()));
				}
				return false;
			}
			
			Instant now = Instant.now();
			if (lastWarningTime==null || now.isAfter(lastWarningTime.plusSeconds(5))) {
				lastWarningTime = now;
				try {
					ValidationHelper.validate(validator, currentElement, Warning.class);
				} catch(ConstraintViolationException ex) {
					for(ConstraintViolation<?> viol: ex.getConstraintViolations()) {
						log.warn("ConstraintViolation warnings for {} : {}", viol.getPropertyPath(), viol.getMessage());
						FacesContext.getCurrentInstance().addMessage("ValidationErrors", new FacesMessage(FacesMessage.SEVERITY_WARN, viol.getPropertyPath().toString(), viol.getMessage()));
					}
					return false;
				}
			}
		}
		return true;
	}
	
	public void save() {
		currentElement = elementService.save(currentElement);
		saved();
	}
	
	protected void saved() {
		edition=false; // switch back to view mode after save
		PrimeFaces.current().ajax().addCallbackParam("isSaved",true);
		showMessage("Saved", currentElement);
	}

	public void delete() {
		elementService.delete(currentElement);
		deleted();
	}
	
	protected void deleted() {
		showMessage("Deleted", currentElement);
		currentElement = null;
	}
	
	public void setCurrentElement(T t) {
		resetCurrentElement(t);
	}
	
	protected void resetCurrentElement(Object o) {
		this.currentElement = reloadableService.reload(o);
		this.lastWarningTime = null;
		initCurrentElement();
	}
	
	protected void initCurrentElement() {}
}
