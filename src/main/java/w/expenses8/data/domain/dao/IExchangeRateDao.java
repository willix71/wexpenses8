package w.expenses8.data.domain.dao;

import java.time.LocalDate;

import w.expenses8.data.core.dao.IGenericDao;
import w.expenses8.data.core.dao.IUidableDao;
import w.expenses8.data.domain.model.ExchangeRate;
import w.expenses8.data.domain.model.Payee;

public interface IExchangeRateDao extends IGenericDao<ExchangeRate, Long>, IUidableDao<ExchangeRate> {

	ExchangeRate findFirstByDateLessThanEqualAndInstitutionAndFromCurrencyCodeAndToCurrencyCodeOrderByDateDesc(LocalDate date, Payee institution, String fromCode, String toCode);
}
