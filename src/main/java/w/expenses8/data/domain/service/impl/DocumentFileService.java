package w.expenses8.data.domain.service.impl;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import w.expenses8.data.core.service.GenericServiceImpl;
import w.expenses8.data.domain.dao.IDocumentFileDao;
import w.expenses8.data.domain.model.DocumentFile;
import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.service.IDocumentFileService;

@Service
public class DocumentFileService extends GenericServiceImpl<DocumentFile, Long, IDocumentFileDao>  implements IDocumentFileService {
	
	@Value("${wexpenses.documents.root}")
	private String urlFormat;
	
	@Autowired
	public DocumentFileService(IDocumentFileDao dao) {
		super(DocumentFile.class, dao);
	}

	@Override
	public DocumentFile findByFileName(String name) {
		return getDao().findByFileName(name);
	}
		
	@Override
	public String generateFilename(LocalDate fileDate, Expense x) {
		String payeeText = x.getPayee()==null?"unknown":(x.getPayee().getPrefix()==null?x.getPayee().getName():x.getPayee().getPrefix()+x.getPayee().getName()).replaceAll("\\s+", "_");
		String fileName = MessageFormat.format(
				"{0,date,yyyyMMdd} {1,number,0.00}{2} {3}.{4}.pdf",
				Date.from(fileDate.atStartOfDay(ZoneId.systemDefault()).toInstant()), 
				x.getCurrencyAmount(), 
				x.getCurrencyCode(), 
				payeeText,
				x.getUid().substring(x.getUid().length()-4));
		return fileName;
	}

	@Override
	public String getUrl(DocumentFile docFile) {
		return MessageFormat.format(urlFormat, Date.from(docFile.getDocumentDate().atStartOfDay(ZoneId.systemDefault()).toInstant()) , docFile.getFileName());
	}
}
