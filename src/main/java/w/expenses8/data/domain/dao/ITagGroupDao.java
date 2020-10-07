package w.expenses8.data.domain.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import w.expenses8.data.core.dao.IGenericDao;
import w.expenses8.data.core.dao.IUidableDao;
import w.expenses8.data.domain.model.TagGroup;

public interface ITagGroupDao extends IGenericDao<TagGroup, Long>, IUidableDao<TagGroup> {

	@Override
	@Query("select distinct g from TagGroup g left join fetch g.tags")
	List<TagGroup> findAll();
	
	@Query("from TagGroup g left join fetch g.tags where g.name = ?1")
	TagGroup findByName(String name);
	
	@Query("select distinct g from TagGroup g left join fetch g.tags where lower(g.name) like ?1")
	List<TagGroup> findByText(String like);
}
