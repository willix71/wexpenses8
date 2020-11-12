package w.expensesLegacy.config;

import java.io.InputStream;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.google.common.io.ByteStreams;

import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.config.CurrencyValue;

@Slf4j
@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = {"w.expenses8.data.domain.model","w.expensesLegacy.data.domain.model"})
@EnableJpaRepositories(basePackages={"w.expenses8.data.domain","w.expensesLegacy.data.domain.dao"}) 
@ComponentScan(basePackages={"w.expenses8.data.domain.service","w.expensesLegacy.data.domain.service"})
public class DataConfig {

	@Bean public CurrencyValue currencyValue() { return new CurrencyValue(); }
	
	@Bean
	public DataSourceInitializer dataSourceInitializer(final DataSource dataSource) throws Exception {
		try(InputStream is = new FileSystemResource("db.sql").getInputStream()) {
			log.info("Creating new database with SQL\n{}", new String(ByteStreams.toByteArray(is)));
		}
		
		ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();	    
	    resourceDatabasePopulator.addScript(new FileSystemResource("db.sql"));
	    
	    DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
	    dataSourceInitializer.setEnabled(true);
	    dataSourceInitializer.setDataSource(dataSource);
	    dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
	    return dataSourceInitializer;
	}
}
