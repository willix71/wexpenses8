package w.expenses8.data.domain.criteria;

public interface TagCriteria {

	TagCriteria NOT = new TagCriteria() {
		@Override public String getName() {return "NOT";}
		
		@Override public Object getCriteriaType() {return "NOT";}
	};
	
	Object getCriteriaType();
	
	String getName();	
}
