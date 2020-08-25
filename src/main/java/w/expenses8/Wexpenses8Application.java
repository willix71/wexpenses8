package w.expenses8;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import w.expenses8.data.config.DataConfig;

@Import(DataConfig.class)
@SpringBootApplication
public class Wexpenses8Application {

	public static void main(String[] args) {
		SpringApplication.run(Wexpenses8Application.class, args);
	}
}
