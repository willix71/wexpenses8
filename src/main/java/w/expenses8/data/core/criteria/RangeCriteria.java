package w.expenses8.data.core.criteria;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RangeCriteria<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private T from;
	private T to;
	
	public void clear() {
		from=null;
		to=null;
	}
}
