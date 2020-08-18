package w.expenses8.data.model.core;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@SuperBuilder(builderMethodName = "with")
@Accessors(chain = true) @Getter @Setter  @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@MappedSuperclass
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class DBable<T extends DBable<T>> implements Serializable, Cloneable {

	private static final long serialVersionUID = 2482940442245899869L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	private Long version;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedTs;

	@NonNull
	@Builder.Default
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="createdTS", nullable = false, updatable = false)
	private Date createdTs = new Date();

	@NonNull
	@Builder.Default
	@EqualsAndHashCode.Include
	@Column(name = "uid", unique = true, nullable = false, updatable = false)
	private String uid = UUID.randomUUID().toString();

	@PrePersist
	@PreUpdate
	public void preupdate() {
		modifiedTs = new Date();
	}
	
	public boolean isNew() {
		return id == null;
	}
	
	public String getFullId() {
		return (id == null ? 0 : id) + "." + (version == null ? 0 : version);
	}

	@Override
	public String toString() {
		return MessageFormat.format("{0}[{1} {2}]", this.getClass().getSimpleName(), getFullId(), uid);
	}

	public void copy(T t) {
		this.id = ((DBable<T>) t).id;
		this.version = ((DBable<T>) t).version;
		this.createdTs = ((DBable<T>) t).createdTs;
		this.modifiedTs = ((DBable<T>) t).modifiedTs;
		this.uid = ((DBable<T>) t).uid;
	}
	
	@SneakyThrows(CloneNotSupportedException.class)
	public T klone() {
		@SuppressWarnings("unchecked")
		T t = (T) clone();
		return t;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}