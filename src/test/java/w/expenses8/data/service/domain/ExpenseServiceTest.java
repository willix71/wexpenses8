package w.expenses8.data.service.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
import w.expenses8.data.criteria.core.RangeCriteria;
import w.expenses8.data.criteria.domain.ExpenseCriteria;
import w.expenses8.data.model.domain.Expense;
import w.expenses8.data.model.domain.Payee;
import w.expenses8.data.model.domain.Tag;
import w.expenses8.data.model.domain.TransactionEntry;
import w.expenses8.data.model.enums.TagEnum;
import w.expenses8.data.model.enums.TransactionFactor;
import w.expenses8.data.utils.ExpenseHelper;

@Slf4j
@SpringBootTest
@Import(DataConfig.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create-drop","spring.datasource.url=jdbc:h2:mem:ExpenseServiceTest;DB_CLOSE_DELAY=-1"})
public class ExpenseServiceTest {

	@Autowired
	IExpenseService expenseService;
	
	@Autowired
	ITagService tagService;
	
	@Autowired
	private StoreService storeService;
	
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
	public void test_insertWithNewRelations() {
		Payee someone = Payee.with().name("Someone").build();
		Tag cach = Tag.with().type(TagEnum.ASSET).name("cash").build();
		Tag tax = Tag.with().type(TagEnum.EXPENSE).name("tax").build();
		
		Expense x = ExpenseHelper.build(new Date(),new BigDecimal(20),"CHF",someone, cach, tax);
		Expense saved = expenseService.save(x);
		log.info("\n\t==== Saved Expense {} ====", saved);
		assertThat(x).isSameAs(saved);
		assertThat(saved.getId()).isNotNull();
		Expense loaded =expenseService.load(saved.getId());
		assertThat(loaded).isNotNull().isNotSameAs(saved);
	}
	
	@Test
	@Order(2)
	public void test_insertWithExistingRelation() {
		Payee someone = storeService.save(Payee.with().name("SomeoneElse").build());
		Tag mastercard = storeService.save(Tag.with().type(TagEnum.ASSET).name("mastercard").build());
		Tag insurance = storeService.save(Tag.with().type(TagEnum.EXPENSE).name("insurance").build());
		
		Expense x = ExpenseHelper.build(new Date(),new BigDecimal(50),"CHF",someone, mastercard, insurance);
		Expense saved = expenseService.save(x);
		log.info("\n\t===== Saved Expense {} ====", saved);
		assertThat(saved.getId()).isNotNull();
	}
	
	@Test
	@Order(3)
	public void test_update3() {
		Payee someone = storeService.save(Payee.with().name("Mr and Mrs Smith").build());
		Tag visa = storeService.save(Tag.with().type(TagEnum.ASSET).name("visa").build());
		Tag grocery = storeService.save(Tag.with().type(TagEnum.EXPENSE).name("grocery").build());
		
		Expense x = ExpenseHelper.build(new Date(),new BigDecimal(100),"CHF",someone, visa, grocery);
		Expense saved = expenseService.save(x);
		log.info("\n\t===== Saved 1 Expense {} ====", saved);
		assertThat(saved.getVersion()).isEqualTo(0L);
		
		saved.setPayee(Payee.with().name("New Payee").build()); // new payee
		Expense updated1 = expenseService.save(saved);
		log.info("\n\t===== Saved 2 Expense {} ====", updated1);
		assertThat(updated1.getVersion()).isEqualTo(1L);
		
		updated1.setPayee(someone); // existing payee
		updated1.getTransactions().get(1).setCurrencyAmount(new BigDecimal(90)).setAccountingValue(new BigDecimal(90)); // modify transaction
		Tag newTag = Tag.with().type(TagEnum.EXPENSE).name("beer").build(); // new tag
		TransactionEntry newEntry = new  TransactionEntry().setFactor(TransactionFactor.IN).addTag(newTag); // new transaction entry
		updated1.addTransaction(newEntry); // add new transaction
		updated1.updateValues();
		Expense updated2 = expenseService.save(updated1);
		log.info("\n\t===== Saved 3 Expense {} ====", updated2);
		assertThat(updated2.getVersion()).isEqualTo(2L);
		assertThat(updated2.getTransactions()).hasSize(3);
	}
	
	
	@Test
	@Order(10)
	public void test_listAll() {
		List<Expense> all = expenseService.loadAll();
		assertThat(all).isNotEmpty();
		log.info("\n\t===== All Expenses {} ====", all);
		
		all.stream().forEach(e->log.info(ExpenseHelper.toString(e)));
	}
	
	@Test
	@Order(10)
	public void test_listByCriteria() {
		List<Expense> all = expenseService.findExpenses(ExpenseCriteria.with().currencyAmount(new RangeCriteria<BigDecimal>(new BigDecimal(50),new BigDecimal(100))).build());
		log.info("\n\t===== Criteria Expenses {} ====", all);		
		all.stream().forEach(e->log.info(ExpenseHelper.toString(e)));
		assertThat(all).hasSize(1);
	}

	@Test
	@Order(100)
	public void test_delete() {
		Payee nonono = storeService.save(Payee.with().name("No no no").uid("uid-no-no-no").build());
		Tag in = storeService.save(Tag.with().type(TagEnum.ASSET).name("in").uid("uid-in").build());
		Tag out = storeService.save(Tag.with().type(TagEnum.EXPENSE).name("out").uid("uid-out").build());
		Expense x = ExpenseHelper.build(new Date(),new BigDecimal(100),"CHF",nonono, in, out);
		expenseService.save(x);
		
		assertThat(expenseService.loadByUid(x.getUid())).isNotNull();
		
		expenseService.delete(expenseService.loadByUid(x.getUid()));
		log.info("\n\t===== deleted uid {} ====", x.getUid());
		assertThat(expenseService.loadByUid(x.getUid())).isNull();
		
		assertThat(storeService.load(Payee.class, nonono.getId())).isNotNull();
		assertThat(storeService.load(Tag.class, in.getId())).isNotNull();
		assertThat(storeService.load(Tag.class, out.getId())).isNotNull();
	}
}
