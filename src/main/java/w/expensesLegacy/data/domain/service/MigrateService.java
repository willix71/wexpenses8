package w.expensesLegacy.data.domain.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import w.expenses8.data.config.CurrencyValue;
import w.expenses8.data.domain.model.Consolidation;
import w.expenses8.data.domain.model.DocumentFile;
import w.expenses8.data.domain.model.ExchangeRate;
import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.model.ExpenseType;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.model.PayeeType;
import w.expenses8.data.domain.model.Tag;
import w.expenses8.data.domain.model.TransactionEntry;
import w.expenses8.data.domain.model.enums.PayeeDisplayer;
import w.expenses8.data.domain.model.enums.TagType;
import w.expenses8.data.domain.service.IConsolidationService;
import w.expenses8.data.domain.service.IDocumentFileService;
import w.expenses8.data.utils.ConsolidationHelper;
import w.expenses8.data.utils.DateHelper;
import w.expensesLegacy.data.domain.model.Discriminator;

@Service
public class MigrateService {

	@PersistenceContext
	private EntityManager entityManager;

	@Inject
	private CurrencyValue currencyValue;
	
	@Inject
	private IDocumentFileService docService;

	@Inject
	private IConsolidationService consolidationService;
	
	@Inject 
	private DataSource dataSource;
	
	public HashMap<Long, DocumentFile> getCondsolidationDocuments() {
		HashMap<Long, DocumentFile> consolidationsDocument = new HashMap<Long, DocumentFile>();
		new JdbcTemplate(dataSource).query("SELECT c.id,c.date consoDate,p.name,e.FILEDATE filedate1,e.FILENAME filename1,e2.FILEDATE filedate2,e2.FILENAME filename2 FROM CONSOLIDATION c JOIN PAYEE p ON p.ID = c.INSTITUTION_ID  LEFT JOIN EXPENSE e ON c.DATE = e.FILEDATE AND c.INSTITUTION_ID = e.PAYEE_ID LEFT JOIN EXPENSE e2 ON c.CLOSINGBALANCE = -e2.amount AND c.INSTITUTION_ID = e2.PAYEE_ID ", new RowMapper<DocumentFile>() {
			@Override
			public DocumentFile mapRow(ResultSet rs, int rowNum) throws SQLException {
				DocumentFile file = null;
				if (rs.getString("filename1")!=null) {
					file = new DocumentFile(rs.getDate("filedate1").toLocalDate(), rs.getString("filename1"));
				} else if (rs.getString("filename2")!=null) {
					file = new DocumentFile(rs.getDate("filedate2").toLocalDate(), rs.getString("filename2"));					
				} else {
					java.sql.Date d = rs.getDate("consoDate");
					file = new DocumentFile(d.toLocalDate(), MessageFormat.format("{0,date,yyyyMMdd}_unknownConsolidation_{1}", d, rs.getString("name")));
				}
				if (file != null) {
					consolidationsDocument.put(rs.getLong(1),file);
				}
				return file;
			}
		});
		return consolidationsDocument;
	}
	
	@Transactional
	public void migrate() {
		HashMap<Long, DocumentFile> consolidationsDocument = getCondsolidationDocuments();
		
		int i = 0;
		for(w.expensesLegacy.data.domain.model.Expense legacyExpense: entityManager.createQuery("SELECT a from ExpenseOld a order by a.date DESC", w.expensesLegacy.data.domain.model.Expense.class).getResultList()) {
			//if (i>1000) break;
			System.err.println(++i + ") " + legacyExpense);
			
			Expense newx = w.expenses8.data.domain.model.Expense.with()
					.version(legacyExpense.getVersion()).uid(legacyExpense.getUid()).createdTs(legacyExpense.getCreatedTs()).modifiedTs(legacyExpense.getModifiedTs())
					.expenseType(getExpenseType(legacyExpense.getType()))
					.date(DateHelper.toLocalDateTime(legacyExpense.getDate()))
					.currencyAmount(legacyExpense.getAmount())
					.currencyCode(legacyExpense.getCurrency().getCode())
					.description(legacyExpense.getDescription())
					.externalReference(legacyExpense.getExternalReference())
					.payee(getPayee(legacyExpense.getPayee()))
					.build();
			
			w.expensesLegacy.data.domain.model.ExchangeRate rate = null;
			for(w.expensesLegacy.data.domain.model.TransactionLine line: legacyExpense.getTransactions()) {
				TransactionEntry entry = TransactionEntry.with().version(line.getVersion()).createdTs(line.getCreatedTs()).modifiedTs(line.getModifiedTs())
						.factor(line.getFactor())
						.currencyAmount(line.getAmount())
						// TODO
						//.accountingDate(line.getDate())
						//.description(line.getDescription())
						.tags(getTags(line.getAccount(),line.getDiscriminator(),line.getConsolidation()))
						.build();
				if (line.getExchangeRate() != null && "CHF".equals(line.getExchangeRate().getToCurrency().getCode())) {
					if (rate == null) {
						rate = line.getExchangeRate();
					} else if (!rate.equals(line.getExchangeRate())) {
						// two different rates for same expense
						throw new RuntimeException("two different rates for same expense " + legacyExpense.getUid());
					}
				}
				entry.setConsolidation(getConsolidation(line.getConsolidation(), consolidationsDocument));
				newx.addTransaction(entry);
			}
			newx.setExchangeRate(getExchangeRate(rate));
			newx.updateValues(currencyValue.getPrecision());
			if (legacyExpense.getFileDate()!=null && legacyExpense.getFileName()!=null) {
				newx.addDocumentFile(getDocumentFile(DateHelper.toLocalDate(legacyExpense.getFileDate()), legacyExpense.getFileName()));
			}
			
			if (legacyExpense.getPayment() != null) {
				newx.setPayedDate(DateHelper.toLocalDate(legacyExpense.getPayment().getDate()));
			}
			
			entityManager.persist(newx);
			//entityManager.flush();
		}
	}
	
	protected Set<Tag> getTags(w.expensesLegacy.data.domain.model.Account account, Discriminator discriminator, w.expensesLegacy.data.domain.model.Consolidation consolidation) {
		Set<Tag> tags = new HashSet<>();
		if (account != null) {
			switch(account.getType()) {
			case ASSET:
				tags.add(getTag(account.getName(), 1000, TagType.ASSET, getPayee(account.getOwner())));
				break;
			case LIABILITY:
				tags.add(getTag(account.getName(), 2000, TagType.LIABILITY, getPayee(account.getOwner())));
				break;
			case INCOME:
				tags.add(getTag(account.getName(), 3000, TagType.INCOME, getPayee(account.getOwner())));
				break;
			case EXPENSE:
				if (account.getParent()!=null) {
					if ("vacances".equals(account.getParent().getName())) {
						tags.add(getTag("vacances", 4000, TagType.EXPENSE));
						tags.add(getTag(account.getName(),Integer.parseInt(account.getNumber()), TagType.FLAG));
						break;
					} else if ("home".equals(account.getRoot().getName())) {
						if (discriminator == null) {
							tags.add(getTag("house", 5000, TagType.DISCRIMINATOR));
						}
					}
				}
				tags.add(getTag(account.getName(), 4000, TagType.EXPENSE, getPayee(account.getOwner())));
				break;
			default:
			}
		}
		if (discriminator != null) {
			tags.add(getTag(discriminator.getName(), 5000, TagType.DISCRIMINATOR));
		}
		if (consolidation != null) {
			tags.add(buildConsolidationTag(consolidation.getDate()));
		}
		return tags;
	}

	protected Tag getTag(String name, Integer number, TagType type) {
		return getTag(name, number, type, null);
	}
	
	protected Tag getTag(String name, Integer number, TagType type, Payee institution) {
		try {
			return entityManager.createQuery("SELECT a from Tag a where a.name = ?1", Tag.class).setParameter(1, name).getSingleResult();
		} catch(NoResultException noresult) {
			Tag newType = Tag.with()
					.name(name)
					.number(number)
					.type(type)
					.institution(institution)
					.build();
			entityManager.persist(newType);
			entityManager.flush();
			return newType;
		}
			
	}
	
	protected Tag buildConsolidationTag(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		String name = MessageFormat.format("{0,number,00}/{1,number,0000}", c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR));
		int number = c.get(Calendar.YEAR) * 100 + c.get(Calendar.MONTH)+1;
		return getTag(name, number, TagType.CONSOLIDATION);
	}
	
	protected ExpenseType getExpenseType(w.expensesLegacy.data.domain.model.ExpenseType legacy) {
		if (legacy == null) return null;
		try {
			return entityManager.createQuery("SELECT a from ExpenseType a where a.uid = ?1", ExpenseType.class).setParameter(1, legacy.getUid()).getSingleResult();
		} catch(NoResultException noresult) {
			ExpenseType newType = ExpenseType.with().uid(legacy.getUid()).version(legacy.getVersion()).createdTs(legacy.getCreatedTs()).modifiedTs(legacy.getModifiedTs())
					.name(legacy.getName()).selectable(legacy.isSelectable()).build();
			switch(legacy.getName()) {
			case "BVO":
				newType.setDisplayer(PayeeDisplayer.CCP);
				break;
			case "BVI":
				newType.setDisplayer(PayeeDisplayer.IBAN);
				break;
			}
			entityManager.persist(newType);
			entityManager.flush();
			return newType;
		}
	}
	protected ExchangeRate getExchangeRate(w.expensesLegacy.data.domain.model.ExchangeRate legacy) {
		if (legacy == null) return null;
		
		// a new exchangeRate for each expense
		ExchangeRate newType = ExchangeRate.with().version(legacy.getVersion()).createdTs(legacy.getCreatedTs()).modifiedTs(legacy.getModifiedTs())
				.institution(getPayee(legacy.getInstitution()))
				.fromCurrencyCode(legacy.getFromCurrency().getCode())
				.toCurrencyCode(legacy.getToCurrency().getCode())
				.date(DateHelper.toLocalDate(legacy.getDate()))
				.rate(legacy.getRate())
				.fee(legacy.getFee())
				.fixFee(legacy.getFixFee())
				.build();
		entityManager.persist(newType);
		entityManager.flush();
		return newType;
	}
	
	protected Payee getPayee(w.expensesLegacy.data.domain.model.Payee legacy) {
		if (legacy == null) return null;
		try {
			return entityManager.createQuery("SELECT a from Payee a where a.uid = ?1", Payee.class).setParameter(1, legacy.getUid()).getSingleResult();
		} catch(NoResultException noresult) {
			w.expensesLegacy.data.domain.model.City city = legacy.getCity();
			w.expensesLegacy.data.domain.model.Payee postalBank = legacy.getBankDetails();
			
			Payee newPayee = Payee.with().uid(legacy.getUid()).version(legacy.getVersion()).createdTs(legacy.getCreatedTs()).modifiedTs(legacy.getModifiedTs())
					.payeeType(getPayeeType(legacy.getType()))
					.name(legacy.getName())
					.prefix(legacy.getPrefix())
					.address1(legacy.getAddress1())
					.address2(legacy.getAddress2())
					.city(city==null?null:city.getName())
					.zip(city==null?null:city.getZip())
					.countryCode(city==null || city.getCountry()==null?null:city.getCountry().getCode())
					.postalAccount(legacy.getPostalAccount())
					.postalBank(postalBank==null?null:postalBank.getDisplay())
					.iban(legacy.getIban())
					.build();
			entityManager.persist(newPayee);
			entityManager.flush();
			return newPayee;
		}
	}
	
	protected PayeeType getPayeeType(w.expensesLegacy.data.domain.model.PayeeType legacy) {
		if (legacy == null) return null;
		try {
			return entityManager.createQuery("SELECT a from PayeeType a where a.uid = ?1", PayeeType.class).setParameter(1, legacy.getUid()).getSingleResult();
		} catch(NoResultException noresult) {
			PayeeType newType = PayeeType.with().uid(legacy.getUid()).version(legacy.getVersion()).createdTs(legacy.getCreatedTs()).modifiedTs(legacy.getModifiedTs())
					.name(legacy.getName()).selectable(legacy.isSelectable()).build();
			entityManager.persist(newType);
			entityManager.flush();
			return newType;
		}
	}
	
	protected DocumentFile getDocumentFile(LocalDate date, String filename) {
		DocumentFile docfile = docService.findByFileName(filename);
		if (docfile == null) {
			docfile = docService.save(new DocumentFile(date, filename));
		}
		return docfile;
	}
	
	protected Consolidation getConsolidation(w.expensesLegacy.data.domain.model.Consolidation legacy, HashMap<Long, DocumentFile> consolidationsDocument) {
		if (legacy == null) return null;
		try {
			return entityManager.createQuery("SELECT a from Consolidation a where a.uid = ?1", Consolidation.class).setParameter(1, legacy.getUid()).getSingleResult();
		} catch(NoResultException noresult) {
			
			Consolidation newType = Consolidation.with().uid(legacy.getUid()).version(legacy.getVersion()).createdTs(legacy.getCreatedTs()).modifiedTs(legacy.getModifiedTs())
					.date(DateHelper.toLocalDate(legacy.getDate()))
					.institution(getPayee(legacy.getInstitution()))
					.build();

			DocumentFile file = consolidationsDocument.get(legacy.getId());
			if (file != null) {
				newType.setDocumentFile(getDocumentFile(file.getDocumentDate(), file.getFileName()));
			}
			if (legacy.getOpeningBalance()!=null && legacy.getClosingBalance()!=null) {
				newType.setOpeningValue(legacy.getOpeningBalance());
				newType.setClosingValue(legacy.getClosingBalance());
				//Set<Tag> tags = getTags(legacy.getInstitution().getFirstAccount(), null, legacy);
				//newType.setOpeningEntry(getConsolidatinEntry(newType, legacy.getDate(), tags, TransactionFactor.IN,  legacy.getOpeningBalance()));
				//newType.setClosingEntry(getConsolidatinEntry(newType, legacy.getDate(), tags, TransactionFactor.OUT,  legacy.getClosingBalance()));
				newType.setDeltaValue(legacy.getDeltaBalance());
			}
			entityManager.persist(newType);
			entityManager.flush();
			return newType;
		}
		
	}
	
//	protected TransactionEntry getConsolidatinEntry(Consolidation conso, Date date, Set<Tag> tags, TransactionFactor factor, BigDecimal value) {
//		long accountingOrder = ((conso.getDate().getYear() * 100 ) + conso.getDate().getMonthValue()) * 10000;
//		if (factor == TransactionFactor.OUT) 
//			accountingOrder +=9999;
//		
//		LocalDate accountingDate = conso.getDate();
//		if (factor == TransactionFactor.OUT) 
//			accountingDate = accountingDate.plusMonths(1).minusDays(1);
//		
//		TransactionEntry entry = TransactionEntry.with()
//				.consolidation(conso)
//				.accountingDate(accountingDate)
//				.accountingYear(accountingDate.getYear())
//				.accountingOrder(accountingOrder)
//				.systemEntry(true)
//				.factor(factor)
//				.currencyAmount(value)
//				.accountingValue(value)
//				.accountingBalance(factor == TransactionFactor.OUT?BigDecimal.ZERO:value)
//				.tags(tags)
//				.build();
//		
//		entityManager.persist(entry);
//		return entry;		
//	}
	
	@Transactional
	public void consolidate() {
		for(Consolidation conso: consolidationService.loadAll()) {
			List<TransactionEntry> entries = entityManager.createQuery("SELECT t from TransactionEntry t where t.consolidation = ?1 order by t.accountingDate, t.accountingValue", TransactionEntry.class).setParameter(1, conso).getResultList();
			ConsolidationHelper.balanceConsolidation(conso, entries);
		}
	}
}
 