package w.expenses8.data.domain.service;

import java.time.LocalDate;
import java.util.List;

import w.expenses8.data.core.service.IGenericService;
import w.expenses8.data.domain.model.Payee;
import w.expenses8.data.domain.model.Tag;
import w.expenses8.data.domain.model.enums.TagType;

public interface ITagService extends IGenericService<Tag, Long> {

	Tag findByName(String name);
	
	List<Tag> findByText(String text);

	List<Tag> findByType(TagType type);

	List<Tag> findByInstitution(Payee institution);
	
	Tag getConsolidationTag(LocalDate d);
}
