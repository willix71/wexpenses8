package w.expenses8.data.domain.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Getter;
import w.expenses8.data.domain.model.Country;

@Getter
@Service
public class CountryService {

	private final List<Country> countries;
	
	private final List<String> countriesCodes;
	
	private final List<String> currenciesCodes;
	
	@Autowired
	public CountryService(EntityManager entityManager) {
		countries = entityManager.createQuery("from " + Country.class.getSimpleName(), Country.class).getResultList();
		countriesCodes = countries.stream().map(c->c.getCountryCode()).sorted().collect(Collectors.toList());
		currenciesCodes = countries.stream().map(c->c.getCurrencyCode()).sorted().distinct().collect(Collectors.toList());
	}
	
	public String getCurrency(String countryCode) {
		return countryCode==null?null:countries.stream().filter(c->countryCode.equals(c.getCountryCode())).map(c->c.getCurrencyCode()).findFirst().orElse(null);
	}
}
