package w.expenses8.data.domain.service;

import java.util.List;

import w.expenses8.data.core.service.IGenericService;
import w.expenses8.data.domain.model.TagGroup;

public interface ITagGroupService extends IGenericService<TagGroup, Long> {

	TagGroup findByName(String name);
	
	List<TagGroup> findByText(String text);
}
