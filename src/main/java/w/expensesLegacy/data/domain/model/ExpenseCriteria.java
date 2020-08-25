package w.expensesLegacy.data.domain.model;

import java.math.BigDecimal;
import java.util.Date;

public class ExpenseCriteria {
	private Date fromDate;
	private Date toDate;
	private BigDecimal fromAmount;
	private BigDecimal toAmount;
	private Currency currency;
	private ExpenseType expenseType;
	private String payeeText;
	
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public BigDecimal getFromAmount() {
		return fromAmount;
	}
	public void setFromAmount(BigDecimal fromAmount) {
		this.fromAmount = fromAmount;
	}
	public BigDecimal getToAmount() {
		return toAmount;
	}
	public void setToAmount(BigDecimal toAmount) {
		this.toAmount = toAmount;
	}
	public Currency getCurrency() {
		return currency;
	}
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	public ExpenseType getExpenseType() {
		return expenseType;
	}
	public void setExpenseType(ExpenseType expenseType) {
		this.expenseType = expenseType;
	}
	public String getPayeeText() {
		return payeeText;
	}
	public void setPayeeText(String payeeText) {
		this.payeeText = payeeText;
	}
	
}
