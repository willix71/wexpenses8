package w.expenses8.data.domain.criteria;

public interface TagCriteria {

	TagCriteria NOT = new TagCriteria() {
		@Override public String getName() {return "NOT";}
		
		@Override public Object getCriteriaType() {return "NOT";}
		
		@Override public String toString() {return "NOT";}
	};
	
	TagCriteria IN = new TagCriteria() {
		@Override public String getName() {return "IN";}
		
		@Override public Object getCriteriaType() {return "IN";}
		
		@Override public String toString() {return "IN";}
	};
	
	TagCriteria OUT = new TagCriteria() {
		@Override public String getName() {return "OUT";}
		
		@Override public Object getCriteriaType() {return "OUT";}
		
		@Override public String toString() {return "OUT";}
	};
	
	TagCriteria AND = new TagCriteria() {
		@Override public String getName() {return "AND";}
		
		@Override public Object getCriteriaType() {return "AND";}
		
		@Override public String toString() {return "AND";}
	};
	
	TagCriteria OR = new TagCriteria() {
		@Override public String getName() {return "OR";}
		
		@Override public Object getCriteriaType() {return "OR";}
		
		@Override public String toString() {return "OR";}
	};
	
	Object getCriteriaType();
	
	String getName();	
}
