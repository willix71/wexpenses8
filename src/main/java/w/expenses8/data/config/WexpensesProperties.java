package w.expenses8.data.config;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties("wexpenses")
@Getter @Setter
public class WexpensesProperties {

	private Properties currencies;
	
	public List<String> getCountries() {
		return currencies.keySet().stream().map(s->(String) s).sorted().collect(Collectors.toList());
	}
	
	public List<String> getCurrencies() {
		return currencies.values().stream().map(s->(String) s).sorted().distinct().collect(Collectors.toList());
	}
}
