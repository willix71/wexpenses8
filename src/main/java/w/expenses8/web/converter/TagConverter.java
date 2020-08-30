package w.expenses8.web.converter;

import javax.faces.convert.FacesConverter;
import javax.inject.Named;

import w.expenses8.data.domain.model.Tag;

@Named
@FacesConverter(value = "tagConverter", managed = true)
public class TagConverter extends DbableConverter<Tag> { }