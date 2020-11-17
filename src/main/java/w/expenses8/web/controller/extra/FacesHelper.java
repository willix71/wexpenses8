package w.expenses8.web.controller.extra;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.PrimeFaces;

import w.expenses8.data.core.model.DBable;

public class FacesHelper {
	
	public static void setElementForRetrival(Object o) {
		((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getSession().setAttribute("_wex_", o);
	}
	
	public static Object retrieveElement() {
		Object o = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getSession().getAttribute("_wex_");
		if (o!=null) {
			((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getSession().setAttribute("_wex_", null);
		}
		return o;
	}
	
	public static Map<String,Object> getDefaultDialogOptions() {
	    Map<String,Object> options = new HashMap<String, Object>();
	    options.put("modal", true);
	    options.put("position", "top");
	    options.put("responsive", true);
	    options.put("headerElement", "customheader");
	    options.put("contentWidth", "100%");
	    options.put("width","1280");
	    options.put("minWidth","1280");
	    options.put("fitViewport","true");
	    return options;
	}
	
	public static Map<String,List<String>> getDefaultEditionParam(DBable<?> e, EditionMode mode) {
	    Map<String,List<String>> params = new HashMap<String, List<String>>();
	    params.put("hideMenu",  Collections.singletonList("true"));
	    params.put("mode", Collections.singletonList(mode.toString()));
	    if (e==null) {
	    	params.put("id", Collections.singletonList("new"));
	    } else if (e.isNew()) {
	    	params.put("id", Collections.singletonList("flash"));
	    	setElementForRetrival(e);
	    } else {
	    	params.put("id", Collections.singletonList(String.valueOf(e.getId())));
	    }
	    return params;
	}

	public static void openEditor(String page, DBable<?> e, EditionMode mode) {
		openEditor(page, getDefaultDialogOptions(), getDefaultEditionParam(e, mode));
	}

	public static void openEditor(String page, Map<String,Object> options, Map<String,List<String>> params) {
	    PrimeFaces.current().dialog().openDynamic(page, options, params);
	}
}
