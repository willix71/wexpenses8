package w.expensesLegacy.data.domain.service;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import w.expenses8.data.domain.model.DocumentFile;
import w.expenses8.data.domain.model.ExchangeRate;
import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.model.ExpenseType;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.model.PayeeType;
import w.expenses8.data.domain.model.Tag;
import w.expenses8.data.domain.model.TransactionEntry;
import w.expenses8.data.domain.model.enums.TagType;
import w.expenses8.data.utils.DateHelper;
import w.expensesLegacy.data.domain.model.Account;
import w.expensesLegacy.data.domain.model.Consolidation;
import w.expensesLegacy.data.domain.model.Discriminator;

@Service
public class MigrateService {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Transactional
	public void migrate() {
		int i = 0;
		for(w.expensesLegacy.data.domain.model.Expense legacyExpense: entityManager.createQuery("SELECT a from ExpenseOld a order by a.date DESC", w.expensesLegacy.data.domain.model.Expense.class).getResultList()) {
			System.out.println(++i + ") " + legacyExpense);
			
			Expense newx = w.expenses8.data.domain.model.Expense.with()
					.version(legacyExpense.getVersion()).createdTs(legacyExpense.getCreatedTs()).modifiedTs(legacyExpense.getModifiedTs())
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
						//.accountingDate(line.getDate())
						//.description(line.getDescription())
						.tags(getTags(legacyExpense.getDate(),line.getAccount(),line.getDiscriminator(),line.getConsolidation()))
						.build();
				if (line.getExchangeRate() != null && "CHF".equals(line.getExchangeRate().getToCurrency().getCode())) {
					if (rate == null) {
						rate = line.getExchangeRate();
					} else if (!rate.equals(line.getExchangeRate())) {
						// two different rates for same expense
						throw new RuntimeException("two different rates for same expense " + legacyExpense.getUid());
					}
				}
				newx.addTransaction(entry);
			}
			newx.setExchangeRate(getExchangeRate(rate));
			newx.updateValues();
			if (legacyExpense.getFileDate()!=null && legacyExpense.getFileName()!=null) {
				newx.addDocumentFile(new DocumentFile(newx, DateHelper.toLocalDate(legacyExpense.getFileDate()), legacyExpense.getFileName()));
			}
			entityManager.persist(newx);
			entityManager.flush();
			//if (i>100) break;
			//System.exit(0);
		}
	}
	
	protected Set<Tag> getTags(Date d, Account account, Discriminator discriminator, Consolidation consolidation) {
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
				tags.add(getTag(account.getName(), 4000, TagType.EXPENSE));
				break;
			default:
			}
		}
		if (discriminator != null) {
			tags.add(getTag(account.getName(), 5000, TagType.DISCRIMINATOR));
		}
		if (consolidation != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(consolidation.getDate());
			String name = MessageFormat.format("{0,number,00}/{1,number,0000}", c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR));
			int number = c.get(Calendar.YEAR) * 100 + c.get(Calendar.MONTH)+1;
			tags.add(getTag(name, number, TagType.CONSOLIDATION));
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
	
	protected ExpenseType getExpenseType(w.expensesLegacy.data.domain.model.ExpenseType legacy) {
		if (legacy == null) return null;
		try {
			return entityManager.createQuery("SELECT a from ExpenseType a where a.uid = ?1", ExpenseType.class).setParameter(1, legacy.getUid()).getSingleResult();
		} catch(NoResultException noresult) {
			ExpenseType newType = ExpenseType.with().uid(legacy.getUid()).version(legacy.getVersion()).createdTs(legacy.getCreatedTs()).modifiedTs(legacy.getModifiedTs())
					.name(legacy.getName()).selectable(legacy.isSelectable()).build();
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
}
