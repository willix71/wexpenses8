package w.expenses8.data.domain.model;

import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REFRESH;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
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
	
	@ManyToOne(fetch = FetchType.LAZY)
	private DocumentFile documentFile;
	
	private BigDecimal openingValue;

	@ManyToOne(fetch = FetchType.LAZY)
	private TransactionEntry openingEntry;
	
	private BigDecimal closingValue;

	@ManyToOne(fetch = FetchType.LAZY)
	private TransactionEntry closingEntry;
	
	private BigDecimal deltaValue;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "consolidation", cascade={MERGE, REFRESH, DETACH})
	@OrderBy("accountingOrder")
	private Set<TransactionEntry> transactions;
	
	public Consolidation addTransaction(TransactionEntry transaction) {
		if (transactions == null) {
			transactions = new HashSet<TransactionEntry>();
		}
		transactions.add(transaction);
		transaction.setConsolidation(this);
		return this;
	}

	public Consolidation removeTransaction(TransactionEntry transaction) {
		if (transactions != null) transactions.remove(transaction);
		transaction.setConsolidation(null);
		return this;
	}
}