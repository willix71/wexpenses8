package w.expenses8.data.dao.domain;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import w.expenses8.data.dao.core.IGenericDao;
import w.expenses8.data.dao.core.IUidableDao;
import w.expenses8.data.model.domain.Tag;
import w.expenses8.data.model.enums.TagEnum;

public interface ITagDao extends IGenericDao<Tag, Long>, IUidableDao<Tag> {

	Tag findByName(String name);

	@Query("from Tag t where t.name like ?1 or t.number like ?1")
	List<Tag> findByText(String like);
	
	List<Tag> findByType(TagEnum type);

}
