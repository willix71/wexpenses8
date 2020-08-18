package w.expenses8;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.config.DataConfig;
import w.expenses8.data.service.domain.IPayeeService;

@Slf4j
@Import(DataConfig.class)
@SpringBootApplication
public class Wexpenses8Application {

	public static void main(String[] args) {
		SpringApplication.run(Wexpenses8Application.class, args);
	}

	  @Bean
	  public CommandLineRunner demo(IPayeeService repository) {
	    return (args) -> {
	    	log.info("Started up");
//	    	Payee p = Payee.with().name("test").build();
//	    	p.setName("Test");
//	    	log.info("New payee {}", p);
//	    	log.info("Saved payee {}", repository.save(p));
//	    	
//	    	List<Payee> payees = repository.loadAll();
//	    	System.out.println(payees);
	    };
	  }
}
