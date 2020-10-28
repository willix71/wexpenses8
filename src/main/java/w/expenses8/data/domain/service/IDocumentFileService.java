package w.expenses8.data.domain.service;

import java.time.LocalDate;

import w.expenses8.data.core.model.DBable;
import w.expenses8.data.core.service.IGenericService;
import w.expenses8.data.domain.model.DocumentFile;

public interface IDocumentFileService  extends IGenericService<DocumentFile, Long> {

	DocumentFile findByFileName(String fileName);
	
	String generateFilename(LocalDate fileDate, DBable<?> x);
	
	String getUrl(DocumentFile docFile);

}
