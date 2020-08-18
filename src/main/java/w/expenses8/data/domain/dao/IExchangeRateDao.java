package w.expenses8.data.domain.dao;

import w.expenses8.data.core.dao.IGenericDao;
import w.expenses8.data.core.dao.IUidableDao;
import w.expenses8.data.domain.model.ExchangeRate;

public interface IExchangeRateDao extends IGenericDao<ExchangeRate, Long>, IUidableDao<ExchangeRate> {

}
