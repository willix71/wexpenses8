package w.expensesLegacy.data.domain.model;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class DBable<T extends DBable<T>> implements Serializable, Duplicatable<T> {

	private static final long serialVersionUID = 2482940442245899869L;

	public static final List<String> SYSTEM_PROPERTIES = Arrays.asList("id", "version", "fullId", "uid", "modifiedTs", "createdTs");
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Version
	private Long version;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedTs;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="createdTS", nullable = false, updatable = false)
	private Date createdTs;

	@Column(name = "uid", unique = true, nullable = false, updatable = false)
	private String uid;

	public static String newUid() {
		return UUID.randomUUID().toString();
	}

	public DBable() {
		uid = newUid();
		createdTs = new Date();
	}

	@PrePersist
	@PreUpdate
	public void preupdate() {
		modifiedTs = new Date();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getFullId() {
		return (id == null ? 0 : id) + "." + (version == null ? 0 : version);
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Date getCreatedTs() {
		return createdTs;
	}

	public void setCreatedTs(Date createdTs) {
		this.createdTs = createdTs;
	}

	public Date getModifiedTs() {
		return modifiedTs;
	}

	public void setModifiedTs(Date modifiedTs) {
		this.modifiedTs = modifiedTs;
	}

	public boolean isNew() {
		return id == null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		@SuppressWarnings("rawtypes")
		DBable other = (DBable) obj;
		if (uid == null) {
			if (other.uid != null)
				return false;
		}
		return (uid.equals(other.uid));
	}

	@Override
	public String toString() {
		return MessageFormat.format("{0}{{1}}", this.getClass().getSimpleName(), uid);
	}

	@Override
	public T duplicate() {
		T t = cloneInternal();
		t.setId(null);
		t.setModifiedTs(null);
		t.setCreatedTs(new Date());
		t.setUid(newUid());
		return t;
	}

	@Override
	public T klone() {
		return cloneInternal();
	}

	@SuppressWarnings("unchecked")
	protected T cloneInternal() {
		try {
			return (T) clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Can not clone " + this.getClass().getName(), e);
		}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
