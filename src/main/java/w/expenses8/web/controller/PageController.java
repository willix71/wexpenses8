package w.expenses8.web.controller;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
@Getter @Setter
public class PageController {

	private final boolean hideMenu;

	public PageController() {
		String pHideMenu=((HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequest())).getParameter("hideMenu");
		this.hideMenu = pHideMenu!=null && Boolean.valueOf(pHideMenu); 
	}
}
