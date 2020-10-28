package w.expensesLegacy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import lombok.extern.slf4j.Slf4j;
import w.expensesLegacy.config.DataConfig;
import w.expensesLegacy.data.domain.service.MigrateService;

@Slf4j
@Import(DataConfig.class)
@SpringBootApplication
public class WexpensesLegacyApplication {

	public static void main(String[] args) {
		SpringApplication.run(WexpensesLegacyApplication.class, args);
	}

	  @Bean
	  public CommandLineRunner demo(MigrateService service) {
	    return (args) -> {
	    	log.info("Started up");
	    	service.migrate();
	    	service.consolidate();
	    	log.info("Done migrating");
	    };
	  }
}
