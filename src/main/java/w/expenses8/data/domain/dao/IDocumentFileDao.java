package w.expenses8.data.domain.dao;

import w.expenses8.data.core.dao.IGenericDao;
import w.expenses8.data.core.dao.IUidableDao;
import w.expenses8.data.domain.model.DocumentFile;

public interface IDocumentFileDao extends IGenericDao<DocumentFile, Long>, IUidableDao<DocumentFile> {
	
	DocumentFile findByFileName(String fileName);
}
