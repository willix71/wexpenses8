package w.expenses8.data.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import w.expenses8.data.config.DataConfig;
import w.expenses8.data.domain.model.DocumentFile;
import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.model.Payee;

@SpringBootTest
@Import(DataConfig.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create-drop","spring.datasource.url=jdbc:h2:mem:DocumentServiceTest;DB_CLOSE_DELAY=-1"})
public class DocumentServiceTest {

	@Autowired
	IDocumentFileService service;
	
	@Test
	public void testGenerateFilename() {
		String filename = service.generateFilename(LocalDate.of(2010, 1, 2),Expense.with().currencyAmount(new BigDecimal("10.00")).currencyCode("CHF").payee(Payee.with().name("hello world").build()).build());
		assertThat(filename).startsWith("20100102 10.00CHF hello_world").endsWith(".pdf");
	}

	@Test
	public void testGetUrl() {
		String filename = service.getUrl(DocumentFile.with().fileName("ThisIsATest").documentDate(LocalDate.of(2012, 1, 31)).build());
		assertThat(filename).isEqualTo("/somewhere/2012/ThisIsATest");
	}
}
