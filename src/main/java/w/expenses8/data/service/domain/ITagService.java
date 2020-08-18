package w.expenses8.data.service.domain;

import java.util.List;

import w.expenses8.data.model.domain.Tag;
import w.expenses8.data.model.enums.TagEnum;
import w.expenses8.data.service.core.IGenericService;

public interface ITagService extends IGenericService<Tag, Long> {

	Tag findByName(String name);
	
	List<Tag> findByText(String text);

	List<Tag> findByType(TagEnum type);
}
