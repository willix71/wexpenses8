package w.expenses8.data.domain.service;

import java.time.LocalDate;

import w.expenses8.data.core.service.IGenericService;
import w.expenses8.data.domain.model.DocumentFile;
import w.expenses8.data.domain.model.Expense;

public interface IDocumentFileService  extends IGenericService<DocumentFile, Long> {

	String generateFilename(LocalDate fileDate, Expense x);
	
	String getUrl(DocumentFile docFile);

}
