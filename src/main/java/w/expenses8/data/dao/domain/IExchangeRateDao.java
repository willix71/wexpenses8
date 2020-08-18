package w.expenses8.data.dao.domain;

import w.expenses8.data.dao.core.IGenericDao;
import w.expenses8.data.dao.core.IUidableDao;
import w.expenses8.data.model.domain.ExchangeRate;

public interface IExchangeRateDao extends IGenericDao<ExchangeRate, Long>, IUidableDao<ExchangeRate> {

}
