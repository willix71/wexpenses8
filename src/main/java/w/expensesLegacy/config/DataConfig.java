package w.expensesLegacy.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = {"w.expenses8.data.domain.model","w.expensesLegacy.data.domain.model"})
@EnableJpaRepositories(basePackages={"w.expenses8.data.domain","w.expensesLegacy.data.domain.dao"}) 
@ComponentScan(basePackages={"w.expenses8.data.domain.service","w.expensesLegacy.data.domain.service"})
public class DataConfig {

}
