package w.expensesLegacy.data.domain.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Entity
public class Discriminator extends AbstractType<Discriminator> implements Parentable<Discriminator> {

	private static final long serialVersionUID = -5425160349628676177L;

	@ManyToOne(fetch = FetchType.LAZY)
	private Discriminator parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	@OrderBy("name")
	private List<Discriminator> children;

	public Discriminator() {
	}

	public Discriminator(String name) {
		super(name);
	}

	public Discriminator(String name, boolean selectable) {
		super(name, selectable);
	}

	public Discriminator getParent() {
		return parent;
	}

	public void setParent(Discriminator parent) {
		this.parent = parent;
	}

	public List<Discriminator> getChildren() {
		return children;
	}

	public void setChildren(List<Discriminator> children) {
		this.children = children;
	}
	
	@Override
   public Discriminator duplicate() {
		Discriminator klone = super.duplicate();
		klone.setChildren(new ArrayList<Discriminator>());
		return klone;
   }

	@Override
   public Discriminator klone() {
		Discriminator klone = super.klone();
		if (klone.getChildren()!=null) {
			klone.setChildren(new ArrayList<Discriminator>(klone.getChildren()));
		}
		return klone;
   }
}
