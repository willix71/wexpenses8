package w.expenses8.data.domain.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import w.expenses8.data.core.model.AbstractType;
import w.expenses8.data.domain.criteria.TagCriteria;

@SuperBuilder(builderMethodName = "with")
@Getter @Setter  @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "WEX_TagGroup")
public class TagGroup extends AbstractType<TagGroup> implements TagCriteria {

	private static final long serialVersionUID = 1L;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "WEX_TagGroup_WEX_Tag", joinColumns = @JoinColumn(name = "WEX_TagGroup_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
	@OrderBy("number,name")
	private Set<Tag> tags;
	
	public TagGroup addTag(Tag tag) {
		if (tags == null) {
			tags = new HashSet<Tag>();
		}
		tags.add(tag);
		return this;
	}

	public TagGroup removeTag(Tag tag) {
		if (tags != null) tags.remove(tag);
		return this;
	}
	
	// we need a list for the <p:autoComplete>
	public List<Tag> getTagsList() {
		return tags==null?null:new LinkedList<Tag>(tags);
	}
	
	public void setTagsList(List<Tag> tagsList) {
		if (tagsList == null) {
			tags = null;
		} else if (tags==null) {
			tags = new HashSet<Tag>(tagsList);
		} else {
			tags.addAll(tagsList);
			tags.retainAll(tagsList);
		}
	}
	
	@Override
	public Object getCriteriaType() {
		return "GROUP";
	}

	@Override
	public void copy(TagGroup t) {
		super.copy(t);
		this.tags = new HashSet<Tag>(t.tags);
	}
}