package w.expenses8.data.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
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
import w.expenses8.data.core.criteria.RangeCriteria;
import w.expenses8.data.domain.criteria.TransactionEntryCriteria;
import w.expenses8.data.domain.model.ExpenseType;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.model.Tag;
import w.expenses8.data.domain.model.TransactionEntry;
import w.expenses8.data.domain.model.enums.TagType;
import static w.expenses8.data.utils.DateHelper.*;
import w.expenses8.data.utils.ExpenseHelper;

@Slf4j
@SpringBootTest
@Import(DataConfig.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create-drop","spring.datasource.url=jdbc:h2:mem:TransactionEntryServiceTest;DB_CLOSE_DELAY=-1"})
public class TransactionEntryServiceTest {

	@Autowired
	IExpenseService expenseService;
	
	@Autowired
	ITransactionEntryService transactionEntryService;
	
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
	public void test_findTransactionEntrys() {
		Payee migros = storeService.save(Payee.with().name("Migros").build());
		Payee doctor = storeService.save(Payee.with().name("Doctor").build());
		Tag cash = storeService.save(Tag.with().type(TagType.ASSET).name("cash").build());
		Tag visa = storeService.save(Tag.with().type(TagType.LIABILITY).name("visa").build());
		Tag grocery = storeService.save(Tag.with().type(TagType.EXPENSE).name("grocery").build());
		Tag health = storeService.save(Tag.with().type(TagType.EXPENSE).name("health").build());
		Tag kim = storeService.save(Tag.with().type(TagType.DISCRIMINATOR).name("kim").build());
		Tag shaun = storeService.save(Tag.with().type(TagType.DISCRIMINATOR).name("shaun").build());
		ExpenseType bvo = storeService.save(ExpenseType.with().name("BVO").build());
		
		expenseService.save(ExpenseHelper.build(toDate(10,6,2019),new BigDecimal(20),"CHF",migros, cash, grocery));
		expenseService.save(ExpenseHelper.build(toDate(4,12,2019),new BigDecimal(30),"USD",migros, visa, grocery, Tag.with().type(TagType.EXPENSE).name("beer").build()));
		expenseService.save(ExpenseHelper.build(bvo, new Date(),new BigDecimal(40),"CHF",doctor, cash, TransactionEntry.in(health, kim)));
		expenseService.save(ExpenseHelper.build(bvo, new Date(),new BigDecimal(50),"CHF",doctor, cash, TransactionEntry.in(health, shaun)));
		assertThat(expenseService.count()).isEqualTo(4);
		assertThat(transactionEntryService.count()).isEqualTo(9);
		
		List<TransactionEntry> accounted2019 = transactionEntryService.findTransactionEntrys(TransactionEntryCriteria.with().accountingYear(2019).build());
		assertThat(accounted2019).hasSize(5);
		
		List<TransactionEntry> year2019 = transactionEntryService.findTransactionEntrys(TransactionEntryCriteria.with().date(new RangeCriteria<Date>(toDate(1,1,2019), toDate(1,1,2020))).build());
		assertThat(year2019).hasSize(5);
		
		List<TransactionEntry> chf = transactionEntryService.findTransactionEntrys(TransactionEntryCriteria.with().currencyCode("USD").build());
		assertThat(chf).hasSize(3);
		
		List<TransactionEntry> cashes = transactionEntryService.findTransactionEntrys(TransactionEntryCriteria.with().tags(Collections.singleton(cash)).build());
		assertThat(cashes).hasSize(3);

		List<TransactionEntry> healthkim = transactionEntryService.findTransactionEntrys(TransactionEntryCriteria.with().tags(Arrays.asList(health, kim)).build());
		assertThat(healthkim).hasSize(1);
		assertThat(healthkim.get(0).getExpense().getPayee().getName()).isEqualTo("Doctor");
		assertThat(healthkim.get(0).getExpense().getExpenseType().getName()).isEqualTo("BVO");

		List<TransactionEntry> bvos = transactionEntryService.findTransactionEntrys(TransactionEntryCriteria.with().expenseType(bvo).build());
		assertThat(bvos).hasSize(4);
		
		List<TransactionEntry> groceries = transactionEntryService.findTransactionEntrys(TransactionEntryCriteria.with().accountingValue(new RangeCriteria<BigDecimal>(new BigDecimal(40),new BigDecimal(50))).build());
		assertThat(groceries).hasSize(2);
	}
}
