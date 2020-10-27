package w.expenses8.data.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
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
import w.expenses8.data.domain.model.Consolidation;
import w.expenses8.data.domain.model.DocumentFile;
import w.expenses8.data.domain.model.Payee;

@Slf4j
@SpringBootTest
@Import(DataConfig.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create-drop","spring.datasource.url=jdbc:h2:mem:ConsolidationServiceTest;DB_CLOSE_DELAY=-1"})
public class ConsolidationServiceTest {

	@Autowired
	IConsolidationService service;
	
	@Test
	@Order(1)
	public void testListAll1() {
		List<Consolidation> all = service.loadAll();
		assertThat(all).isNotNull().isEmpty();
	}
	
	@Test
	@Order(2)
	public void testInsert() {
		Consolidation conso = Consolidation.with()
				.date(LocalDate.now())
				.institution(Payee.with().name("CONSOTEST").build())
				.documentFile(DocumentFile.with().documentDate(LocalDate.now()).fileName("filename").build())
				.build();
		service.save(conso);
		assertThat(conso.getId()).isNotNull();
	}
	
	@Test
	@Order(10)
	public void testListAll2() {
		List<Consolidation> all = service.loadAll();
		assertThat(all).isNotNull().isNotEmpty();
		assertThat(all.get(0).getInstitution().getName()).isNotNull();
		assertThat(all.get(0).getDocumentFile().getFileName()).isNotNull();
		//assertThat(all.get(0).getOpeningEntry().getCurrencyAmount()).isNotNull();
		//assertThat(all.get(0).getClosingEntry().getCurrencyAmount()).isNotNull();
	}
}
