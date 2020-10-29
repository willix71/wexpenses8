package w.expenses8.data.domain.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import w.expenses8.data.core.dao.IGenericDao;
import w.expenses8.data.core.dao.IUidableDao;
import w.expenses8.data.domain.model.DocumentFile;

public interface IDocumentFileDao extends IGenericDao<DocumentFile, Long>, IUidableDao<DocumentFile> {
	
	DocumentFile findByFileName(String fileName);
	
	@Query("select distinct d from DocumentFile d where lower(d.fileName) like ?1")
	List<DocumentFile> findByText(String text);
}
