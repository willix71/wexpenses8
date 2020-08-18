package w.expenses8.data.criteria.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true) @Getter @Setter  @NoArgsConstructor @AllArgsConstructor
public class RangeCriteria<T> {

	private T from;
	private T to;
	
}
