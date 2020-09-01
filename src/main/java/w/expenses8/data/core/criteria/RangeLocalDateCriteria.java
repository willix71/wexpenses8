package w.expenses8.data.core.criteria;

import java.time.LocalDate;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RangeLocalDateCriteria extends RangeCriteria<LocalDate> {
	private static final long serialVersionUID = 1L;

	public RangeLocalDateCriteria(LocalDate from, LocalDate to) {
		super(from, to);
	}
}
