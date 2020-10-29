package w.expenses8.web.converter;

import javax.faces.convert.FacesConverter;
import javax.inject.Named;

import w.expenses8.data.domain.model.DocumentFile;

@Named
@FacesConverter(value = "documentFileConverter", managed = true)
public class DocumentFileConverter extends DbableConverter<DocumentFile> { }