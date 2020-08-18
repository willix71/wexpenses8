package w.expenses8.data.model.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import w.expenses8.data.model.core.AbstractType;
import w.expenses8.data.model.enums.TagEnum;

@SuperBuilder(builderMethodName = "with")
@Accessors(chain = true) @Getter @Setter  @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "Tag2")
public class Tag extends AbstractType<Tag> {

	private static final long serialVersionUID = 1L;

	@NonNull
	@javax.validation.constraints.NotNull
	private TagEnum type;

	private Integer number;

	@Override
	public void copy(Tag t) {
		super.copy(t);
		this.number = t.number;
		this.type = t.type;
	}
}
