package w.expenses8.data.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages="w.expenses8.data.domain.dao") 
@ComponentScan(basePackages="w.expenses8.data.domain.service")
public class DataConfig {

}
