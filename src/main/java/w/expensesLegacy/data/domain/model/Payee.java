package w.expensesLegacy.data.domain.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

import com.google.common.base.Joiner;

@Entity(name = "PayeeOld")
@Table(name = "Payee")
public class Payee extends DBable<Payee> {

	private static final long serialVersionUID = 2482940442245899869L;
	
    @ManyToOne(fetch = FetchType.EAGER)
    private PayeeType type;

    private String prefix;
    
    @NotEmpty
    private String name;

    private String address1;

    private String address2;
    
    @ManyToOne(fetch = FetchType.EAGER)
    private City city;

    @Pattern(regexp="|\\d{1,2}-\\d{1,6}-\\d")
    private String postalAccount;
    
    private String iban;
    
    @ManyToOne(fetch = FetchType.EAGER)
    private Payee bankDetails;
   
    @Transient
    private String externalReference;
 
    private String display;
    
    @OneToMany(mappedBy = "owner",fetch = FetchType.LAZY)
	private Set<Account> accounts;
	
    @PrePersist
    @PreUpdate
    public void preupdate() {
    	externalReference = buildExternalReference();
    	display = toString() + "; " + externalReference;    
    }

	public String getDisplay() {
		return display;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	
	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	
	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public PayeeType getType() {
		return type;
	}

	public void setType(PayeeType type) {
		this.type = type;
	}

	public String getPrefixedName() {
		if (prefix == null) {
			return name;
		} else {
			return prefix + name;
		}
	}
	
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String typeOf) {
		this.prefix = typeOf;
	}

	public String getExternalReference() {
		if (this.externalReference == null) {
			this.externalReference = buildExternalReference();
		}
		return externalReference;
	}
	
	public String buildExternalReference() {
		StringBuilder sb = new StringBuilder();
		if (iban != null) {
			sb.append("IBAN:").append(iban);		
		}
		String cp = postalAccount!=null?postalAccount:bankDetails!=null?bankDetails.getPostalAccount():null;
		if (cp != null) {
			if (sb.length()==0) {
				sb.append("CP:");
			} else {
				sb.append( " | CP:");
			}
			sb.append(cp);
		}
		if (bankDetails != null) {
			if (sb.length()==0) {
				sb.append("Bank: ");
			} else {
				sb.append( " | Bank: ");
			}
			sb.append(bankDetails.toString());
		}
		return sb.toString();
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getPostalAccount() {
		return postalAccount;
	}

	public void setPostalAccount(String postalAccount) {
		this.postalAccount = postalAccount;
	}
	
	public Payee getBankDetails() {
		return bankDetails;
	}

	public void setBankDetails(Payee bankDetails) {
		this.bankDetails = bankDetails;
	}
	
	public Account getFirstAccount() {
		return accounts==null||accounts.isEmpty()?null:accounts.stream().findFirst().orElse(null);
	}
	
	public Set<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(Set<Account> accounts) {
		this.accounts = accounts;
	}

	public String toShortString() {
		String s = getPrefixedName();
		if (city != null) s += ", " + city.toString();
		return s;
	}
	@Override
	public String toString() {
		String s = toShortString();
		if (postalAccount!=null || iban!=null) s+= " [" + Joiner.on(" | ").skipNulls().join(postalAccount,iban) + "]";
		return s;
	}
}
