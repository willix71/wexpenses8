package w.expenses8;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.config.DataConfig;
import w.expenses8.data.domain.criteria.ExpenseCriteria;
import w.expenses8.data.domain.service.IExpenseService;
import w.expenses8.web.servlet.FileServlet;

@Slf4j
@Import(DataConfig.class)
@SpringBootApplication
public class Wexpenses8Application {

	public static void main(String[] args) {
		SpringApplication.run(Wexpenses8Application.class, args);
	}

	@Bean
	public ServletRegistrationBean<FileServlet> servletRegistrationBean(@Value("${wexpenses.documents.root}") String root) {
	    return new ServletRegistrationBean<FileServlet>(new FileServlet(root),"/documents/*");
	} 
	
	@Bean
	public CommandLineRunner demo(IExpenseService service) {
		return (args) -> {
			log.info("Started up with {} expenses", service.findExpenses(ExpenseCriteria.with().build()).size());
		};
	}
}
