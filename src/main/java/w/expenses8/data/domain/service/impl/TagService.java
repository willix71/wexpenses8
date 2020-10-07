package w.expenses8.data.domain.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.expenses8.data.core.service.GenericServiceImpl;
import w.expenses8.data.domain.dao.ITagDao;
import w.expenses8.data.domain.model.Tag;
import w.expenses8.data.domain.model.enums.TagType;
import w.expenses8.data.domain.service.ITagService;
import w.expenses8.data.utils.CriteriaHelper;

@Service
public class TagService extends GenericServiceImpl<Tag, Long, ITagDao> implements ITagService {

	@Autowired
	public TagService(ITagDao dao) {
		super(Tag.class, dao);
	}

	@Override
	public Tag findByName(String name) {
		return getDao().findByName(name);
	}

	@Override
	public List<Tag> findByText(String text) {
		return getDao().findByText(CriteriaHelper.safeLowerLike(text));
	}
	
	@Override
	public List<Tag> findByType(TagType type) {
		return getDao().findByType(type);
	}
}