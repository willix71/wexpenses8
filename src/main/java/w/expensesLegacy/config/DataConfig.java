package w.expensesLegacy.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = {"w.expenses8.data.domain.model","w.expensesLegacy.data.domain.model"})
@EnableJpaRepositories(basePackages={"w.expenses8.data.domain","w.expensesLegacy.data.domain.dao"}) 
@ComponentScan(basePackages={"w.expenses8.data.domain.service","w.expensesLegacy.data.domain.service"})
public class DataConfig {

	@Bean
	public DataSourceInitializer dataSourceInitializer(final DataSource dataSource) {
	    ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();	    
	    resourceDatabasePopulator.addScript(new FileSystemResource("db.sql"));
	    
	    DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
	    dataSourceInitializer.setEnabled(true);
	    dataSourceInitializer.setDataSource(dataSource);
	    dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
	    return dataSourceInitializer;
	}
}
