package w.expenses8.data.domain.model.enums;


import com.google.common.base.Joiner;

import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.utils.StringHelper;

public enum PayeeDisplayer {

	DEFAULT, 
	
	CCP {
		@Override
		protected String displayIt(Payee p) {
			if(p==null) return null;
			String display = super.displayIt(p);
			display = append(display,"; Ccp: ", p.getPostalAccount());
			return display;
		}
	}, 
	IBAN {
		@Override
		protected String displayIt(Payee p) {
			if(p==null) return null;
			String display = super.displayIt(p);
			display = append(display,"; Iban: ", p.getIban());
			return display;
		}
	};

	public String display(Payee p) {
		if(p==null) return null;
		return displayIt(p);
	}
	
	protected String displayIt(Payee p) {
		String display = p.getPrefix()==null?p.getName():p.getPrefix()+p.getName();
		display = append(display," ", p.getExtra());
		display = append(display, ", ", Joiner.on(" ").skipNulls().join(p.getCity(),p.getCountryCode()));
		return display;
	}
	
	private static String append(String value, String prefix, String additional) {
		return StringHelper.isEmpty(additional)?value:value+prefix+additional;
	}
	
}
