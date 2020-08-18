package w.expenses8.data.service.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Date;

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
import w.expenses8.data.model.domain.Expense;
import w.expenses8.data.model.domain.Payee;
import w.expenses8.data.model.domain.Tag;
import w.expenses8.data.model.domain.TransactionEntry;
import w.expenses8.data.model.enums.TagEnum;
import w.expenses8.data.model.enums.TransactionFactor;

@Slf4j
@SpringBootTest
@Import(DataConfig.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create-drop","spring.datasource.url=jdbc:h2:mem:ExpenseServiceTest;DB_CLOSE_DELAY=-1"})
public class ExpenseListServiceTest {

	private static final String EXPENSE_UID_1 = "11111-22222-33333";
	
	@Autowired
	IExpenseService expenseService;
	
	@Autowired
	IPayeeService payeeService;
	
	@BeforeEach
	public void separatorBefore(TestInfo info) {
		log.info("\n\n---- Running {} -----", info.getDisplayName());		
	}
	
	@AfterEach
	public void separatorAfter(TestInfo info) {
		log.info("\n---- Run {} -----\n", info.getDisplayName());		
	}
	
	@Test
	@Order(1)
	public void test_insert1() {
		Payee someone = Payee.with().name("Someone").build();
		
		Expense x = Expense.with().date(new Date()).currencyAmount(new BigDecimal(20)).currencyCode("CHF").accountingValue(new BigDecimal(20)).payee(someone).build();
		x.addTransaction(TransactionEntry.with().factor(TransactionFactor.OUT).currencyAmount(new BigDecimal(20)).accountingValue(new BigDecimal(20)).build().addTag(Tag.with().name("tag").type(TagEnum.EXPENSE).build()));
		x.addTransaction(TransactionEntry.with().factor(TransactionFactor.IN).currencyAmount(new BigDecimal(20)).accountingValue(new BigDecimal(20)).build());
		Expense x1 = expenseService.save(x);
		log.info("\n\t==== Saved Expense {} ====", x1);
		assertThat(x1.getId()).isNotNull();
	}
	
	@Test
	@Order(2)
	public void test_insert2() {
		Payee someone = payeeService.save(Payee.with().name("SomeoneElse").build());
		
		Expense x = Expense.with().date(new Date()).currencyAmount(new BigDecimal(20)).currencyCode("CHF").accountingValue(new BigDecimal(50)).payee(someone).build();
		x.addTransaction(TransactionEntry.with().factor(TransactionFactor.OUT).currencyAmount(new BigDecimal(50)).accountingValue(new BigDecimal(50)).build());
		x.addTransaction(TransactionEntry.with().factor(TransactionFactor.IN).currencyAmount(new BigDecimal(50)).accountingValue(new BigDecimal(50)).build());
		Expense x1 = expenseService.save(x);
		log.info("\n\t===== Saved Expense {} ====", x1);
		assertThat(x1.getId()).isNotNull();
	}
	
	@Test
	@Order(3)
	public void test_update3() {
		Payee someone = payeeService.save(Payee.with().name("SomeoneElse").build());
		
		Expense x = Expense.with().uid(EXPENSE_UID_1).date(new Date()).currencyAmount(new BigDecimal(20)).currencyCode("CHF").accountingValue(new BigDecimal(100)).payee(someone).build();
		x.addTransaction(TransactionEntry.with().factor(TransactionFactor.OUT).currencyAmount(new BigDecimal(100)).accountingValue(new BigDecimal(100)).build());
		x.addTransaction(TransactionEntry.with().factor(TransactionFactor.IN).currencyAmount(new BigDecimal(100)).accountingValue(new BigDecimal(100)).build());
		Expense x1 = expenseService.save(x);
		log.info("\n\t===== Saved 1 Expense {} ====", x1);
		assertThat(x1.getVersion()).isEqualTo(0L);
		
		x1.setPayee(Payee.with().name("New Payee").build()); // new payee
		Expense x2 = expenseService.save(x1);
		log.info("\n\t===== Saved 2 Expense {} ====", x2);
		assertThat(x2.getVersion()).isEqualTo(1L);
		
		x2.setPayee(someone); // existing payee
		x2.getTransactions().get(1).setCurrencyAmount(new BigDecimal(90)).setAccountingValue(new BigDecimal(90)); // modify transaction
		x2.addTransaction(TransactionEntry.with().factor(TransactionFactor.IN).currencyAmount(new BigDecimal(10)).accountingValue(new BigDecimal(10)).build()); // new transaction
		Expense x3 = expenseService.save(x2);
		log.info("\n\t===== Saved 3 Expense {} ====", x3);
		assertThat(x3.getVersion()).isEqualTo(2L);
		assertThat(x3.getTransactions()).hasSize(3);
	}
	
	
	@Test
	@Order(10)
	public void test_listAll() {
		log.info("\n\t===== All Expenses {} ====", expenseService.loadAll());
	}
	
	@Test
	@Order(11)
	public void test_findByUid() {
		log.info("\n\t===== Find by uid {} ====", expenseService.loadByUid(EXPENSE_UID_1));
	}
	
	@Test
	@Order(100)
	public void test_delete() {
		expenseService.delete(expenseService.loadByUid(EXPENSE_UID_1));
		log.info("\n\t===== deleted uid {} ====", EXPENSE_UID_1);
		assertThat(expenseService.loadByUid(EXPENSE_UID_1)).isNull();
	}
}
