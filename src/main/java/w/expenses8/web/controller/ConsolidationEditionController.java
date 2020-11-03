package w.expenses8.web.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
	
//    private List<TransactionEntry> sourceEntries;
//	
//    private List<TransactionEntry> targetEntries;
	
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

	public void setNextElement(Consolidation conso) {
		conso = consolidationService.reload(conso);
		this.currentElement = Consolidation.with().date(conso.getDate().plusMonths(1)).institution(conso.getInstitution()).openingValue(conso.getClosingValue()).build();
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
				setTargetEntries(transactionEntryService.findConsolidationEntries(this.currentElement));
			}

			documentFileSelector.setCurrentDocumentFile(this.currentElement.getDocumentFile());
			
			updateSourceEntries();

		} else {
			sourceTags = null;
			targetTags = null;
			setSourceEntries(new ArrayList<>());
			setTargetEntries(new ArrayList<>());
			documentFileSelector.reset();
		}
	}
	
	public void onConsoDateChange() {
		targetTags = new ArrayList<TagCriteria>(Collections.singleton(tagService.getConsolidationTag(this.currentElement.getDate())));
		updateSourceEntries();
	}

	public void onConsoInstitutionChange(AjaxBehaviorEvent event) {
		sourceTags = new ArrayList<TagCriteria>(tagService.findByInstitution(this.currentElement.getInstitution()));
		updateSourceEntries();
	}
	
	public void updateSourceEntries() {
		RangeLocalDateCriteria dateRange = !limitRange || this.currentElement.getDate()==null?null:new RangeLocalDateCriteria(this.currentElement.getDate().minusMonths(3),this.currentElement.getDate().plusMonths(3));
		List<TransactionEntry> sourceEntries = CollectionHelper.isEmpty(sourceTags)?new ArrayList<>():transactionEntryService.findConsolidatableEntries(sourceTags, dateRange);
		setSourceEntries(sourceEntries);
	}

	public BigDecimal getTotalIn() {
		return ConsolidationHelper.sum(getTargetEntries(), TransactionFactor.IN);
	}
	
	public BigDecimal getTotalOut() {
		return ConsolidationHelper.sum(getTargetEntries(), TransactionFactor.OUT);
	}
	
	public BigDecimal getLastBalance() {
		return CollectionHelper.isEmpty(getTargetEntries())?null:CollectionHelper.last(getTargetEntries()).getAccountingBalance();
	}

	public void onTransfer(TransferEvent event) {
		log.info("Transfered {}: {}", event.isAdd(), event.getItems());
		compute();
    }  
	
	public void onReorder() {
		log.info("Reordered...");
		compute();
	}
     
    public void onSelect(SelectEvent<TransactionEntry> event) {
    	log.info("Selected {}", event.getObject());
    }
     
    public void onUnselect(UnselectEvent<TransactionEntry> event) {
    	log.info("Unselected {}", event.getObject());
    }
    
    public void compute() {
    	ConsolidationHelper.clearConsolidationEntries(getSourceEntries());
    	ConsolidationHelper.balanceConsolidation(this.currentElement, getTargetEntries());
    }

	@Override
	public void save() {
		compute();
		currentElement.setDocumentFile(documentFileSelector.getCurrentDocumentFile());
		
		List<Tag> tags = targetTags.stream().filter(t->t instanceof Tag).map(t->(Tag) t).collect(Collectors.toList());
		Set<TransactionEntry> altered = new HashSet<>();
		getTargetEntries().stream().forEach(t->{
			t.setConsolidation(this.currentElement);
			t.getTags().addAll(tags);
			altered.add(t);
		});
		getSourceEntries().stream().filter(t->t.getConsolidation()!=null).forEach(t->{
			t.setConsolidation(null);
			t.getTags().removeAll(tags);
			altered.add(t);
		});
		
		currentElement = consolidationService.save(currentElement, altered);
		saved();
	}

	public List<TransactionEntry> getSourceEntries() {
		//return sourceEntries;
		return entries.getSource();
	}

	public void setSourceEntries(List<TransactionEntry> sourceEntries) {
		//this.sourceEntries = sourceEntries;
		this.entries.setSource(sourceEntries);
	}

	public List<TransactionEntry> getTargetEntries() {
		//return targetEntries;
		return entries.getTarget();
	}

	public void setTargetEntries(List<TransactionEntry> targetEntries) {
		//this.targetEntries = targetEntries;
		this.entries.setTarget(targetEntries);
	}
}
