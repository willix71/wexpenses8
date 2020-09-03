package w.expenses8.data.domain.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Expense expense;
	
	@NotNull
	private LocalDate documentDate;
	
	@NotNull
	private String fileName;
	
	public DocumentFile(@NotNull LocalDate documentDate, @NotNull String fileName) {
		super();
		this.documentDate = documentDate;
		this.fileName = fileName;
	}
	
	@Override
	public void copy(DocumentFile t) {
		super.copy(t);
		this.expense = t.expense;
		this.documentDate = t.documentDate;
		this.fileName = t.fileName;
	}
	
	@Override
	public String toString() {
		return super.toString() + " " + fileName;
	}
}