package w.expenses8;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.config.DataConfig;
import w.expenses8.data.domain.criteria.ExpenseCriteria;
import w.expenses8.data.domain.service.IExpenseService;

@Slf4j
@SpringBootTest
@Import(DataConfig.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create-drop","spring.datasource.url=jdbc:h2:mem:WexpensesUpdateTool;DB_CLOSE_DELAY=-1"})
public class WexpensesUpdateTool {

	@Autowired
	IExpenseService service;
	
	@Test
	public void update() {
		log.info("Started up with {} expenses", service.findExpenses(ExpenseCriteria.with().build()).size());

	}
}
