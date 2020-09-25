package w.expenses8.data.domain.validation;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import w.expenses8.data.config.DataConfig;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.service.IPayeeService;

@SpringBootTest
@Import(DataConfig.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = { "spring.jpa.hibernate.ddl-auto=create-drop",	"spring.datasource.url=jdbc:h2:mem:IbanValidatorTest;DB_CLOSE_DELAY=-1" })
public class IbanValidatorTest {

	@Autowired
	IPayeeService payeeService;

	@Test
	public void testPayee() {
		assertThat(payeeService.save(Payee.with().name("test1").build())).hasFieldOrProperty("id");
	}

	@Test
	public void testPayeeWithPostalAccount() {
		assertThat(payeeService.save(Payee.with().name("test2").postalAccount("01-01234-1").build()))
				.hasFieldOrProperty("id");
	}

	@Test()
	public void testPayeeWithBadPostalAccount() {
		 Assertions.assertThrows(ConstraintViolationException.class, () -> {
			 payeeService.save(Payee.with().name("test3").postalAccount("asdfg").build());
		 });
	}
	
	@Test()
	public void testPayeeWithIban() {
		assertThat(payeeService.save(Payee.with().name("test4").iban("BE62 5100 0754 7061").build()))
				.hasFieldOrProperty("id");
	}

	@Test()
	public void testPayeeWithBadIban() {
		Assertions.assertThrows(ConstraintViolationException.class, () -> {
			payeeService.save(Payee.with().name("test4").iban("asdfg").build());
		});
	}
}
