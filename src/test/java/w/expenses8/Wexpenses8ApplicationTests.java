package w.expenses8;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create-drop","spring.datasource.url=jdbc:h2:mem:Wexpenses8ApplicationTests;DB_CLOSE_DELAY=-1"})
class Wexpenses8ApplicationTests {

	@Test
	void contextLoads() {
	}

}
