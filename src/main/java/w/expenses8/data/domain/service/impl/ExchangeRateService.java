package w.expenses8.data.domain.service.impl;

import java.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.expenses8.data.config.CurrencyValue;
import w.expenses8.data.core.service.GenericServiceImpl;
import w.expenses8.data.domain.dao.IExchangeRateDao;
import w.expenses8.data.domain.model.ExchangeRate;
import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.service.IExchangeRateService;
import w.expenses8.data.domain.service.IPayeeService;

@Service
public class ExchangeRateService extends GenericServiceImpl<ExchangeRate, Long, IExchangeRateDao> implements IExchangeRateService {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private CurrencyValue currencyValue;
	
	@Autowired
	private IPayeeService payeeService;
	
	@Autowired
	public ExchangeRateService(IExchangeRateDao dao) {
		super(ExchangeRate.class, dao);
	}
	
	@Override
	public ExchangeRate copyExchangeRate(ExchangeRate source) {
		return ExchangeRate.with()
			.date(source.getDate())
			.institution(source.getInstitution()==null || source.getInstitution().getId()==null?null:payeeService.reload(source.getInstitution().getId()))
			.fromCurrencyCode(source.getFromCurrencyCode())
			.toCurrencyCode(source.getToCurrencyCode())
			.rate(source.getRate())
			.fee(source.getFee())
			.fixFee(source.getFixFee())
			.build();
	}

	@Override
	public ExchangeRate buildExchangeRate(Expense x) {
		Long institutionId = x.getTransactions().stream().flatMap(f->f.getTags().stream()).filter(f->f.getInstitution()!=null && f.getInstitution().getId()!=null).map(f->f.getInstitution().getId()).findFirst().orElse(null);
		Payee institution = payeeService.reload(institutionId!=null?institutionId:x.getPayee());

		LocalDate date = x.getDate()!=null?x.getDate().toLocalDate():null;

		ExchangeRate newRate = ExchangeRate.with()
				.institution(institution)
				.date(date)
				.fromCurrencyCode(x.getCurrencyCode())
				.toCurrencyCode(currencyValue.getCode())
				.build();

		ExchangeRate lastRate = getDao().findFirstByDateLessThanEqualAndInstitutionAndFromCurrencyCodeAndToCurrencyCodeOrderByDateDesc(date!=null?date:LocalDate.now(), institution, x.getCurrencyCode(), currencyValue.getCode());
		if (lastRate!=null) {
			newRate.setRate(lastRate.getRate());
			newRate.setFee(lastRate.getFee());
			newRate.setFixFee(lastRate.getFixFee());
		}
		return newRate;
		
	}
}
