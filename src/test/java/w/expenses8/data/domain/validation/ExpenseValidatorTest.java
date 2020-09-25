package w.expenses8.data.domain.validation;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.model.Tag;
import w.expenses8.data.domain.model.TransactionEntry;
import w.expenses8.data.domain.model.enums.TagType;
import w.expenses8.data.domain.service.IExpenseService;
import w.expenses8.data.utils.CollectionHelper;
import w.expenses8.data.utils.ExpenseHelper;

@Slf4j
@SpringBootTest
@Import(DataConfig.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = { "spring.jpa.hibernate.ddl-auto=create-drop",	"spring.datasource.url=jdbc:h2:mem:ExpenseValidatorTest;DB_CLOSE_DELAY=-1" })
public class ExpenseValidatorTest {

	@Autowired
	IExpenseService expenseService;
	
	@BeforeEach
	public void separatorBefore(TestInfo info) {
		log.info("\n\n---- Running {} -----", info.getDisplayName());		
	}
	
	@AfterEach
	public void separatorAfter(TestInfo info) {
		log.info("\n---- Run {} -----\n", info.getDisplayName());		
	}
	
	private Expense getExpense(int index) {
		Payee someone = Payee.with().name("Someone"+index).build();
		Tag cach = Tag.with().type(TagType.ASSET).name("cash"+index).build();
		Tag tax = Tag.with().type(TagType.EXPENSE).name("tax"+index).build();
		Expense x = ExpenseHelper.build(new Date(),new BigDecimal(20),"CHF",someone, cach, tax);
		return x;
	}
	@Test
	@Order(0)
	public void test_OK() {
		Long id = expenseService.save(getExpense(0)).getId();
		
		Expense x = expenseService.reload(id);
		
		// modify values of first transactions
		CollectionHelper.first(x.getTransactions()).setAccountingYear(2010);;
		
		Expense saved = expenseService.save(x);
		saved.getFullId();
	}
	
	@Test
	@Order(1)
	public void test_insertNewInvalidExpense() {
		Expense x = getExpense(1);
		
		// modify values of first transactions
		TransactionEntry out = CollectionHelper.first(x.getTransactions());
		out.setCurrencyAmount(new BigDecimal(30));
		out.setAccountingValue(new BigDecimal(30));

		// try saving
		Assertions.assertThrows(ConstraintViolationException.class, () -> {
			Expense saved = expenseService.save(x);
			saved.getFullId();
		});
	}

	@Test
	@Order(2)
	public void test_modifyInvalidExpense() {
		Long id = expenseService.save(getExpense(2)).getId();	
		Expense x = expenseService.reload(id);
		
		// modify values of first transactions
		TransactionEntry out = CollectionHelper.first(x.getTransactions());
		out.setCurrencyAmount(new BigDecimal(30));
		out.setAccountingValue(new BigDecimal(30));

		// try saving
		Assertions.assertThrows(ConstraintViolationException.class, () -> {
			Expense saved = expenseService.save(x);
			saved.getFullId();
		});
	}
}
