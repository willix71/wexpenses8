package w.expenses8.web.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
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
import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.model.Tag;
import w.expenses8.data.domain.model.TransactionEntry;
import w.expenses8.data.domain.model.enums.TransactionFactor;
import w.expenses8.data.domain.service.IConsolidationService;
import w.expenses8.data.domain.service.ITagService;
import w.expenses8.data.domain.service.ITransactionEntryService;
import w.expenses8.data.utils.CollectionHelper;
import w.expenses8.data.utils.ConsolidationHelper;
import w.expenses8.web.controller.extra.EditionMode;
import w.expenses8.web.controller.extra.EditorReturnValue;
import w.expenses8.web.controller.extra.FacesHelper;
import w.expenses8.web.converter.DbableConverter;

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
	
	private Integer rangeLimit = 3;
	
	private List<TagCriteria> sourceTags;

	private List<TagCriteria> targetTags;
	
	private DualListModel<TransactionEntry> entries = new DualListModel<TransactionEntry>();

	private TransactionEntry selectedEntry;
	
	@Override
	protected void initCurrentElement() {
		if (this.currentElement!=null) {
			if (this.currentElement.getDate() != null) {
				targetTags = new ArrayList<TagCriteria>(Collections.singleton(tagService.getConsolidationTag(this.currentElement.getDate())));
				log.debug("Initial target tags {} ", targetTags);
			}
			if (this.currentElement.getInstitution() != null) {
				sourceTags = new ArrayList<TagCriteria>(tagService.findByInstitution(this.currentElement.getInstitution()));
				log.debug("Initial Source tags {} ", sourceTags);
			}
			if (!this.currentElement.isNew()) {
				setTargetEntries(transactionEntryService.findConsolidationEntries(this.currentElement));
			}

			documentFileSelector.reset(this.currentElement, this.currentElement.getDocumentFile());
			
			updateSourceEntries();

		} else {
			sourceTags = null;
			targetTags = null;
			setSourceEntries(new ArrayList<>());
			setTargetEntries(new ArrayList<>());
			documentFileSelector.reset(null, null);
		}
	}

	public void setNextElement(Consolidation conso) {
		setCurrentElement(Consolidation.with().date(conso.getDate().plusMonths(1)).institution(conso.getInstitution()).openingValue(conso.getClosingValue()).build());
	}
	
	public void onConsoDateChange(SelectEvent<LocalDate> eventS) {
		targetTags = new ArrayList<TagCriteria>(Collections.singleton(tagService.getConsolidationTag(this.currentElement.getDate())));
		updateSourceEntries();
	}
	
	public void onConsoInstitutionChange(AjaxBehaviorEvent event) {
		sourceTags = new ArrayList<TagCriteria>(tagService.findByInstitution(this.currentElement.getInstitution()));
		updateSourceEntries();
	}
	
	public void updateSourceEntries() {
		RangeLocalDateCriteria dateRange = rangeLimit==null || rangeLimit<1 || this.currentElement.getDate()==null?null:new RangeLocalDateCriteria(this.currentElement.getDate().minusMonths(rangeLimit),this.currentElement.getDate().plusMonths(3));
		List<TransactionEntry> sourceEntries = CollectionHelper.isEmpty(sourceTags)?new ArrayList<>():transactionEntryService.findConsolidatableEntries(this.currentElement,sourceTags, dateRange);
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
		log.debug("Transfered {}: {}", event.isAdd(), event.getItems());
		compute();
    }  
	
	public void onReorder() {
		log.debug("Reordered...");
		compute();
	}
     
    public void onSelect(SelectEvent<TransactionEntry> event) {
    	log.debug("Selected {}", event.getObject());
    	selectedEntry = event.getObject();
    }
     
    public void onUnselect(UnselectEvent<TransactionEntry> event) {
    	log.debug("Unselected {}", event.getObject());
    	selectedEntry = null;
    }
    
    public void compute() {
    	log.debug("computing consolidation...");
    	ConsolidationHelper.clearConsolidationEntries(getSourceEntries());
    	ConsolidationHelper.balanceConsolidation(this.currentElement, getTargetEntries());
    }

	@Override
	public void save() {
		log.info("saving consolidation...");
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
		return entries.getSource();
	}

	public void setSourceEntries(List<TransactionEntry> sourceEntries) {
		sourceEntries.removeAll(getTargetEntries());
		this.entries.setSource(sourceEntries);
	}

	public List<TransactionEntry> getTargetEntries() {
		return entries.getTarget();
	}

	public void setTargetEntries(List<TransactionEntry> targetEntries) {
		this.entries.setTarget(targetEntries);
	}
	
	public boolean isExpenseEditable() {
		return selectedEntry!=null;	
	}
	
	public void showExpense() {
		if (selectedEntry!=null) {
			Expense e = selectedEntry.getExpense();
			log.debug("Editing expense {}", e);
			FacesHelper.openEditor(e, EditionMode.EDIT, "expense", FacesHelper.getDefaultDialogOptions());
		}
	}
	
	public void onReturnFromExpenseEdition(SelectEvent<EditorReturnValue<Expense>> event) {
		Expense e = event==null||event.getObject()==null?null:event.getObject().getElement();
		if (e!=null) {
			updateEntries(e,getTargetEntries());
		}
		updateSourceEntries();
		compute();
	}

	private void updateEntries(Expense expense, List<TransactionEntry> entries) {
		for(ListIterator<TransactionEntry> iter=entries.listIterator();iter.hasNext();) {
			TransactionEntry t = iter.next();
			if (expense.equals(t.getExpense())) {
				if (expense.getTransactions().contains(t)) {
					iter.set(transactionEntryService.reload(t));
				} else {
					iter.remove();
				}
			}
		}
	}
	
	public Converter<TransactionEntry> getTransactionEntryConverter() {
		return converter;
	}

	private Converter<TransactionEntry> converter = new DbableConverter<TransactionEntry>() {
		@Override
		public TransactionEntry getAsObject(FacesContext fc, UIComponent uic, String uid) {
			TransactionEntry t = CollectionHelper.stream(getSourceEntries()).filter(e->uid.equals(e.getUid())).findFirst().orElseGet(
					()->CollectionHelper.stream(getTargetEntries()).filter(e->uid.equals(e.getUid())).findFirst().orElseGet(null) );
			return t;
		}
		
	};
}
