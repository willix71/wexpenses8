package w.expenses8.data.domain.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import w.expenses8.data.core.model.DBable;

@SuperBuilder(builderMethodName = "with")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "WEX_Document")
public class DocumentFile extends DBable<DocumentFile> {

	private static final long serialVersionUID = 1L;
	
	@NotNull(message = "DocumentFile's date is mandatory")
	private LocalDate documentDate;
	
	@NotBlank(message =  "DocumentFile's fileName can't be blank")
	@Column(name = "fileName", unique = true, nullable = false)
	private String fileName;
	
	@Override
	public String toString() {
		return super.toString() + " " + fileName;
	}
}