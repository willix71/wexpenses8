package w.expenses8.data.domain.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.domain.criteria.TagCriteria;
import w.expenses8.data.domain.criteria.TransactionEntryCriteria;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.model.Tag;
import w.expenses8.data.domain.model.TransactionEntry;
import w.expenses8.data.domain.service.ITransactionEntryService;
import w.expenses8.data.domain.service.StoreService;

@Slf4j
@Service
public class ToolService {

	@Autowired
	private StoreService storeService;
	
	@Autowired
	private ITransactionEntryService tservice;

	@Transactional
	public void action() {
		Payee baloiseVie = storeService.load(Payee.class,193l);
		List<TagCriteria> tags = Arrays.asList(TagCriteria.NOT, storeService.load(Tag.class, 5l));
		Tag savings = storeService.load(Tag.class, 16375l);
		log.info("Adding {}", savings);
		for(TransactionEntry te: tservice.findTransactionEntries(TransactionEntryCriteria.with()
				.payee(baloiseVie)
				.tagCriterias(tags)
				.build())) {
			
			log.info(te.getTags().toString());
			te.addTag(savings);
		}
		
		
	}
}
