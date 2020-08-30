package w.expenses8.data.domain.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import w.expenses8.data.core.model.AbstractType;
import w.expenses8.data.domain.model.enums.TagType;

@SuperBuilder(builderMethodName = "with")
@Getter @Setter  @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "WEX_Tag")
public class Tag extends AbstractType<Tag> {

	private static final long serialVersionUID = 1L;

	@NonNull
	@javax.validation.constraints.NotNull
	private TagType type;

	private Integer number;

	@Override
	public void copy(Tag t) {
		super.copy(t);
		this.number = t.number;
		this.type = t.type;
	}
}
