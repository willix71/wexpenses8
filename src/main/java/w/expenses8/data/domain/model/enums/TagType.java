package w.expenses8.data.domain.model.enums;

import w.expenses8.data.domain.criteria.TagCriteria;

public enum TagType implements TagCriteria {

	SYSTEM, ASSET, LIABILITY, INCOME, EXPENSE, DISCRIMINATOR, CONSOLIDATION, FLAG, GROUPING;

	@Override
	public String getName() {
		return name();
	}
	
	@Override
	public Object getCriteriaType() {
		return "TYPE";
	}
}
