package w.expenses8.web.controller;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.config.CurrencyValue;

@Slf4j
@Named
@Getter
@ApplicationScoped
public class ApplicationController {

	@Inject
	private CurrencyValue defaultCurrency;

	
    public void action() {
        log.trace("action");
    }
    
    public void handleSelect(SelectEvent<?> event) {
        log.info("selected {}", event==null?"No event":event.getObject());
    }
    
    public void handleUnselect(UnselectEvent<?> event) {
    	log.trace("unselected {}", event==null?"No event":event.getObject());
    }
    
	public void valueChangeMethod(ValueChangeEvent event){
		log.trace("valueChange old {}", event==null?"No event":event.getOldValue());
	}
}
