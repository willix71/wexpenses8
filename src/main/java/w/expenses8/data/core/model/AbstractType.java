package w.expenses8.data.core.model;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@SuperBuilder(builderMethodName = "with")
@Accessors(chain = true) @Getter @Setter  @NoArgsConstructor @AllArgsConstructor
@MappedSuperclass
public abstract class AbstractType<T extends AbstractType<T>> extends DBable<T> {
	
	private static final long serialVersionUID = 1L;

	@NonNull
	@NotBlank
	private String name;

	private String description;
	
	private boolean selectable = true;

	@Override
	public void copy(T t) {
		super.copy(t);
		AbstractType<?> a = (AbstractType<?>) t;
		this.name = a.name;
		this.description = a.description;
		this.selectable = a.selectable;
	}

	@Override
	public String toString() {
		return super.toString() + " " + name;
	}
}
