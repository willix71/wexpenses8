package w.expenses8.web.controller;

import java.time.LocalDate;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import w.expenses8.data.core.model.DBable;
import w.expenses8.data.domain.model.DocumentFile;
import w.expenses8.data.domain.service.IDocumentFileService;
import w.expenses8.data.utils.StringHelper;

@Slf4j
@Named
@ViewScoped
@Getter @Setter
public class DocumentFileSelector {

	@Inject
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private IDocumentFileService documentFileService;
	
	private DocumentFile documentFile;
	
	private String documentUid;
	
	private LocalDate documentDate;
	
	private String fileName;
	
	private DBable<?> source;
	
	private int activeTabIndex = 0;

	public void reset(DBable<?> source, DocumentFile currentDocumentFile) {
		this.source = source;
		this.documentFile = currentDocumentFile;
		this.documentUid =  currentDocumentFile==null?null:currentDocumentFile.getUid();
		this.documentDate = currentDocumentFile==null?null:currentDocumentFile.getDocumentDate();
		this.fileName = currentDocumentFile==null?null:currentDocumentFile.getFileName();
	}
	
	public DocumentFile getCurrentDocumentFile() {
		if (activeTabIndex == 0) {
			if (documentFile !=null && documentUid != null && documentUid.equals(documentFile.getUid())) {
				// same document file instance
				documentFile.setDocumentDate(documentDate);
				documentFile.setFileName(fileName);				
			} else if (documentDate != null || !StringHelper.isEmpty(fileName)) {
				return new DocumentFile(documentDate, fileName);
			}
		}
		return documentFile;
	}

	public void onTabChange(TabChangeEvent<?> event) {
	    log.trace("Tab has changed to {}", activeTabIndex);
	}
	
	public void onDocumentFileSelection(SelectEvent<?> event) {
		log.trace("Tab has changed to {}", activeTabIndex);
	}
	
	public void onDocumentDateChange(SelectEvent<LocalDate> event) {
		updateFileName();
	}
	
	public void updateFileName() {
		if (documentDate!=null && (documentFile == null || StringHelper.isEmpty(fileName))) {
			log.info("Generating document filename");
			fileName = documentFileService.generateFilename(documentDate, source);
		}	
	}
	
	public String getDocumentFileUrl() {
		return activeTabIndex == 0?documentFileService.getUrl(documentDate, fileName):documentFileService.getUrl(documentFile);
	}
	
	public String getDocumentFileUrlFor(DocumentFile docFile) {
		return documentFileService.getUrl(docFile);
	}
}
