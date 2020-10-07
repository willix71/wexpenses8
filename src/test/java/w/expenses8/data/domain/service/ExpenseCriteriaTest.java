package w.expenses8.data.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import static java.util.Arrays.asList;
import java.util.HashSet;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.config.DataConfig;
import w.expenses8.data.domain.criteria.ExpenseCriteria;
import w.expenses8.data.domain.criteria.TransactionEntryCriteria;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.model.Tag;
import w.expenses8.data.domain.model.TagGroup;
import w.expenses8.data.domain.model.TransactionEntry;
import w.expenses8.data.domain.model.enums.TagType;
import w.expenses8.data.domain.model.enums.TransactionFactor;

import static w.expenses8.data.domain.model.enums.TagType.*;
import w.expenses8.data.utils.ExpenseHelper;

@Slf4j
@SpringBootTest
@Import(DataConfig.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create-drop","spring.datasource.url=jdbc:h2:mem:ExpenseCriteriaTest;DB_CLOSE_DELAY=-1"})
public class ExpenseCriteriaTest {

	static Payee someone;
	static Tag cash;
	static Tag matercard;
	static Tag tax;
	static Tag doctor;
	static Tag pills;
	static Tag me;
	static Tag wife;
	static Tag salary;
	static TagGroup healths;
	
	@Autowired
	private StoreService storeService;
	
	@Inject
	private IExpenseService expenseService;
	
	@Inject
	private ITransactionEntryService accountService;
	

	@BeforeEach
	public void separatorBefore(TestInfo info) {
		if (expenseService.count()==0) {
			someone = storeService.save(Payee.with().name("Someone").build());
			cash = Tag.with().type(ASSET).name("cash").build();
			tax = Tag.with().type(EXPENSE).name("tax").build();
			expenseService.save(ExpenseHelper.build(LocalDate.of(1971,6,1), new BigDecimal("71.00"),"CHF",someone,cash,tax));

			matercard = Tag.with().type(LIABILITY).name("mastercart").build();
			doctor = Tag.with().type(EXPENSE).name("doctor").build();
			me = Tag.with().type(EXPENSE).name("me").build();
			expenseService.save(ExpenseHelper.build(LocalDate.of(2000,1,1), new BigDecimal("200.00"),"CHF",someone,matercard,
					TransactionEntry.with().factor(TransactionFactor.IN).tags(new HashSet<>(asList(doctor,me))).build()
					));
			
			pills = Tag.with().type(EXPENSE).name("doctor").build();
			wife = Tag.with().type(EXPENSE).name("wife").build();
			expenseService.save(ExpenseHelper.build(LocalDate.of(2000,2,1), new BigDecimal("300.00"),"CHF",someone,matercard,
					TransactionEntry.with().factor(TransactionFactor.IN).accountingValue(new BigDecimal("120.00")).tags(new HashSet<>(asList(pills,me))).build(),
					TransactionEntry.with().factor(TransactionFactor.IN).accountingValue(new BigDecimal("180.00")).tags(new HashSet<>(asList(pills,wife))).build()
					));
	
			healths = TagGroup.with().name("healths").tags(new HashSet<>(asList(doctor, pills))).build();
			
			salary = Tag.with().type(INCOME).name("salary").build();
			expenseService.save(ExpenseHelper.build(LocalDate.of(2020,2,20), new BigDecimal("1000.00"),"CHF",someone,salary,cash));
		}
		
		log.info("\n\n---- Running {} -----", info.getDisplayName());		
	}
	
	@AfterEach
	public void separatorAfter(TestInfo info) {
		log.info("\n---- Run {} -----\n", info.getDisplayName());		
	}
	
	@Test
	@Order(1)
	public void test_setup() {
		assertThat(expenseService.count()).isEqualTo(4);
		assertThat(accountService.count()).isEqualTo(9);
	}
	
	@Test
	@Order(2)
	public void test_salary() {
		assertThat(expenseService.findExpenses(ExpenseCriteria.with().tagCriterias(asList(salary)).build())).hasSize(1).first().hasFieldOrPropertyWithValue("currencyAmount", new BigDecimal("1000.00"));
		assertThat(accountService.findTransactionEntrys(TransactionEntryCriteria.with().tagCriterias(asList(salary)).build())).hasSize(1).first().hasFieldOrPropertyWithValue("currencyAmount", new BigDecimal("1000.00"));
	}
	
	@Test
	@Order(2)
	public void test_cash() {
		assertThat(expenseService.findExpenses(ExpenseCriteria.with().tagCriterias(asList(cash)).build())).hasSize(2).first().hasFieldOrPropertyWithValue("currencyAmount", new BigDecimal("1000.00"));
		assertThat(accountService.findTransactionEntrys(TransactionEntryCriteria.with().tagCriterias(asList(cash)).build())).hasSize(2).first().hasFieldOrPropertyWithValue("currencyAmount", new BigDecimal("71.00"));
	}
	
	@Test
	@Order(3)
	public void test_me() {
		assertThat(expenseService.findExpenses(ExpenseCriteria.with().tagCriterias(asList(me)).build())).hasSize(2);
		assertThat(accountService.findTransactionEntrys(TransactionEntryCriteria.with().tagCriterias(asList(me)).build())).hasSize(2);
	}
	
	@Test
	@Order(4)
	public void test_pills() {
		assertThat(expenseService.findExpenses(ExpenseCriteria.with().tagCriterias(asList(pills)).build())).hasSize(1);
		assertThat(accountService.findTransactionEntrys(TransactionEntryCriteria.with().tagCriterias(asList(pills)).build())).hasSize(2);
	}
	
	@Test
	@Order(5)
	public void test_healths() {
		assertThat(expenseService.findExpenses(ExpenseCriteria.with().tagCriterias(asList(healths)).build())).hasSize(2);
		assertThat(accountService.findTransactionEntrys(TransactionEntryCriteria.with().tagCriterias(asList(healths)).build())).hasSize(3);
	}
	
	@Test
	@Order(6)
	public void test_healths_and_me() {
		assertThat(expenseService.findExpenses(ExpenseCriteria.with().tagCriterias(asList(healths,me)).build())).hasSize(2);
		assertThat(accountService.findTransactionEntrys(TransactionEntryCriteria.with().tagCriterias(asList(healths,me)).build())).hasSize(2);
	}
	
	@Test
	@Order(6)
	public void test_expenses() {
		assertThat(expenseService.findExpenses(ExpenseCriteria.with().tagCriterias(asList(TagType.EXPENSE)).build())).hasSize(3);
		assertThat(accountService.findTransactionEntrys(TransactionEntryCriteria.with().tagCriterias(asList(TagType.EXPENSE)).build())).hasSize(4);
	}
}
