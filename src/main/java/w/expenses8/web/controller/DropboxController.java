package w.expenses8.web.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.domain.criteria.TagCriteria;
import w.expenses8.data.domain.model.DocumentFile;
import w.expenses8.data.domain.model.ExpenseType;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.model.PayeeType;
import w.expenses8.data.domain.model.Tag;
import w.expenses8.data.domain.model.enums.PayeeDisplayer;
import w.expenses8.data.domain.model.enums.TagType;
import w.expenses8.data.domain.model.enums.TransactionFactor;
import w.expenses8.data.domain.service.IDocumentFileService;
import w.expenses8.data.domain.service.IExpenseTypeService;
import w.expenses8.data.domain.service.IPayeeService;
import w.expenses8.data.domain.service.IPayeeTypeService;
import w.expenses8.data.domain.service.ITagGroupService;
import w.expenses8.data.domain.service.ITagService;
import w.expenses8.data.domain.service.ICountryService;

@Slf4j
@Named
@ApplicationScoped
public class DropboxController implements Serializable {

	private static final long serialVersionUID = 3351336696734127296L;

	@Inject
	private IPayeeTypeService payeeTypeService;
	
	public List<PayeeType> completePayeeType(String prefix) {
		return payeeTypeService.findBySelectable(prefix);
	}
	
	@Inject
	private IExpenseTypeService expenseTypeService;
	
	public List<ExpenseType> completeExpenseType(String prefix) {
		return expenseTypeService.findBySelectable(prefix);
	}
	
	public List<ExpenseType> getExpenseTypes() {
		return expenseTypeService.loadAll();
	}
	
	@Inject
	private IPayeeService payeeService;
	
	public List<Payee> completePayee(String text) {
		return payeeService.findByText(text, PayeeDisplayer.DEFAULT);
	}
	public List<Payee> completePayeeAndCcp(String text) {
		return payeeService.findByText(text, PayeeDisplayer.CCP);
	}
	public List<Payee> completePayeeAndIban(String text) {
		return payeeService.findByText(text, PayeeDisplayer.IBAN);
	}
	
	List<PayeeDisplayer> payeeDisplayers = Arrays.asList(PayeeDisplayer.values());
	
	public List<PayeeDisplayer> getPayeeDisplayers() {
		return payeeDisplayers;
	}
	
	@Inject
	private ITagService tagService;
	
	@Inject
	private ITagGroupService tagGroupService;
	
	public List<Tag> completeTag(String text) {
		return tagService.findByText(text);
	}

	private static List<TagCriteria> operators = Arrays.asList(TagCriteria.NOT,TagCriteria.IN,TagCriteria.OUT,TagCriteria.AND,TagCriteria.OR);
	
	@SuppressWarnings({"unchecked","rawtypes"})
	public List<? extends TagCriteria> completeTagGroup(String text) {
		String upperText=text.toUpperCase();
		List tagCriterias = new ArrayList();
		operators.stream().filter(tt->tt.getName().startsWith(upperText)).forEach(tagCriterias::add);
		tagCriterias.addAll(tagService.findByText(text));
		tagCriterias.addAll(tagGroupService.findByText(text));
		Arrays.stream(TagType.values()).filter(tt->tt.getName().contains(upperText)).forEach(tagCriterias::add);
		return tagCriterias;
	}
	
	List<TagType> tagTypes = Arrays.asList(TagType.values());
	
	public List<TagType> getTagTypes() {
		return tagTypes;
	}
	
	List<TransactionFactor> transactionFactors = Arrays.asList(TransactionFactor.IN, TransactionFactor.OUT);
	
	public List<TransactionFactor> getTransactionFactors() {
		return transactionFactors;
	}
	
	@Inject
	private ICountryService countryService;
	
	public List<String> getCountries() {
		return countryService.getCountriesCodes();
	}
	
	public List<String> getCurrencies() {
		return countryService.getCurrenciesCodes();
	}
	
	@Inject
	private IDocumentFileService documentFileService;
	
	public List<DocumentFile> completeDocumentFile(String fileName) {
		return documentFileService.findByText(fileName);
	}
	
	public boolean filterByValue(Object value, Object filter, Locale locale) {
		if (filter == null)
			return true;

		String filterText = filter.toString().trim();
		if (filterText.isEmpty())
			return true;

		if (value == null)
			return false;

		try {
			if (filterText.startsWith(">=") || filterText.startsWith("=>")) {
				return ((BigDecimal) value).compareTo(new BigDecimal(filterText.substring(2)))>=0;
			} else if (filterText.startsWith("<=") || filterText.startsWith("=<")) {
				return ((BigDecimal) value).compareTo(new BigDecimal(filterText.substring(2)))<=0;
			} else if (filterText.startsWith(">")) {
				return ((BigDecimal) value).compareTo(new BigDecimal(filterText.substring(1)))>0;
			} else if (filterText.startsWith("<")) {
				return ((BigDecimal) value).compareTo(new BigDecimal(filterText.substring(1)))<0;
			} else if (filterText.startsWith("=")) {
				return ((BigDecimal) value).compareTo(new BigDecimal(filterText.substring(1)))==0;
			} else {
				return ((BigDecimal) value).compareTo(new BigDecimal(filterText))==0;
			}
		} catch(NumberFormatException nfe) {
			log.debug("Failed to conver number [{}]", value);
			return false;
		}
	}
	
	public boolean filterByDate(Object value, Object filter, Locale locale) {
		if (filter == null)
			return true;
		
		String filterText = filter.toString().trim();
		if (filterText.isEmpty())
			return true;
		
		String ds = dateToString(value);
		
		if (ds == null)
			return false;
		
		try {
			if (filterText.startsWith(">=") || filterText.startsWith("=>")) {
				return ds.compareTo(filterText.substring(2))>=0;
			} else if (filterText.startsWith("<=") || filterText.startsWith("=<")) {
				return ds.compareTo(filterText.substring(2))<=0;
			} else if (filterText.startsWith(">")) {
				return ds.compareTo(filterText.substring(2))>0;
			} else if (filterText.startsWith("<")) {
				return ds.compareTo(filterText.substring(2))<0;
			} else if (filterText.startsWith("=")) {
				return ds.compareTo(filterText.substring(2))==0;
			} else {
				return ds.startsWith(filterText);
			}
		} catch(NumberFormatException nfe) {
			log.debug("Failed to conver number [{}]", value);
			return false;
		}
	}
	
	private String dateToString(Object d) {
		if (d==null) {
			return null;
		} else if (d instanceof LocalDate) {
			return ((LocalDate) d).format(DateTimeFormatter.BASIC_ISO_DATE);
		} else if (d instanceof LocalDateTime) {
			return ((LocalDateTime) d).format(DateTimeFormatter.BASIC_ISO_DATE);
		} else if (d instanceof Date) {
			return new SimpleDateFormat("yyyyMMdd").format((Date) d);
		} else {
			return d.toString();
		}
	}
 }
