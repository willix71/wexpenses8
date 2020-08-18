package w.expenses8.data.service.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.config.DataConfig;
import w.expenses8.data.model.domain.Payee;

@Slf4j
@SpringBootTest
@Import(DataConfig.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create-drop","spring.datasource.url=jdbc:h2:mem:PayeeServiceTest;DB_CLOSE_DELAY=-1"})
public class PayeeServiceTest {

	@Autowired
	IPayeeService payeeService;
	
	@Test
	@Order(1)
	public void test_setup() {
		assertThat(payeeService).isNotNull();
		assertThat(payeeService.loadAll()).isNotNull();
	}
	
	
	@Test
	@Order(2)
	public void test_insert() {
		Payee p = payeeService.save(Payee.with().name("Test1").build());
		log.info("\\n\\t === Saved Payee {}", p);
		assertThat(p.getId()).isNotNull();
	}
	
	@Test
	@Order(3)
	public void test_list() {
		List<Payee> payees = payeeService.loadAll();
		log.info("\n\t === All Payees {}", payees);
		assertThat(payees).hasSize(1);
	}
	
	@Test
	@Order(4)
	public void test_findByName() {
		Payee p1 = payeeService.save(Payee.with().name("Test2").build());
		
		Payee p2 = payeeService.findByName("Test2");
		assertThat(p2).isEqualTo(p1);
	}
	
	@Test
	@Order(5)
	public void test_findByText() {
		Payee p1 = payeeService.save(Payee.with().name("XXX").extra("TestX").build());
		Payee p2 = payeeService.save(Payee.with().name("Something else").build());
		List<Payee> ps = payeeService.findByText("Test");
		assertThat(ps).hasSize(3).contains(p1).doesNotContain(p2);
	}
}
