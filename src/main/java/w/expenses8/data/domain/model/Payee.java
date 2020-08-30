package w.expenses8.data.domain.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import w.expenses8.data.core.model.DBable;
import w.expenses8.data.domain.validation.Ibanized;

@SuperBuilder(builderMethodName = "with")
@Getter @Setter  @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "WEX_Payee")
public class Payee extends DBable<Payee> {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade={MERGE, REFRESH, DETACH})
	private PayeeType payeeType;
	
	@NonNull
	@NotBlank
	private String name;
	private String prefix;
	private String extra;
	private String address1;
	private String address2;
	private String address3;
	private String zip;
	private String city;
	private String countryCode;
	
    @Pattern(regexp="|\\d{1,2}-\\d{1,6}-\\d")
    private String postalAccount;
    private String postalBank;
    
    @Ibanized
    private String iban;

	@Override
	public void copy(Payee t) {
		super.copy(t);
		this.payeeType = t.payeeType;
		this.name = t.name;
		this.prefix = t.prefix;
		this.extra = t.extra;
		this.address1 = t.address1;
		this.address2 = t.address2;
		this.address3 = t.address3;
		this.zip = t.zip;
		this.countryCode = t.countryCode;
	}
}
