package w.expenses8.web.converter;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;

import w.expenses8.data.domain.criteria.TagCriteria;
import w.expenses8.data.domain.model.Tag;
import w.expenses8.data.domain.model.TagGroup;
import w.expenses8.data.domain.model.enums.TagType;
import w.expenses8.data.domain.service.ITagGroupService;
import w.expenses8.data.domain.service.ITagService;

@Named
@FacesConverter(value = "tagCriteriaConverter", managed = true)
public class TagCriteriaConverter implements Converter<TagCriteria> {

	@Inject
	private ITagService tagService;

	@Inject
	private ITagGroupService tagGroupService;

	@Override
	public TagCriteria getAsObject(FacesContext fc, UIComponent uic, String uid) {
		if (uid != null && uid.trim().length() > 0) {
			try {
				switch(uid.charAt(0)) {
				case 't':
					return tagService.loadByUid(uid.substring(1));
				case 'g':
					return tagGroupService.loadByUid(uid.substring(1));
				case 'y':
					return TagType.valueOf(uid.substring(1));
				}
			} catch (NumberFormatException e) {
				throw new ConverterException(
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid expense type."));
			}
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext fc, UIComponent uic, TagCriteria object) {
		if (object != null) {
			if (object instanceof Tag)
				return "t" + ((Tag)object).getUid();
			if (object instanceof TagGroup)
				return "g" + ((TagGroup)object).getUid();
			if (object instanceof TagType)
				return "y" + ((TagType)object).name();
		}
		return null;
	}
}