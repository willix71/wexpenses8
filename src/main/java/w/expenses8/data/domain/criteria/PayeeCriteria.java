package w.expenses8.data.domain.criteria;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@SuperBuilder(builderMethodName = "with")
@Accessors(chain = true) @Getter @Setter  @NoArgsConstructor @AllArgsConstructor
public class PayeeCriteria {

	private String text;
}
