package w.expenses8.data.core.criteria;

import java.math.BigDecimal;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RangeNumberCriteria extends RangeCriteria<BigDecimal> {
	private static final long serialVersionUID = 1L;

	public RangeNumberCriteria(BigDecimal from, BigDecimal to) {
		super(from, to);
	}
}
