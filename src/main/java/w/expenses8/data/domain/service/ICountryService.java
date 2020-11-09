package w.expenses8.data.domain.service;

import java.util.List;

public interface ICountryService {

	List<String> getCountriesCodes();

	List<String> getCurrenciesCodes();
	
	String getCurrency(String countryCode);
}
