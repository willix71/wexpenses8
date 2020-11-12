package w.expenses8.data.domain.service.impl;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import w.expenses8.data.config.CurrencyValue;
import w.expenses8.data.core.model.DBable;
import w.expenses8.data.core.service.GenericServiceImpl;
import w.expenses8.data.domain.dao.IDocumentFileDao;
import w.expenses8.data.domain.model.Consolidation;
import w.expenses8.data.domain.model.DocumentFile;
import w.expenses8.data.domain.model.Expense;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.service.IDocumentFileService;
import w.expenses8.data.utils.CriteriaHelper;

@Service
public class DocumentFileService extends GenericServiceImpl<DocumentFile, Long, IDocumentFileDao>  implements IDocumentFileService {
	
	@Value("${wexpenses.documents.url}")
	private String urlFormat;
	
	@Inject
	private CurrencyValue defaultCurrency;
	
	@Autowired
	public DocumentFileService(IDocumentFileDao dao) {
		super(DocumentFile.class, dao);
	}

	@Override
	public DocumentFile findByFileName(String name) {
		return getDao().findByFileName(name);
	}
		
	@Override
	public List<DocumentFile> findByText(String text) {
		return getDao().findByText(CriteriaHelper.safeLowerLike(text));
	}
	
	@Override
	public String generateFilename(LocalDate fileDate, DBable<?> x) {
		if (x instanceof Expense) return generateFilename(fileDate, (Expense) x);
		if (x instanceof Consolidation) return generateFilename(fileDate, (Consolidation) x);
		return null;
	}

	public String generateFilename(LocalDate fileDate, Expense x) {
		return generateFilename(fileDate, x.getPayee(), x.getCurrencyAmount(), x.getCurrencyCode(), x) ;
	}

	public String generateFilename(LocalDate fileDate, Consolidation x) {
		return generateFilename(fileDate, x.getInstitution(), x.getClosingValue(), defaultCurrency.getCode(), x) ;
	}
	
	public String generateFilename(LocalDate fileDate, Payee payee, BigDecimal amount, String currency, DBable<?> x) {
		String payeeText = payee==null?"unknown":(payee.getPrefix()==null?payee.getName():payee.getPrefix()+payee.getName()).replaceAll("\\s+", "_");
		String fileName = MessageFormat.format(
				"{0,date,yyyyMMdd} {1,number,0.00}{2} {3}.{4}.pdf",
				Date.from(fileDate.atStartOfDay(ZoneId.systemDefault()).toInstant()), 
				amount, 
				currency, 
				payeeText,
				x.getUid().substring(x.getUid().length()-4));
		return fileName;
	}

	@Override
	public String getUrl(DocumentFile docFile) {
		return docFile==null?getUrl(null,null):getUrl(docFile.getDocumentDate(),docFile.getFileName());
	}

	@Override
	public String getUrl(LocalDate documentDate, String filename) {
		if (documentDate==null || filename==null) {
			return MessageFormat.format(urlFormat, new Date() , "");
		} else {
			return MessageFormat.format(urlFormat, Date.from(documentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) , filename);
		}
	}
}
