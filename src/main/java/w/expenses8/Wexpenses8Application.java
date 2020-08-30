package w.expenses8;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.config.DataConfig;
import w.expenses8.data.config.WexpensesProperties;

@Slf4j
@Import(DataConfig.class)
@SpringBootApplication
public class Wexpenses8Application {

	public static void main(String[] args) {
		SpringApplication.run(Wexpenses8Application.class, args);
	}
	
	  @Bean
	  public CommandLineRunner demo(WexpensesProperties props) {
	    return (args) -> {
	    	log.info("Started up");
	    };
	  }
}
