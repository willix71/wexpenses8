package w.expensesLegacy.data.domain.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;

@Entity
public class Account extends AbstractType<Account> implements Parentable<Account> {

	private static final long serialVersionUID = 2482940442245899869L;

	@ManyToOne(fetch = FetchType.LAZY)
	private Account parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	@OrderBy("number")
	private List<Account> children;

	private String number;

	private String fullName;

	private String fullNumber;

	@NotNull
	@Enumerated(EnumType.STRING)
	private AccountEnum type = AccountEnum.FILTER;

	@ManyToOne
	private Currency currency;

	private String display;

	// Iban, card number
	private String externalReference;

	@ManyToOne(fetch = FetchType.EAGER)
	private Payee owner;

	public Account() {
		super();
	}

	public Account(Account parent, int number, String name, AccountEnum type, Currency currency) {
		super(name, currency != null);
		this.number = String.valueOf(number);
		this.parent = parent;
		updateFullNameAndNumber();
		this.type = type;
		this.currency = currency;
	}

	public void updateFullNameAndNumber() {
		String number = getNumber() != null ? getNumber() : "-";

		if (getParent() == null) {
			this.fullName = getName();
			this.fullNumber = number;
		} else {
			this.fullName = getParent().getFullName() + ":" + getName();
			this.fullNumber = getParent().getFullNumber() + ":" + number;
		}
	}

	@PreUpdate
	public void preupdate() {
		display = fullNumber + " " + fullName;
	}

	public String getDisplay() {
		return display;
	}

	@Override
	public Account getParent() {
		return parent;
	}

	@Override
	public void setParent(Account parent) {
		this.parent = parent;
	}

	@Override
	public List<Account> getChildren() {
		return children;
	}

	public void setChildren(List<Account> children) {
		this.children = children;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFullNumber() {
		return fullNumber;
	}

	public void setFullNumber(String fullNumber) {
		this.fullNumber = fullNumber;
	}

	public AccountEnum getType() {
		return type;
	}

	public void setType(AccountEnum type) {
		this.type = type;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public String getExternalReference() {
		return externalReference;
	}

	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	public Payee getOwner() {
		return owner;
	}

	public void setOwner(Payee bankDetails) {
		this.owner = bankDetails;
	}

	@Override
	public String toString() {
		return fullName;
	}

	@Override
	public Account duplicate() {
		Account klone = super.duplicate();
		klone.setChildren(new ArrayList<Account>());
		return klone;
	}

	@Override
	public Account klone() {
		Account klone = super.klone();
		if (klone.getChildren() != null) {
			klone.setChildren(new ArrayList<Account>(klone.getChildren()));
		}
		return klone;
	}

}
