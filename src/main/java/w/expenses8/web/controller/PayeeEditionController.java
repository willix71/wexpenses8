package w.expenses8.web.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.PrimeFaces;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.service.IPayeeService;

@Named
@ViewScoped
@Getter @Setter
public class PayeeEditionController implements Serializable {

	private static final long serialVersionUID = 3351336696734127296L;

	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private IPayeeService payeeService;

	private Payee currentPayee;

	@PostConstruct
	public void initSelectedElement() {
		String id=((HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequest())).getParameter("id");
		if (id!=null) {
			setCurrentPayee(payeeService.load(Long.parseLong(id)));
		} else {
			String uid=((HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequest())).getParameter("uid");
			if (uid!=null) {
				setCurrentPayee(payeeService.loadByUid(uid));
			}
		}
	}
	
	public void save() {
		currentPayee = payeeService.save(currentPayee);
		PrimeFaces.current().ajax().addCallbackParam("isSaved",true);
	}
	
	public void newPayee() {
		setCurrentPayee(new Payee());
	}
	
	public void delete() {
		payeeService.delete(currentPayee);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Payee deleted"));
		currentPayee = null;
	}
	
	public void setCurrentPayee(Payee payee) {
		this.currentPayee = payee.isNew()?payee:payeeService.reload(payee);
	}
}
