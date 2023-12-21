package w.expenses8.data.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import w.expenses8.data.core.model.DBable;
import w.expenses8.data.domain.validation.Warning;

@SuperBuilder(builderMethodName = "with")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "WEX_Consolidation")
public class Consolidation extends DBable<Consolidation> {

	private static final long serialVersionUID = 1L;
	
	@NotNull(message = "Consolidation's date is mandatory")
	private LocalDate date;
	
	@Builder.Default
	private boolean monthly = true;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Payee institution;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private DocumentFile documentFile;
	
	private BigDecimal openingValue;

	@ManyToOne(fetch = FetchType.LAZY)
	private TransactionEntry openingEntry;
	
	private BigDecimal closingValue;

	@ManyToOne(fetch = FetchType.LAZY)
	private TransactionEntry closingEntry;
	
	@Range(min = 0, max = 0, message = "Delta balance should be 0", groups = Warning.class)
	private BigDecimal deltaValue;
}