package w.expenses8.web.controller;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.core.model.DBable;
import w.expenses8.data.domain.model.DocumentFile;
import w.expenses8.data.domain.service.IDocumentFileService;

@Slf4j
@Named
@ApplicationScoped
public class ServiceController {

	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private IDocumentFileService documentFileService;
	
	public void onDocumentFileDateChange(DocumentFile f, DBable<?> o) {
		if (f.isNew() || f.getFileName()==null) {
			// generate new filename
			log.info("Generating document filename");
			String fileName = documentFileService.generateFilename(f.getDocumentDate(), o);
			f.setFileName(fileName);
		}		
	}
	
	public String getDocumentFileUrl(DocumentFile docFile) {
		return documentFileService.getUrl(docFile);
	}
}
