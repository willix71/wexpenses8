package w.expenses8.data.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import w.expenses8.data.core.criteria.RangeNumberCriteria;
import w.expenses8.data.domain.criteria.ExpenseCriteria;
import w.expenses8.data.domain.model.DocumentFile;
import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.model.Tag;
import w.expenses8.data.domain.model.TransactionEntry;
import w.expenses8.data.domain.model.enums.TagType;
import w.expenses8.data.utils.CollectionHelper;
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
		Tag cach = Tag.with().type(TagType.ASSET).name("cash").build();
		Tag tax = Tag.with().type(TagType.EXPENSE).name("tax").build();
		
		Expense x = ExpenseHelper.build(new Date(),new BigDecimal(20),"CHF",someone, cach, tax);
		Expense saved = expenseService.save(x);
		log.info("\n\t==== Saved Expense {} ====", saved);
		assertThat(x).isSameAs(saved);
		assertThat(saved.getId()).isNotNull();
		Expense loaded =expenseService.load(saved.getId());
		assertThat(loaded).isNotNull().isNotSameAs(saved);
		
		Expense reloaded =expenseService.reload(saved);
		assertThat(reloaded.getDocumentFiles()).isEmpty();
		assertThat(reloaded.getPayee().getName()).isEqualTo("Someone");
	}
	
	@Test
	@Order(2)
	public void test_insertWithExistingRelation() {
		Payee someone = storeService.save(Payee.with().name("SomeoneElse").build());
		Tag mastercard = storeService.save(Tag.with().type(TagType.ASSET).name("mastercard").build());
		Tag insurance = storeService.save(Tag.with().type(TagType.EXPENSE).name("insurance").build());
		
		Expense x = ExpenseHelper.build(new Date(),new BigDecimal(50),"CHF",someone, mastercard, insurance);
		Expense saved = expenseService.save(x);
		log.info("\n\t===== Saved Expense {} ====", saved);
		assertThat(saved.getId()).isNotNull();
	}
	
	@Test
	@Order(3)
	public void test_update3() {
		Payee someone = storeService.save(Payee.with().name("Mr and Mrs Smith").build());
		Tag visa = storeService.save(Tag.with().type(TagType.ASSET).name("visa").build());
		Tag grocery = storeService.save(Tag.with().type(TagType.EXPENSE).name("grocery").build());
		
		Expense x = ExpenseHelper.build(new Date(),new BigDecimal(100),"CHF",someone, visa, grocery);
		Expense saved = expenseService.save(x);
		log.info("\n\t===== Saved 1 Expense {} ====", saved);
		assertThat(saved.getVersion()).isEqualTo(0L);
		
		// need to reload so that the documentFiles collections is reloaded
		saved = expenseService.reload(saved);
		
		saved.setPayee(Payee.with().name("New Payee").build()); // new payee
		Expense updated1 = expenseService.save(saved);
		log.info("\n\t===== Saved 2 Expense {} ====", updated1);
		assertThat(updated1.getVersion()).isEqualTo(1L);
		
		updated1.setPayee(someone); // existing payee
		TransactionEntry in1 = CollectionHelper.last(updated1.getTransactions());
		in1.setCurrencyAmount(new BigDecimal(90));
		in1.setAccountingValue(new BigDecimal(90));
		Tag newTag1 = Tag.with().type(TagType.EXPENSE).name("beer").build(); // new tag
		Tag newTag2 = Tag.with().type(TagType.EXPENSE).name("wine").build(); // new tag
		updated1.addTransaction(TransactionEntry.in(newTag1,newTag2)); // add new transaction entry
		updated1.updateValues(null);
		Expense updated2 = expenseService.save(updated1);
		log.info("\n\t===== Saved 3 Expense {} ====", updated2);
		assertThat(updated2.getVersion()).isEqualTo(2L);
		assertThat(updated2.getTransactions()).hasSize(3);
		
		// load using the criteria (loads all in one)
		List<Expense> loadeds = expenseService.findExpenses(ExpenseCriteria.with().amountValue(new RangeNumberCriteria(new BigDecimal(100),null)).build());
		assertThat(loadeds).hasSize(1);
		Expense loaded = CollectionHelper.first(loadeds);		
		assertThat(loaded.getVersion()).isEqualTo(2L);
		assertThat(loaded.getTransactions()).hasSize(3);
		
	}
	
	
	@Test
	@Order(10)
	public void test_listAll() {
		List<Expense> all = expenseService.loadAll();
		assertThat(all).isNotEmpty();
		log.info("\n\t===== All Expenses {} ====", all);
		assertThat(all).hasSize(3);
		
		all.stream().forEach(e->log.info(ExpenseHelper.toString(e)));
	}
	
	@Test
	@Order(10)
	public void test_listByCriteria() {
		List<Expense> all = expenseService.findExpenses(ExpenseCriteria.with().amountValue(new RangeNumberCriteria(new BigDecimal(50),new BigDecimal(100))).build());
		log.info("\n\t===== Criteria Expenses {} ====", all);		
		all.stream().forEach(e->log.info(ExpenseHelper.toString(e)));
		assertThat(all).hasSize(1);
	}

	@Test
	@Order(10)
	public void test_listByCriteriaPayeeText() {
		List<Expense> all = expenseService.findExpenses(ExpenseCriteria.with().amountValue(new RangeNumberCriteria(new BigDecimal(5),null)).payeeText("someone").build());
		log.info("\n\t===== Someone's Expenses {} ====", all);		
		all.stream().forEach(e->log.info(ExpenseHelper.toString(e)));
		assertThat(all).hasSize(2);
	}
	
	@Test
	@Order(100)
	public void test_delete() {
		Payee nonono = storeService.save(Payee.with().name("No no no").uid("uid-no-no-no").build());
		Tag in = storeService.save(Tag.with().type(TagType.ASSET).name("in").uid("uid-in").build());
		Tag out = storeService.save(Tag.with().type(TagType.EXPENSE).name("out").uid("uid-out").build());
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
	
	@Test
	@Order(200)
	public void test_reload() {
		List<Expense> all = expenseService.loadAll();
		
		assertThat(expenseService.reload(all.get(0)).getDocumentFiles()).hasSize(0);
	}
	
	@Test
	@Order(300)
	public void test_documentFile() {
		Payee tempo = Payee.with().name("Temporary").build();
		Tag tmp1 = Tag.with().type(TagType.ASSET).name("temp1").build();
		Tag tmp2 = Tag.with().type(TagType.EXPENSE).name("temp2").build();
		
		Expense x1 = ExpenseHelper.build(new Date(),new BigDecimal(1000),"CHF",tempo, tmp1,  tmp2);
		x1.addDocumentFile(new DocumentFile(LocalDate.now(), "test file 1"));
		expenseService.save(x1);
		
		// simple id check
		assertThat(x1.getId()).isNotNull();
		
		Expense r1 = expenseService.reload(x1);
		assertThat(r1.getDocumentFiles()).hasSize(1);
		assertThat(r1.getDocumentCount()).isEqualTo(1);
		assertThat(storeService.loadAll(DocumentFile.class)).hasSize(1).extracting(f->f.getFileName()).contains("test file 1");
		
		// add another document
		Expense x2 = expenseService.reload(x1); 
		x2.addDocumentFile(new DocumentFile(LocalDate.of(2000, 1, 1), "test file 2"));
		expenseService.save(x2);
		
		Expense r2 = expenseService.reload(x2);
		assertThat(r2.getDocumentFiles()).hasSize(2);
		assertThat(r2.getDocumentCount()).isEqualTo(2);
		assertThat(storeService.loadAll(DocumentFile.class)).hasSize(2).extracting(f->f.getFileName()).contains("test file 1","test file 2");
		
		Expense x3 = expenseService.reload(x2); 
		DocumentFile doc = CollectionHelper.first(x3.getDocumentFiles());
		x3.removeDocumentFile(doc);		
		expenseService.save(x3);
		
		Expense r3 = expenseService.reload(x2);
		assertThat(r3.getDocumentFiles()).hasSize(1);
		assertThat(r3.getDocumentCount()).isEqualTo(1);
		
		// documents have to be explicitly removed
		// assertThat(storeService.loadAll(DocumentFile.class)).hasSize(1).extracting(f->f.getFileName()).contains("test file 1");
	}
}
