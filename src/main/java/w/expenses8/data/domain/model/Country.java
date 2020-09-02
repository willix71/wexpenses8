package w.expenses8.data.domain.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "WEX_Country")
public class Country {

	@Id
	@Size(min=2,max=2)
	private String countryCode;
	
	private String countryName;
	
	@NotBlank
	@Size(min=3,max=3)
	private String currencyCode;
}
