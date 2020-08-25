package w.expensesLegacy.data.domain.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY, region="codable")
public class Currency implements Codable<Currency> {

	private static final long serialVersionUID = 2482940442245899869L;

	@Id
	@Size(min = 3, max = 3)
	private String code;

	@NotEmpty
	private String name;

	private Integer roundingFactor;
	
	private Integer strengh;
	
	public Currency() {
		super();
	}
		
	public Currency(String code, String name, Integer roundingFactor) {
		super();
		this.code = code;
		this.name = name;
		this.roundingFactor = roundingFactor;
	}

	public Currency(String code, String name, Integer roundingFactor, Integer strengh) {
		this(code,name,roundingFactor);
		this.strengh = strengh;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getRoundingFactor() {
		return roundingFactor;
	}

	public void setRoundingFactor(Integer roundingFactor) {
		this.roundingFactor = roundingFactor;
	}

	public Integer getStrengh() {
		return strengh;
	}

	public void setStrengh(Integer strengh) {
		this.strengh = strengh;
	}

	@Override
	public int hashCode() {
		return code==null?0:code.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		Currency other = (Currency) obj;
		if (code == null) {
			if (other.code != null) return false;
		} 			
		return (code.equals(other.code));
	}	

	@Override
	public String toString() {
		return code;
	}
	
   @Override
   public Currency klone() {
	   try {
	   	return (Currency) clone();
	   }
	   catch(CloneNotSupportedException e) {
	   	throw new RuntimeException("Can not clone " + this.getClass().getName(), e);
	   }
   }
	
	@Override
   protected Object clone() throws CloneNotSupportedException {
	   return super.clone();
   }
}
