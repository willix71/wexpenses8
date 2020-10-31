package w.expenses8.web.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
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
import w.expenses8.data.domain.model.Tag;
import w.expenses8.data.domain.model.TransactionEntry;
import w.expenses8.data.domain.model.enums.TransactionFactor;
import w.expenses8.data.domain.service.IConsolidationService;
import w.expenses8.data.domain.service.ITagService;
import w.expenses8.data.domain.service.ITransactionEntryService;
import w.expenses8.data.utils.CollectionHelper;
import w.expenses8.data.utils.ConsolidationHelper;

@Slf4j
@Named
@ViewScoped
@Getter @Setter
public class ConsolidationEditionController extends AbstractEditionController<Consolidation> {

	private static final long serialVersionUID = 3351336696734127296L;

	static final String NEXT_CONSOLIDATION_FLASH_ID = "_NEXT_CONSOLIDATION_";

	@Inject
	DocumentFileSelector documentFileSelector;
	
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
			if (!this.currentElement.isNew()) {
				this.entries.setTarget(transactionEntryService.findConsolidationEntries(this.currentElement));
			}

			documentFileSelector.setCurrentDocumentFile(this.currentElement.getDocumentFile());
			
			updateSourceEntries();

		} else {
			sourceTags = null;
			targetTags = null;
			this.entries.setSource(new ArrayList<>());
			this.entries.setTarget(new ArrayList<>());
			documentFileSelector.reset();
		}
	}
	
	public void updateDate() {
		targetTags = new ArrayList<TagCriteria>(Collections.singleton(tagService.getConsolidationTag(this.currentElement.getDate())));
		updateSourceEntries();
	}

	public void updateInstitution(AjaxBehaviorEvent event) {
		sourceTags = new ArrayList<TagCriteria>(tagService.findByInstitution(this.currentElement.getInstitution()));
		updateSourceEntries();
	}
	
	public void updateSourceEntries() {
		RangeLocalDateCriteria dateRange = !limitRange || this.currentElement.getDate()==null?null:new RangeLocalDateCriteria(this.currentElement.getDate().minusMonths(3),this.currentElement.getDate().plusMonths(3));
		List<TransactionEntry> sourceEntries = CollectionHelper.isEmpty(sourceTags)?new ArrayList<>():transactionEntryService.findConsolidatableEntries(sourceTags, dateRange);
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

	public BigDecimal getTotalIn() {
		return ConsolidationHelper.sum(entries.getTarget(), TransactionFactor.IN);
	}
	
	public BigDecimal getTotalOut() {
		return ConsolidationHelper.sum(entries.getTarget(), TransactionFactor.OUT);
	}
	
	public BigDecimal getLastBalance() {
		return CollectionHelper.isEmpty(entries.getTarget())?null:CollectionHelper.last(entries.getTarget()).getAccountingBalance();
	}

	public void onTransfer(TransferEvent event) {
		log.info("Transfered {}: {}", event.isAdd(), event.getItems());
		compute();
    }  
	
	public void onReorder() {
		compute();
	}
     
    public void onSelect(SelectEvent<TransactionEntry> event) {
    	log.info("Selected {}", event.getObject());
    }
     
    public void onUnselect(UnselectEvent<TransactionEntry> event) {
    	log.info("Unselected {}", event.getObject());
    }
    
    public void compute() {
    	ConsolidationHelper.clearConsolidationEntries(entries.getSource());
    	ConsolidationHelper.balanceConsolidation(this.currentElement, entries.getTarget());
    }

	@Override
	public void save() {
		compute();
		currentElement.setDocumentFile(documentFileSelector.getCurrentDocumentFile());
		
		List<Tag> tags = targetTags.stream().filter(t->t instanceof Tag).map(t->(Tag) t).collect(Collectors.toList());
		Set<TransactionEntry> altered = new HashSet<>();
		entries.getTarget().stream().forEach(t->{
			t.setConsolidation(this.currentElement);
			t.getTags().addAll(tags);
			altered.add(t);
		});
		entries.getSource().stream().filter(t->t.getConsolidation()!=null).forEach(t->{
			t.setConsolidation(null);
			t.getTags().removeAll(tags);
			altered.add(t);
		});
		currentElement = consolidationService.save(currentElement, altered);

		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("consolidation.xhtml?id="+currentElement.getId());
		} catch(IOException ioe) {
			log.error("Failed to redirect consolidation...", ioe);
		}
	}
    
    
}
