package w.expensesLegacy.data.domain.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY, region="codable")
public class Country implements Codable<Country>  {

	private static final long serialVersionUID = 2482940442245899869L;

	@Id
	@Size(min = 2, max = 2)
	private String code;

	@NotEmpty
	private String name;

	@NotNull
	@ManyToOne
	private Currency currency;

	public Country() {
		super();
	}

	public Country(String code, String name, Currency currency) {
		super();
		this.code = code;
		this.name = name;
		this.currency = currency;
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

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	@Override
	public int hashCode() {
		return code.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Country) {
			return code.equals(((Country) obj).code);
		}
		return false;
	}

	@Override
	public String toString() {
		return code;
	}
	
   @Override
   public Country klone() {
	   try {
	   	return (Country) clone();
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
