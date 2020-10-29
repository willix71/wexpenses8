package w.expenses8.data.domain.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;

import java.math.BigDecimal;
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
@Table(name = "WEX_Consolidation")
public class Consolidation extends DBable<Consolidation> {

	private static final long serialVersionUID = 1L;
	
	@NotNull
	private LocalDate date;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Payee institution;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade={PERSIST, MERGE, REFRESH, DETACH})
	private DocumentFile documentFile;
	
	private BigDecimal openingValue;

	@ManyToOne(fetch = FetchType.LAZY)
	private TransactionEntry openingEntry;
	
	private BigDecimal closingValue;

	@ManyToOne(fetch = FetchType.LAZY)
	private TransactionEntry closingEntry;
	
	private BigDecimal deltaValue;
}