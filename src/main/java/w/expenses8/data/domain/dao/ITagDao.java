package w.expenses8.data.domain.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import w.expenses8.data.core.dao.IGenericDao;
import w.expenses8.data.core.dao.IUidableDao;
import w.expenses8.data.domain.model.Tag;
import w.expenses8.data.domain.model.enums.TagType;

public interface ITagDao extends IGenericDao<Tag, Long>, IUidableDao<Tag> {

	@Override
	@Query("from Tag t left join fetch t.institution")
	List<Tag> findAll();
	
	@Query("from Tag t left join fetch t.institution where t.name = ?1")
	Tag findByName(String name);
	
	@Query("from Tag t where lower(t.name) like ?1 or lower(t.number) like ?1")
	List<Tag> findByText(String like);
	
	@Query("from Tag t left join fetch t.institution where t.type = ?1")
	List<Tag> findByType(TagType type);

}
