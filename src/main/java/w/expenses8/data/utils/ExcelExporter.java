package w.expenses8.data.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;

import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.model.TransactionEntry;
import w.expenses8.data.domain.model.enums.TransactionFactor;

public class ExcelExporter {

//	private static final int MAX_CHARACTERS_PER_CELL = SpreadsheetVersion.EXCEL97.getMaxTextLength(); // 32767;
//
//	private static void setCellValue(Cell cell, String s) {
//		if (s != null && s.length() > MAX_CHARACTERS_PER_CELL) {
//			s = s.substring(0, MAX_CHARACTERS_PER_CELL - 5) + "[...]";
//		}
//		cell.setCellValue(s);
//	}
	
	public static void exportExcelExpenses(List<Expense> expenses, OutputStream outstream) throws IOException {
		new ExpenseExporter().prepare().populate(expenses).export(outstream);
	}
	
	public static void exportExcelTransactionEntries(List<TransactionEntry> entries, OutputStream outstream) throws IOException {
		new TransactionEntryExporter().prepare().populate(entries).export(outstream);
	}
}

class AbstractExporter {
	HSSFWorkbook workbook;
	HSSFSheet sheet;
	CreationHelper helper;
	HSSFCellStyle headerStyle;
	HSSFCellStyle typeStyle;
	HSSFCellStyle dateStyle;
	HSSFCellStyle amountStyle;
	
	int rowNumber = 0;
	int columnNumber;
	Row row;
	
	AbstractExporter() {
		workbook = new HSSFWorkbook();
		sheet = workbook.createSheet("Expenses");
		helper = workbook.getCreationHelper();
		
		headerStyle = workbook.createCellStyle();
		HSSFFont boldFont = workbook.createFont();
	    boldFont.setBold(true);
	    headerStyle.setFont(boldFont);  
	      
	    typeStyle = workbook.createCellStyle();
	    typeStyle.setAlignment(HorizontalAlignment.CENTER);
	    
		short dateFormat = helper.createDataFormat().getFormat("DD.MM.yyyy");
		dateStyle = workbook.createCellStyle();
		dateStyle.setDataFormat(dateFormat);
		
		short amountFormat = helper.createDataFormat().getFormat("0.00");
		amountStyle = workbook.createCellStyle();
		amountStyle.setDataFormat(amountFormat);
	}

	protected void addHeader(String name) {
		Cell cell = row.createCell(++columnNumber);
		cell.setCellValue(name);
		cell.setCellStyle(headerStyle);
	}
	
	protected void addHeader(String name, int width) {
		addHeader(name);
		sheet.setColumnWidth(columnNumber, width);
	}
	
	void export(OutputStream outstream) throws IOException {
		workbook.write(outstream);
		workbook.close();
	}
}

class ExpenseExporter extends AbstractExporter {
	
	ExpenseExporter prepare() {
		columnNumber = -1; // reset
		row = sheet.createRow(rowNumber); // first row
		
		addHeader("Id");
		addHeader("Date", 256*11);
		addHeader("Type");
		addHeader("", 256*9);
		addHeader("", 256*6);
		addHeader("CHF", 256*10);
		addHeader("Payee", 256*35);
		addHeader("All Tags", 256*50);
		addHeader("IN Tags", 256*50);
		addHeader("OUT Tags", 256*50);
		addHeader("Description");
		return this;
	}
	
	ExpenseExporter populate(List<Expense> expenses) {
		for (Expense expense: expenses) {
			columnNumber = -1; // reset
			row = sheet.createRow(++rowNumber); // new row
			
			Cell cell = row.createCell(++columnNumber);
			cell.setCellValue(expense.getId());
			
			cell = row.createCell(++columnNumber);
			cell.setCellValue(Date.from(expense.getDate().atZone(ZoneId.systemDefault()).toInstant()));
			cell.setCellStyle(dateStyle);
			
			cell = row.createCell(++columnNumber);
			cell.setCellStyle(typeStyle);
			if (expense.getExpenseType()!=null) {
				cell.setCellValue(expense.getExpenseType().getName());
			}
			
			cell = row.createCell(++columnNumber);
			if (!"CHF".equals(expense.getCurrencyCode())) {
				cell.setCellValue(expense.getCurrencyAmount().doubleValue());
				cell.setCellStyle(amountStyle);
			}
			
			cell = row.createCell(++columnNumber);
			if (!"CHF".equals(expense.getCurrencyCode())) {
				cell.setCellValue(expense.getCurrencyCode());
			}
			
			cell = row.createCell(++columnNumber);
			cell.setCellValue(expense.getAccountingValue().doubleValue());
			cell.setCellStyle(amountStyle);
			
			cell = row.createCell(++columnNumber);
			Payee p=expense.getPayee();
			String pstr=p.getName();
			if (p.getPrefix()!=null) pstr=p.getPrefix()+pstr;
			if (p.getExtra()!=null) pstr+=p.getExtra();
			if (p.getCity()!=null) pstr+="," +p.getCity();
			cell.setCellValue(pstr);			
			
			cell = row.createCell(++columnNumber);
			cell.setCellValue(expense.getTags().stream().map(t->t.getName()).collect(Collectors.joining("; ")));
			
			cell = row.createCell(++columnNumber);
			cell.setCellValue(expense.getTransactions().stream().filter(t->t.getFactor()==TransactionFactor.IN).flatMap(t->t.getTags().stream()).map(t->t.getName()).collect(Collectors.joining("; ")));
		
			cell = row.createCell(++columnNumber);
			cell.setCellValue(expense.getTransactions().stream().filter(t->t.getFactor()==TransactionFactor.OUT).flatMap(t->t.getTags().stream()).map(t->t.getName()).collect(Collectors.joining("; ")));
			
			cell = row.createCell(++columnNumber);
			cell.setCellValue(expense.getDescription());
		}
		
		return this;
	}
}


class TransactionEntryExporter extends AbstractExporter {
	Row topTotal;
	
	TransactionEntryExporter prepare() {
		columnNumber = -1; // reset
		row = sheet.createRow(rowNumber); // first row
		
		addHeader("Id");
		addHeader("Date", 256*11);
		
		addHeader("Payee", 256*35);
		addHeader("Amount", 256*10);
		addHeader("Currency");
		addHeader("In", 256*10);
		addHeader("Out", 256*10);
		addHeader("Balance", 256*10);
		addHeader("Tags", 256*50);
		addHeader("Counter Tags", 256*50);
		addHeader("Description");

		topTotal = sheet.createRow(++rowNumber); // total row (1)
		
		return this;
	}
	
	TransactionEntryExporter populate(List<TransactionEntry> entries) {
		double balance=0.0d; 
		double sumIN=0.0d;
		double sumOUT=0.0d;
		for (TransactionEntry entry: entries) {
			Expense ex = entry.getExpense();
			
			columnNumber = -1; // reset
			row = sheet.createRow(++rowNumber); // new row
			
			Cell cell = row.createCell(++columnNumber);
			cell.setCellValue(entry.getId());
			
			cell = row.createCell(++columnNumber);
			cell.setCellValue(Date.from(entry.getAccountingDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
			cell.setCellStyle(dateStyle);
			
			cell = row.createCell(++columnNumber);
			Payee p=ex.getPayee();
			String pstr=p.getName();
			if (p.getPrefix()!=null) pstr=p.getPrefix()+pstr;
			if (p.getExtra()!=null) pstr+=p.getExtra();
			if (p.getCity()!=null) pstr+="," +p.getCity();

			cell.setCellValue(pstr);			
			cell = row.createCell(++columnNumber);
			cell.setCellStyle(amountStyle);
			cell.setCellValue(entry.getCurrencyAmount().doubleValue());
			
			cell = row.createCell(++columnNumber);
			cell.setCellValue(ex.getCurrencyCode());
			
			cell = row.createCell(++columnNumber);
			cell.setCellStyle(amountStyle);
			if (entry.getFactor()==TransactionFactor.IN) {
				double v = entry.getAccountingValue().doubleValue();
				sumIN += v;
				balance += v;
				cell.setCellValue(v);
			}
			
			cell = row.createCell(++columnNumber);
			cell.setCellStyle(amountStyle);
			if (entry.getFactor()==TransactionFactor.OUT) {
				double v = entry.getAccountingValue().doubleValue();
				sumOUT += v;
				balance -= v;
				cell.setCellValue(v);
			}
			
			cell = row.createCell(++columnNumber);
			cell.setCellStyle(amountStyle);
			cell.setCellValue(balance);
			
			cell = row.createCell(++columnNumber);
			cell.setCellValue(entry.getTags().stream().map(t->t.getName()).collect(Collectors.joining("; ")));

			cell = row.createCell(++columnNumber);
			cell.setCellValue(ex.getTransactions().stream().filter(t->t!=entry).flatMap(t->t.getTags().stream()).map(t->t.getName()).collect(Collectors.joining("; ")));

			cell = row.createCell(++columnNumber);
			cell.setCellValue(ex.getDescription());
		}
		
		setTotal(topTotal,sumIN,sumOUT,balance);
		setTotal(sheet.createRow(++rowNumber),sumIN,sumOUT,balance);

		return this;
	}
	
	private void setTotal(Row row, double sumIN, double sumOUT,double balance) {
		columnNumber = 2; // set

		Cell cell = row.createCell(columnNumber);
		cell.setCellStyle(headerStyle);
		cell.setCellValue("Total");
		
		columnNumber = 5; // set
		
		cell = row.createCell(columnNumber);
		cell.setCellStyle(amountStyle);
		cell.setCellValue(sumIN);
		
		cell = row.createCell(++columnNumber);
		cell.setCellStyle(amountStyle);
		cell.setCellValue(sumOUT);
		
		cell = row.createCell(++columnNumber);
		cell.setCellStyle(amountStyle);
		cell.setCellValue(balance);
		
	}
}
