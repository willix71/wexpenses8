package w.expenses8.web.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DualListModel;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.core.criteria.RangeLocalDateCriteria;
import w.expenses8.data.domain.criteria.TagCriteria;
import w.expenses8.data.domain.model.Consolidation;
import w.expenses8.data.domain.model.TransactionEntry;
import w.expenses8.data.domain.model.enums.TransactionFactor;
import w.expenses8.data.domain.service.IConsolidationService;
import w.expenses8.data.domain.service.ITagService;
import w.expenses8.data.domain.service.ITransactionEntryService;
import w.expenses8.data.utils.CollectionHelper;

@Slf4j
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
	
	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private ITransactionEntryService transactionEntryService;

	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private ITagService tagService;
	
	private boolean limitRange = true;
	
	private List<TagCriteria> sourceTags;

	private List<TagCriteria> targetTags;
	
	private DualListModel<TransactionEntry> entries = new DualListModel<TransactionEntry>();
	
	@Override
	public void setCurrentElementId(Object o) {
		this.currentElement = consolidationService.reload(o);
		initCurrentConsolidation();
	}
	
	@Override
	public void setCurrentElement(Consolidation conso) {
		this.currentElement = consolidationService.reload(conso);
		initCurrentConsolidation();
	}

	private void initCurrentConsolidation() {
		if (this.currentElement!=null) {
			if (this.currentElement.getDate() != null) {
				targetTags = new ArrayList<TagCriteria>(Collections.singleton(tagService.getConsolidationTag(this.currentElement.getDate())));
				log.info("Target tags {} ", targetTags);
			}
			if (this.currentElement.getInstitution() != null) {
				sourceTags = new ArrayList<TagCriteria>(tagService.findByInstitution(this.currentElement.getInstitution()));
				log.info("Target tags {} ", sourceTags);
			}
			if (this.currentElement.getTransactions()!=null) {
				this.entries.setTarget(new ArrayList<>(this.currentElement.getTransactions()));
			}
			updateSourceEntries();
		}		
	}
	
	public void updateDate() {
		targetTags = new ArrayList<TagCriteria>(Collections.singleton(tagService.getConsolidationTag(this.currentElement.getDate())));
		updateSourceEntries();
	}

	public void updateInstitution() {
		sourceTags = new ArrayList<TagCriteria>(tagService.findByInstitution(this.currentElement.getInstitution()));
		updateSourceEntries();
	}
	
	public void updateSourceEntries() {
		RangeLocalDateCriteria dateRange = !limitRange || this.currentElement.getDate()==null?null:new RangeLocalDateCriteria(this.currentElement.getDate().minusMonths(3),this.currentElement.getDate().plusMonths(3));
		List<TransactionEntry> sourceEntries = transactionEntryService.findConsolidatableEntrys(sourceTags, dateRange);
		entries.setSource(sourceEntries);
	}
	
	@Override
	protected Object getInitialElementId() {
		Object id = super.getInitialElementId();
		if (id == null) {
			try {
				id = FacesContext.getCurrentInstance().getExternalContext().getFlash().get(NEXT_CONSOLIDATION_FLASH_ID);
				FacesContext.getCurrentInstance().getExternalContext().getFlash().put(NEXT_CONSOLIDATION_FLASH_ID,null);
			} catch(NullPointerException e) { /* sometimes happens just after the session is created */ }
		}
		return id;
	}

    public void onTransfer(TransferEvent event) {
    	compute();
    }  
     
    public void onSelect(SelectEvent<TransactionEntry> event) {
    	log.info("Selected {}", event.getObject());
    	compute();
    }
     
    public void onUnselect(UnselectEvent<TransactionEntry> event) {
    	log.info("Unselected {}", event.getObject());
    	compute();
    }
     
    public void onReorder() {
    	compute();
    }
    
    private void compute() {
		if (!CollectionHelper.isEmpty(entries.getTarget())) {
			//Tag tag = entityManager.
			Consolidation conso = this.getCurrentElement();
			long order = ((conso.getDate().getYear()*100) + conso.getDate().getMonthValue()) * 10000;
			BigDecimal balance = conso.getOpeningValue();
			for(TransactionEntry entry: entries.getTarget()) {
				entry.setConsolidation(conso);
				entry.setAccountingOrder(++order);
				if (entry.getFactor()==TransactionFactor.IN) {
					balance = balance.add(entry.getAccountingValue());
				} else if (entry.getFactor()==TransactionFactor.OUT) {
					balance = balance.subtract(entry.getAccountingValue());
				}
				entry.setAccountingBalance(balance);
			}
			conso.setDeltaValue(conso.getClosingValue().subtract(balance));
		}
    }
}
