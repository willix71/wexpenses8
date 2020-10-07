package w.expenses8.data.domain.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.expenses8.data.core.service.GenericServiceImpl;
import w.expenses8.data.domain.dao.ITagGroupDao;
import w.expenses8.data.domain.model.TagGroup;
import w.expenses8.data.domain.service.ITagGroupService;
import w.expenses8.data.utils.CriteriaHelper;

@Service
public class TagGroupService extends GenericServiceImpl<TagGroup, Long, ITagGroupDao> implements ITagGroupService {

	@Autowired
	public TagGroupService(ITagGroupDao dao) {
		super(TagGroup.class, dao);
	}

	@Override
	public TagGroup findByName(String name) {
		return getDao().findByName(name);
	}
	
	@Override
	public List<TagGroup> findByText(String text) {
		return getDao().findByText(CriteriaHelper.safeLowerLike(text));
	}
	
}