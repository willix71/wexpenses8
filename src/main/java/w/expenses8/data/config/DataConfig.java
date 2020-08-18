package w.expenses8.data.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages="w.expenses8.data.dao.domain") 
@ComponentScan(basePackages="w.expenses8.data.service.domain")
public class DataConfig {

}
