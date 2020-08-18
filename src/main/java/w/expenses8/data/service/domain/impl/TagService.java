package w.expenses8.data.service.domain.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import w.expenses8.data.dao.domain.ITagDao;
import w.expenses8.data.model.domain.Tag;
import w.expenses8.data.model.enums.TagEnum;
import w.expenses8.data.service.core.GenericServiceImpl;
import w.expenses8.data.service.domain.ITagService;

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
		return getDao().findByText(like(text));
	}
	
	@Override
	public List<Tag> findByType(TagEnum type) {
		return getDao().findByType(type);
	}
}